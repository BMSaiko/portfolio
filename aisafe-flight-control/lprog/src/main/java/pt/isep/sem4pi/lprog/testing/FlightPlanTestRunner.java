package pt.isep.sem4pi.lprog.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Launches the C {@code flight_tester} executable as a child process and
 * communicates with it via stdin/stdout using the JSON protocol.
 *
 * <h3>Executable resolution (in order)</h3>
 * <ol>
 *   <li>System property {@code flight.tester.path} — set via VM options or
 *       Maven Surefire config. Used as-is (no .exe appended).</li>
 *   <li>Default path relative to the module root, with {@code .exe} appended
 *       automatically on Windows.</li>
 * </ol>
 */
public class FlightPlanTestRunner {

    public static final String EXECUTABLE_PATH_PROPERTY = "flight.tester.path";

    private static final String DEFAULT_EXECUTABLE_PATH =
            "src/main/c/flight_tester/bin/flight_tester";

    private static final int TIMEOUT_SECONDS = 10;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    public FlightPlanTestResult run(FlightPlanTestInput input) {
        Path executablePath = resolveExecutable();

        if (!Files.isExecutable(executablePath)) {
            return FlightPlanTestResult.executionError(
                    "C tester not found or not executable at: "
                            + executablePath.toAbsolutePath());
        }

        String jsonIn = input.toJson();

        try {
            ProcessBuilder pb = new ProcessBuilder(executablePath.toString());
            pb.redirectErrorStream(false);

            Process process = pb.start();

            // Write JSON to stdin then close the stream
            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.print(jsonIn);
            }

            // Read stdout (the JSON result)
            StringBuilder stdoutBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stdoutBuilder.append(line);
                }
            }

            // Read stderr (diagnostic only)
            StringBuilder stderrBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stderrBuilder.append(line).append('\n');
                }
            }

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return FlightPlanTestResult.executionError(
                        "C tester timed out after " + TIMEOUT_SECONDS + " seconds.");
            }

            int exitCode = process.exitValue();

            if (exitCode == 2) {
                return FlightPlanTestResult.executionError(
                        "C tester reported input parse error: "
                                + stderrBuilder.toString().trim());
            }

            String stdout = stdoutBuilder.toString().trim();
            if (stdout.isEmpty()) {
                return FlightPlanTestResult.executionError(
                        "C tester produced no output. stderr: "
                                + stderrBuilder.toString().trim());
            }

            return parseResult(stdout);

        } catch (IOException e) {
            return FlightPlanTestResult.executionError(
                    "Failed to launch C tester: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return FlightPlanTestResult.executionError(
                    "Interrupted while waiting for C tester.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Executable resolution
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Resolves the path to the flight_tester executable.
     *
     * Priority:
     *   1. System property {@code flight.tester.path}  — used exactly as given.
     *   2. Default relative path, with {@code .exe} appended on Windows.
     */
    static Path resolveExecutable() {
        String override = System.getProperty(EXECUTABLE_PATH_PROPERTY);
        if (override != null && !override.isBlank()) {
            Path overridePath = Paths.get(override);
            // If the given path exists as-is, use it
            if (Files.exists(overridePath)) return overridePath;

            // On Windows the user may have omitted .exe — try appending it
            if (isWindows()) {
                Path withExe = Paths.get(override + ".exe");
                if (Files.exists(withExe)) return withExe;
            }
            // Return as-is even if not found — the caller will report the error
            return overridePath;
        }

        // Default path
        String defaultPath = DEFAULT_EXECUTABLE_PATH + (isWindows() ? ".exe" : "");
        return Paths.get(defaultPath);
    }

    static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JSON output parser
    // ─────────────────────────────────────────────────────────────────────────

    static FlightPlanTestResult parseResult(String json) {
        try {
            boolean passed = extractBoolField(json, "passed");

            if (passed) {
                return FlightPlanTestResult.passed();
            }

            List<PhysicalIssue> issues = extractIssues(json);
            if (issues.isEmpty()) {
                return FlightPlanTestResult.executionError(
                        "C tester reported failure but provided no issues. Raw: " + json);
            }
            return FlightPlanTestResult.failed(issues);

        } catch (Exception e) {
            return FlightPlanTestResult.executionError(
                    "Could not parse C tester output: " + e.getMessage()
                            + ". Raw: " + json);
        }
    }

    private static boolean extractBoolField(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) throw new IllegalArgumentException("Key not found: " + key);
        int colon = json.indexOf(':', idx + search.length());
        String rest = json.substring(colon + 1).stripLeading();
        if (rest.startsWith("true"))  return true;
        if (rest.startsWith("false")) return false;
        throw new IllegalArgumentException("Expected boolean for key: " + key);
    }

    private static List<PhysicalIssue> extractIssues(String json) {
        List<PhysicalIssue> issues = new ArrayList<>();

        int issuesStart = json.indexOf("\"issues\"");
        if (issuesStart < 0) return issues;

        int arrayOpen  = json.indexOf('[', issuesStart);
        int arrayClose = json.lastIndexOf(']');
        if (arrayOpen < 0 || arrayClose < 0) return issues;

        String array = json.substring(arrayOpen + 1, arrayClose);

        int depth    = 0;
        int objStart = -1;
        for (int i = 0; i < array.length(); i++) {
            char c = array.charAt(i);
            if (c == '{') {
                if (depth == 0) objStart = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && objStart >= 0) {
                    String obj     = array.substring(objStart, i + 1);
                    String field   = extractStringField(obj, "field");
                    String message = extractStringField(obj, "message");
                    if (field != null && message != null) {
                        issues.add(new PhysicalIssue(field, message));
                    }
                    objStart = -1;
                }
            }
        }
        return issues;
    }

    private static String extractStringField(String json, String key) {
        String search   = "\"" + key + "\"";
        int idx         = json.indexOf(search);
        if (idx < 0) return null;
        int colon       = json.indexOf(':', idx + search.length());
        int quoteOpen   = json.indexOf('"', colon + 1);
        if (quoteOpen < 0) return null;
        StringBuilder sb = new StringBuilder();
        int i = quoteOpen + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"'  -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case 'n'  -> sb.append('\n');
                    case 'r'  -> sb.append('\r');
                    case 't'  -> sb.append('\t');
                    default   -> sb.append(next);
                }
                i += 2;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}