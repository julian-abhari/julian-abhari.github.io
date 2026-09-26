import fs from "node:fs";
import path from "node:path";
import matter from "gray-matter";
import { marked } from "marked";
import {
  CATEGORIES,
  schemaByCategory,
  type Category,
  type ContentEntry,
  type RenderedContentEntry,
} from "../src/content/schema";

const CONTENT_DIR = path.resolve(__dirname, "../content");
const OUTPUT_DIR = path.resolve(__dirname, "../src/generated");
const OUTPUT_FILE = path.join(OUTPUT_DIR, "content.ts");
const CROSS_LINK_PATTERN = /\[\[([a-zA-Z0-9-]+)\]\]/g;

type FileErrors = { file: string; errors: string[] };

function loadTags(): Set<string> {
  const raw = fs.readFileSync(path.join(CONTENT_DIR, "tags.json"), "utf-8");
  const tags = JSON.parse(raw) as unknown;
  if (!Array.isArray(tags) || !tags.every((t) => typeof t === "string")) {
    throw new Error("content/tags.json must be a JSON array of strings");
  }
  return new Set(tags);
}

function collectMarkdownFiles(): { category: Category; file: string }[] {
  const found: { category: Category; file: string }[] = [];
  for (const category of CATEGORIES) {
    const dir = path.join(CONTENT_DIR, category);
    if (!fs.existsSync(dir)) continue;
    for (const name of fs.readdirSync(dir)) {
      if (!name.endsWith(".md")) continue;
      found.push({ category, file: path.join(dir, name) });
    }
  }
  return found;
}

function relative(file: string): string {
  return path.relative(path.resolve(__dirname, ".."), file);
}

function main() {
  const fileErrors: FileErrors[] = [];
  const tags = loadTags();
  const files = collectMarkdownFiles();

  const parsed: { relPath: string; category: Category; frontmatter: ContentEntry; body: string }[] = [];

  for (const { category, file } of files) {
    const errors: string[] = [];
    const relPath = relative(file);
    const raw = fs.readFileSync(file, "utf-8");
    const { data, content: body } = matter(raw);

    const expectedId = path.basename(file, ".md");
    if (data.id !== expectedId) {
      errors.push(`frontmatter id "${data.id}" does not match filename "${expectedId}"`);
    }
    if (data.category !== category) {
      errors.push(`frontmatter category "${data.category}" does not match directory "${category}"`);
    }

    const schema = schemaByCategory[category];
    const result = schema.safeParse(data);
    if (!result.success) {
      for (const issue of result.error.issues) {
        const fieldPath = issue.path.join(".") || "(root)";
        errors.push(`${fieldPath}: ${issue.message}`);
      }
    } else {
      const entryTags = result.data.tags;
      for (const tag of entryTags) {
        if (!tags.has(tag)) {
          errors.push(`tag "${tag}" is not present in content/tags.json`);
        }
      }
      parsed.push({ relPath, category, frontmatter: result.data, body });
    }

    if (errors.length > 0) {
      fileErrors.push({ file: relPath, errors });
    }
  }

  // Global id uniqueness.
  const idToFiles = new Map<string, string[]>();
  for (const { relPath, frontmatter } of parsed) {
    const list = idToFiles.get(frontmatter.id) ?? [];
    list.push(relPath);
    idToFiles.set(frontmatter.id, list);
  }
  for (const [id, filesForId] of idToFiles) {
    if (filesForId.length > 1) {
      for (const relPath of filesForId) {
        fileErrors.push({
          file: relPath,
          errors: [`duplicate id "${id}" also used by ${filesForId.filter((f) => f !== relPath).join(", ")}`],
        });
      }
    }
  }

  const idToTitle = new Map(parsed.map((p) => [p.frontmatter.id, p.frontmatter.title]));

  // Cross-link validation + resolution. Resolved into an anchor carrying a
  // data-entry-id — there's no per-entry route in this statically-exported,
  // single-page app, so ContentPanel intercepts clicks on these in-place.
  const rendered: RenderedContentEntry[] = [];
  for (const { relPath, frontmatter, body } of parsed) {
    const errors: string[] = [];
    const resolvedBody = body.replace(CROSS_LINK_PATTERN, (_match, refId: string) => {
      if (!idToTitle.has(refId)) {
        errors.push(`cross-link [[${refId}]] does not match any known entry id`);
        return _match;
      }
      return `<a href="#" data-entry-id="${refId}">${idToTitle.get(refId)}</a>`;
    });

    if (errors.length > 0) {
      fileErrors.push({ file: relPath, errors });
      continue;
    }

    const html = marked.parse(resolvedBody, { async: false }) as string;
    rendered.push({ ...frontmatter, html });
  }

  if (fileErrors.length > 0) {
    console.error(`\nContent validation failed in ${fileErrors.length} file(s):\n`);
    for (const { file, errors } of fileErrors) {
      console.error(`  ${file}`);
      for (const error of errors) {
        console.error(`    - ${error}`);
      }
    }
    console.error("");
    process.exit(1);
  }

  fs.mkdirSync(OUTPUT_DIR, { recursive: true });

  const entriesByCategory = Object.fromEntries(
    CATEGORIES.map((category) => [category, rendered.filter((e) => e.category === category)]),
  );

  const output = `// Generated by scripts/generate-content.ts — do not edit by hand.
import type { RenderedContentEntry, Category } from "../content/schema";

export const allEntries: RenderedContentEntry[] = ${JSON.stringify(rendered, null, 2)};

export const entriesById: Record<string, RenderedContentEntry> = ${JSON.stringify(
    Object.fromEntries(rendered.map((e) => [e.id, e])),
    null,
    2,
  )};

export const entriesByCategory: Record<Category, RenderedContentEntry[]> = ${JSON.stringify(
    entriesByCategory,
    null,
    2,
  )};
`;

  fs.writeFileSync(OUTPUT_FILE, output);
  console.log(`Generated ${OUTPUT_FILE} with ${rendered.length} entries.`);
}

main();
