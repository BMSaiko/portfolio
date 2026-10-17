package com.example.production.Utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for managing repository paths, specifically related to finding the root directory of a Git repository.
 */
public class RepPathUtils {

    private static Path repBasePath = null;

    /**
     * Gets the base path of the repository by locating the .git directory.
     *
     * @return The base path of the Git repository.
     * @throws IllegalStateException If the .git directory cannot be found.
     */
    public static Path getRepBasePath() {
        if (repBasePath == null) {
            Path currentDir = Paths.get("").toAbsolutePath();
            Path gitRoot = findGitRoot(currentDir);

            if (gitRoot != null) {
                repBasePath = gitRoot;
                System.out.println("Repository base directory found: " + repBasePath.toString());
            } else {
                throw new IllegalStateException("Could not find the .git directory.");
            }
        }
        return repBasePath;
    }

    /**
     * Recursively searches for the Git root directory by moving up the directory structure.
     *
     * @param currentDir The current directory to start the search from.
     * @return The path to the Git root directory, or null if not found.
     */
    private static Path findGitRoot(Path currentDir) {
        File dir = currentDir.toFile();

        File gitDir = new File(dir, ".git");
        if (gitDir.exists() && gitDir.isDirectory()) {
            return currentDir;
        } else {
            if (currentDir.getParent() == null) {
                return null; // .git not found
            }
            return findGitRoot(currentDir.getParent()); // Move up one level
        }
    }

    /**
     * Resolves a path relative to the repository base path.
     *
     * @param relativePath The relative path from the repository root.
     * @return The resolved absolute path.
     */
    public static Path getPathRelativeToRep(String relativePath) {
        return getRepBasePath().resolve(relativePath).normalize();
    }

    /**
     * Gets the absolute path for a given relative path from the repository root.
     *
     * @param relativePath The relative path from the repository root.
     * @return The absolute path as a string.
     */
    public static String getAbsolutePath(String relativePath) {
        return getPathRelativeToRep(relativePath).toString();
    }
}
