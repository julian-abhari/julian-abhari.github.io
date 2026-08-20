# Julian's Developer Portfolio V3
### A Cretaceous-Era Laboratory Built with Next.js and a Custom Java Game Engine

> Status: Early development — architecture and world-building in progress.

---

## Purpose

This isn't a scrolling list of bullet points. It's an interconnected world built to showcase who I am — the work, research, creations, and education I've experienced, the skills I've picked up along the way, and what I've been recognized for. Every section of a traditional resume has a physical place to explore here instead of a line to skim.

## Goals

1. **Be uniquely mine** — an interesting, artistic showcase, not a template.
2. **Streamline access** — anyone should be able to find any fact about me or my accomplishments quickly.
3. **Stay intuitive** — accessible to anyone, regardless of technical background or gaming experience.
4. **Run seamlessly everywhere** — full functionality and intuitive controls on any device.

## Tech Stack

- **[Next.js](https://nextjs.org/)** — renders the web app shell and handles routing, content, and the overall interactive experience.
- **Custom Java Game Engine** — a game engine written from scratch in Java, compiled to WebAssembly via **[TeaVM](https://teavm.org/)** and embedded directly in the Next.js app, powering the gamified world the viewer explores.

## The World

You wake up as a velociraptor inside an abandoned research laboratory, long since overtaken by an overgrown island. As you explore — room by room, exhibit by exhibit — you piece together what happened here: an experiment in time travel that went catastrophically wrong, hurling the lab, its contents, and everyone in it back to the Cretaceous era.

The lab's exhibits *are* the portfolio — its research logs, achievements, and artifacts map onto real experience, education, publications, and awards. As you dig deeper, one thread stays unresolved: what happened to the lab's creator. Whether he thrived or perished out here is left for the viewer to wonder.

## Sections

- **About Me**
  - Contact
- **Experience**
  - Work
  - Research
- **Education**
- **Publications**
- **Patents**
- **Awards and Honors**

## Controls

Movement is meant to feel intuitive immediately, reinforced by an in-world cue near the starting point (a sign, a terminal, or on-screen text) that spells out the controls before the viewer needs them.

| Platform | Movement | Interaction |
|---|---|---|
| **Mobile** | Tap anywhere to spawn a joystick at that x-position; drag to steer | Tap the highlighted object |
| **Desktop** | Keyboard + cursor to move left/right | Press the prompted key on the highlighted object |

When the viewer nears an interactible object, it highlights automatically and a stylized HUD appears at the bottom of the screen:
- **Mobile:** `TAP TO INTERACT`
- **Desktop:** `PRESS __ TO INTERACT`

## Local Development

```bash
npm run dev            # start the Next.js dev server at localhost:3000
npm run engine:build   # rebuild the Java engine (Gradle -> TeaVM -> wasm) and
                        # copy game.wasm / game.wasm-runtime.js into public/game/
```

The wasm output is a static asset, not part of Next.js's module graph — Next's dev
server serves everything under `public/` straight from disk, but it does **not**
watch or rebuild the Java side. The loop when working on the engine is:

1. Edit Java under `GameEngine/src/main/java/com/Julian/game/...`
2. `npm run engine:build`
3. Reload the browser tab (a real reload — the wasm module is re-fetched, not hot-reloaded)

## Deployment

Target: **https://julian-abhari.github.io/**

This is a GitHub **user page**, which comes with a hard constraint: it must be
served from a repo named *exactly* `julian-abhari.github.io`, from the domain
root — there's no subpath the way a project page gets
(`<user>.github.io/<repo>/`). That's why `next.config.ts` sets `output: "export"`
with no `basePath`/`assetPrefix`.

Because GitHub Pages only serves static files (no Node server, no API routes),
"deploying" means: compile the Java engine, statically export the Next.js app,
and publish the result. Whether that's run by hand or by a CI system, the steps
are the same:

1. **Checkout the repo.**
2. **Set up a JDK** (21+ — whatever `java -version` reports locally) and **Node**
   (20+). The Gradle wrapper (`GameEngine/gradlew`) bundles everything else it
   needs, so no system-wide Gradle/Maven install is required.
3. **Install JS dependencies:** `npm ci`
4. **Build the engine:** `npm run engine:build` — runs
   `./gradlew buildWasmGC` and copies `game.wasm` + `game.wasm-runtime.js` into
   `public/game/`. This must happen *before* the Next.js build, since static
   export just copies whatever is in `public/` verbatim.
5. **Static export:** `npm run build` — with `output: "export"` in
   `next.config.ts`, this produces a fully static site in `out/` (verified:
   `out/index.html`, `out/_next/`, `out/game/game.wasm`, and `out/.nojekyll`
   all land correctly).
6. **Publish `out/`.** GitHub Pages needs a branch to serve from, configured in
   the repo's Settings → Pages. Recommended split: keep source on `main`, and
   publish only the built `out/` contents to a `gh-pages` branch (so build
   artifacts never mix with source history). The standard tool for this is the
   [`gh-pages`](https://www.npmjs.com/package/gh-pages) package (installed as a
   devDependency):
   ```bash
   npm run deploy
   ```
   then point GitHub Pages (Settings → Pages → Source) at the `gh-pages` branch,
   root.

   `public/.nojekyll` (and therefore `out/.nojekyll`) exists specifically so
   GitHub Pages' default Jekyll processing doesn't strip Next.js's `_next/`
   directory — Jekyll ignores any folder starting with an underscore unless
   told not to.

No GitHub Actions workflow exists for this yet — the above is run manually for
now. If/when it's worth automating, a workflow would just wire these same six
steps to run on push to `main` (`actions/setup-java` + `actions/setup-node` +
the commands above + `peaceiris/actions-gh-pages` or
`actions/deploy-pages`).

## Roadmap

- [x] Scaffold Next.js app shell
- [x] Prove the TeaVM -> WebAssembly (wasmGC) pipeline with a minimal demo
- [x] Port the real Java Game Engine (rendering, input, resource loading) to the browser and embed it in Next.js
- [ ] Build core movement + interaction system (mobile joystick, desktop keyboard/cursor)
- [ ] Design lab environment and exhibit-to-content mapping
- [ ] Populate exhibits with real portfolio content (experience, education, publications, patents, awards)
- [ ] Cross-device polish pass
- [ ] Automate the deployment steps above (GitHub Actions)
