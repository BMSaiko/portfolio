// Extrai cada bloco ```mermaid dos .md e renderiza com mmdc; falha se algum não compilar.
import { readdirSync, readFileSync, writeFileSync, mkdirSync, rmSync, statSync } from "node:fs";
import { join } from "node:path";
import { spawnSync } from "node:child_process";
import { tmpdir } from "node:os";

const SKIP = new Set([".git", "node_modules"]);
const mdFiles = [];
(function walk(dir) {
  for (const name of readdirSync(dir)) {
    if (SKIP.has(name)) continue;
    const p = join(dir, name);
    if (statSync(p).isDirectory()) walk(p);
    else if (name.endsWith(".md")) mdFiles.push(p);
  }
})(".");

const blocks = [];
for (const file of mdFiles) {
  const text = readFileSync(file, "utf8").replace(/\r\n/g, "\n");
  const re = /^```mermaid[^\n]*\n([\s\S]*?)^```/gm;
  let m;
  while ((m = re.exec(text))) {
    const line = text.slice(0, m.index).split("\n").length;
    blocks.push({ file, line, code: m[1] });
  }
}

console.log(`${blocks.length} blocos Mermaid em ${new Set(blocks.map((b) => b.file)).size} ficheiros`);
if (process.argv.includes("--list")) process.exit(0);

const work = join(tmpdir(), `mermaid-check-${process.pid}`);
mkdirSync(work, { recursive: true });
const cfg = join(work, "puppeteer.json");
writeFileSync(cfg, JSON.stringify({ args: ["--no-sandbox", "--disable-setuid-sandbox"] }));

let failed = 0;
blocks.forEach((b, i) => {
  const input = join(work, `b${i}.mmd`);
  writeFileSync(input, b.code);
  const r = spawnSync("npx", ["--no-install", "mmdc", "-i", input, "-o", join(work, `b${i}.svg`), "-p", cfg, "-q"], {
    encoding: "utf8",
    shell: process.platform === "win32",
  });
  if (r.status !== 0) {
    failed++;
    console.log(`::error file=${b.file},line=${b.line}::Mermaid inválido`);
    console.log(`FALHA ${b.file}:${b.line}\n${(r.stderr || r.stdout || "").trim().split("\n").slice(0, 6).join("\n")}`);
  }
});
rmSync(work, { recursive: true, force: true });
if (failed) {
  console.log(`${failed}/${blocks.length} blocos inválidos`);
  process.exit(1);
}
console.log("Todos os blocos Mermaid compilam");
