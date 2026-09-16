# World Content & Cataloguing Plan

This plan covers two things:

1. **Cataloguing** — a system for recording every job, research project, publication, patent, award, and personal project in a consistent, structured way, independent of the game engine.
2. **World content** — how those catalogued entries become physical exhibits in the lab, and what needs to be built (engine + React) to make that work.

The guiding principle: **content and world design should be decoupled.** Adding a new job or paper to your career should never require touching Java, level art, or tile coordinates. It should mean adding one file to a `content/` directory. The mapping from "entry" to "physical spot in the lab" is a thin, separate layer on top.

---

## 1. The Cataloguing System

### 1.1 Categories

Matches the README's existing Sections list, with one addition proposed:

- **About Me** (not catalogued — it's the hub/intro, handled separately)
- **Experience**
  - **Work** — paid employment
  - **Research** — formal/academic research roles
  - **Projects** *(new — see below)* — personal/side projects that aren't employment or formal research (you have a long list of these: creative-coding sketches, simulations, game engines, etc.)
- **Education**
- **Publications**
- **Patents**
- **Awards and Honors**

> **Decided:** Projects is confirmed as a third Experience subsection.

### 1.2 Directory structure

```
content/
  work/
    <kebab-case-id>.md
  research/
    <kebab-case-id>.md
  projects/
    <kebab-case-id>.md
  education/
    <kebab-case-id>.md
  publications/
    <kebab-case-id>.md
  patents/
    <kebab-case-id>.md
  awards/
    <kebab-case-id>.md
  tags.json          # controlled vocabulary of skill/technology tags
  exhibit-map.json    # entry id -> in-world exhibit location (see §3.3)
```

One file per entry. Markdown with YAML frontmatter — human-editable, diffable in git, no database needed.

### 1.3 Common schema (every entry, regardless of category)

```yaml
---
id: skincheck-cofounder                # stable slug, used everywhere (exhibit map, links, tags)
category: work                         # work | research | projects | education | publications | patents | awards
title: "Co-Founder & Technical Lead"
organization: "SkinCheck"
startDate: 2024-01                     # YYYY-MM
endDate: null                          # null = ongoing
location: "Remote"
tags: [teavm, nextjs, computer-vision, dermatology]
summary: "One or two sentences — this is what shows on the exhibit's small label / list view."
featured: false                        # true = gets a hand-placed physical exhibit; false = archive-terminal only (see §3.3)
links:
  - label: "Company site"
    url: "https://skincheck.health"
media: []                               # optional image paths for the content panel
---

Full description in markdown body. This is what renders in the content panel
when the exhibit is interacted with. Can be as long as you want — length only
matters for the physical exhibit's label text, not this body.
```

### 1.4 Category-specific extra fields

| Category | Extra fields |
|---|---|
| Work | `role`, `employmentType` (full-time / contract / advisor) |
| Research | `institution`, `advisor` or `lab` |
| Projects | `techStack` (array), `repoUrl`, `demoUrl` |
| Education | `institution`, `degree`, `field`, `gpa` (optional) |
| Publications | `authors` (array), `venue`, `doi` or `link`, `publicationDate` |
| Patents | `patentNumber`, `status` (filed / granted), `inventors` (array) |
| Awards | `awardingBody`, `dateReceived` |

### 1.5 Tags

`content/tags.json` is the single controlled vocabulary — a flat list of every tag string that's allowed to be used (e.g. `["nextjs", "teavm", "java", "computer-vision", "genetic-algorithms", ...]`). When adding an entry, reuse an existing tag if one fits before inventing a new one. This is what makes cross-linking possible later ("show everything tagged `computer-vision`" spans Work, Research, and Projects at once) — it only works if the same concept is always spelled the same way.

### 1.6 Adding a new entry — the actual workflow

1. Pick the category, create `content/<category>/<id>.md`.
2. Fill in the common frontmatter + that category's extra fields.
3. Check `tags.json` before adding new tags; add any genuinely new ones there too.
4. Leave `featured: false` unless you've already decided this entry gets a hand-placed physical exhibit (see §3.3) — that's a level-design decision, not a content one, and can be flipped later without re-writing the entry.
5. Run the dev server and confirm it shows up (once §4's content pipeline exists, this becomes an actual visible checklist item — right now it's aspirational until that's built).
6. Commit.

That's the entire ongoing maintenance loop once the rest of this plan is built — no Java, no level art, no tile coordinates, ever, for a routine new entry.

---

## 2. World Mapping — Rooms, Exhibits, and the Hub

### 2.1 Structure

- **Hub (spawn room)** — "About Me" + Contact. This is where the viewer wakes up as the velociraptor; the in-world controls sign/terminal lives here too (per README's Controls section).
- **One wing per Experience/Education/Publications/Patents/Awards section**, themed distinctly (different tile palette/props per wing) so the viewer always has a sense of "which section am I in" without needing a UI label.

### 2.2 Exhibit types (thematic, matches the abandoned-lab-in-the-Cretaceous setting)

Pick a small, reusable vocabulary of exhibit "props" rather than one-off art per entry — this is nothing more than a set of reusable tiles, no unique asset per entry:

- **Specimen jar / terrarium** — Projects (things you grew/built)
- **Research terminal / whiteboard** — Research, Publications
- **Filing cabinet / vault door** — Patents
- **Trophy case** — Awards and Honors
- **Personnel file / photo on a desk** — Work
- **Diploma on the wall** — Education

Each prop is a reusable tile/sprite the engine renders; which *entry* it points to is data (see §3.3), not a unique asset per entry. **Decided:** start by populating a tiny sample of these (enough to prove the pipeline), then expand the set as needed when adding more physical exhibits.

### 2.3 Scaling problem: finite level art vs. a career that keeps growing

A hand-drawn level PNG has a fixed number of tiles. Your catalogue will keep growing indefinitely.

**Decided: featured exhibits + an archive terminal per wing.** Hand-place a handful of physical exhibits for your most visually/narratively important entries (`featured: true`) in each wing. Additionally place **one "archive terminal" interactive tile per wing** that opens a scrollable list of *every* entry in that category (including the featured ones). This scales indefinitely — adding entry #47 to Work never requires new level art, it just appears in the Work wing's archive terminal automatically. You expect to keep adding "must have" featured exhibits as your career goes on, so this needs to stay easy to extend, not a one-time build.

---

## 3. Technical Build Plan

### 3.1 Content pipeline (Next.js side)

- A small build-time script (or a simple `contentlayer`-style loader) that reads every `content/**/*.md` file, parses frontmatter + body, validates against the schema in §1, and produces a single generated JSON/TS module the app imports (e.g. `src/generated/content.ts`). Keeps runtime code simple — no markdown parsing in the browser.
- Validate at build time (fail the build on a missing required field, a tag not in `tags.json`, a duplicate `id`, etc.) so bad content data is caught immediately, not discovered live on the deployed site.

### 3.2 React content panel

- One generic, schema-driven component (not one component per category) that takes an entry id, looks it up in the generated content module, and renders title/org/dates/tags/summary/body/links — styling can vary by category (via the `category` field) without needing separate components.
- Triggered the same way the current interact HUD works today, but see §3.3 — it needs an entry id, not just a boolean.

### 3.3 Extending the Java ↔ React bridge

Today, `window.__portfolioGame.setNearInteractive(isNear: boolean)` only knows "near *something* interactive" — enough for a generic "PRESS D TO INTERACT" prompt, not enough to know *which* entry's content to show. This needs to become identity-aware:

- New Java tile type, e.g. `ExhibitTile extends BasicInteractiveTile`, carrying a string `exhibitId` (matches an id in `content/`).
- `Player`/`WebMain`'s proximity tracking needs to surface *which* tile it's near (its `exhibitId`), not just a boolean.
- Bridge signature becomes something like `setNearExhibit(exhibitId: string | null)` — `null` when not near anything, replacing the current boolean. React uses a non-null id both to show the "press to interact" HUD *and*, on actual interact (D press / tap), to open the content panel for that id.
- `content/exhibit-map.json` is the data linking a physical `exhibitId` tile placement to a content entry — keeps level design (where things are) separate from content (what they say).

### 3.4 Level design

- **Decided:** one contiguous level PNG — hub plus all wings as regions of the same map, not separate level files per wing. Wing-to-wing transitions are walked, not door-cut. Follows the existing `Tile.tiles` registry pattern (`level/tiles/Tile.java`) — new tile colors for new `ExhibitTile` placements, same mechanism `DOOR_BOTTOM` already uses.
- Start with a basic proof of concept: greybox a single wing (a room with a few exhibit tiles and a terminal, no polish) to validate the exhibit → content → panel pipeline works end-to-end before investing in themed art or repeating across the other wings.

---

## 4. Suggested Build Order

1. Schema (§1, incl. Projects) and exhibit-prop vocabulary (§2.2) are confirmed.
2. Populate `content/` with real data — this is pure data entry, no code, and can start immediately/in parallel with everything else.
3. Build the content pipeline (§3.1) and generic content panel (§3.2) against that real data, independent of the game engine — this can be verified in isolation (e.g. a temporary debug route that lists all entries) before the exhibit/tile wiring exists.
4. Extend the Java bridge to be exhibit-id-aware (§3.3) and build one `ExhibitTile`.
5. Greybox a single wing (recommend **Education** — likely your smallest dataset) end-to-end: level tiles → exhibit-map entry → bridge → content panel. Prove the full loop once, here, before scaling out.
6. Repeat for the remaining wings (Work, Research, Projects, Publications, Patents, Awards) plus the hub/About+Contact room.
7. Add archive terminals per wing (§2.3) once there's more than a couple of entries in any category.
8. Polish pass: per-wing theming, signage, transitions.

---

## Decisions Log

- **Projects** is a confirmed third Experience subsection (§1.1).
- Exhibit-prop vocabulary (§2.2) confirmed as a small reusable tile set — populate a tiny sample first, expand as new physical exhibits get added.
- Featured exhibits + per-wing archive terminal (§2.3) confirmed as the scaling approach, expected to keep growing over time.
- One contiguous level PNG (§3.4), not separate files per wing. Build order starts with a single-wing greybox proof of concept to validate the exhibit → content → panel pipeline before scaling out.
