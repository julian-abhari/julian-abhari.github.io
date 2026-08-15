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

## Roadmap

- [ ] Scaffold Next.js app shell
- [ ] Compile Java Game Engine to WebAssembly via TeaVM and embed in Next.js
- [ ] Build core movement + interaction system (mobile joystick, desktop keyboard/cursor)
- [ ] Design lab environment and exhibit-to-content mapping
- [ ] Populate exhibits with real portfolio content (experience, education, publications, patents, awards)
- [ ] Cross-device polish pass
