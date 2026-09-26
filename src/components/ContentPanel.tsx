"use client";

import { useState, type MouseEvent, type ReactNode } from "react";
import { entriesById } from "@/generated/content";
import type { Category, RenderedContentEntry } from "@/content/schema";

type Theme = { label: string; icon: string; accent: string };

const THEMES: Record<Category, Theme> = {
  work: { label: "Work", icon: "\u{1F4BC}", accent: "border-amber-500 text-amber-400" },
  research: { label: "Research", icon: "\u{1F52C}", accent: "border-cyan-500 text-cyan-400" },
  projects: { label: "Project", icon: "\u{1F9EA}", accent: "border-emerald-500 text-emerald-400" },
  education: { label: "Education", icon: "\u{1F393}", accent: "border-indigo-500 text-indigo-400" },
  publications: { label: "Publication", icon: "\u{1F4C4}", accent: "border-purple-500 text-purple-400" },
  patents: { label: "Patent", icon: "\u{1F5C4}️", accent: "border-slate-400 text-slate-300" },
  awards: { label: "Award", icon: "\u{1F3C6}", accent: "border-yellow-500 text-yellow-400" },
};

function Field({ label, value }: { label: string; value: ReactNode }) {
  return (
    <div className="flex gap-2">
      <span className="w-32 shrink-0 opacity-60">{label}</span>
      <span>{value}</span>
    </div>
  );
}

function ExternalLink({ href, children }: { href: string; children: ReactNode }) {
  return (
    <a href={href} target="_blank" rel="noreferrer" className="underline">
      {children}
    </a>
  );
}

function ExtraFields({ entry }: { entry: RenderedContentEntry }) {
  switch (entry.category) {
    case "work":
      return (
        <>
          <Field label="Role" value={entry.role} />
          <Field label="Employment type" value={entry.employmentType} />
        </>
      );
    case "research":
      return (
        <>
          <Field label="Institution" value={entry.institution} />
          {entry.advisor && <Field label="Advisor" value={entry.advisor} />}
          {entry.lab && <Field label="Lab" value={entry.lab} />}
        </>
      );
    case "projects":
      return (
        <>
          {entry.techStack.length > 0 && (
            <Field label="Tech stack" value={entry.techStack.join(", ")} />
          )}
          {entry.repoUrl && (
            <Field label="Repo" value={<ExternalLink href={entry.repoUrl}>{entry.repoUrl}</ExternalLink>} />
          )}
          {entry.demoUrl && (
            <Field label="Demo" value={<ExternalLink href={entry.demoUrl}>{entry.demoUrl}</ExternalLink>} />
          )}
        </>
      );
    case "education":
      return (
        <>
          <Field label="Institution" value={entry.institution} />
          <Field label="Degree" value={entry.degree} />
          <Field label="Field" value={entry.field} />
          {entry.gpa != null && <Field label="GPA" value={String(entry.gpa)} />}
        </>
      );
    case "publications":
      return (
        <>
          <Field label="Authors" value={entry.authors.join(", ")} />
          <Field label="Venue" value={entry.venue} />
          {entry.doi && (
            <Field
              label="DOI"
              value={<ExternalLink href={`https://doi.org/${entry.doi}`}>{entry.doi}</ExternalLink>}
            />
          )}
        </>
      );
    case "patents":
      return (
        <>
          <Field label="Patent number" value={entry.patentNumber} />
          <Field label="Status" value={entry.status} />
          <Field label="Inventors" value={entry.inventors.join(", ")} />
        </>
      );
    case "awards":
      return (
        <>
          <Field label="Awarding body" value={entry.awardingBody} />
          <Field label="Date received" value={entry.dateReceived} />
        </>
      );
  }
}

type ContentPanelProps = {
  entryId: string;
  onClose?: () => void;
};

/** Generic, schema-driven panel — one component for every content category. */
export function ContentPanel({ entryId, onClose }: ContentPanelProps) {
  const [stack, setStack] = useState<string[]>([entryId]);
  const currentId = stack[stack.length - 1];
  const entry = entriesById[currentId];

  // Cross-links in entry.html are resolved to <a data-entry-id> anchors at
  // build time (there's no per-entry route in this static-export SPA), so
  // clicking one navigates within the panel instead of leaving the page.
  function handleBodyClick(event: MouseEvent<HTMLDivElement>) {
    const link = (event.target as HTMLElement).closest("a[data-entry-id]");
    if (!link) return;
    event.preventDefault();
    const targetId = link.getAttribute("data-entry-id");
    if (targetId && entriesById[targetId]) {
      setStack((s) => [...s, targetId]);
    }
  }

  if (!entry) {
    return <div className="p-4 text-red-400">Unknown entry: {currentId}</div>;
  }

  const theme = THEMES[entry.category];

  return (
    <div className={`rounded-lg border-2 bg-neutral-900/95 p-6 text-neutral-100 shadow-xl ${theme.accent}`}>
      <div className="mb-4 flex items-center justify-between">
        <div className="flex items-center gap-2 text-sm tracking-wide uppercase opacity-80">
          <span>{theme.icon}</span>
          <span>{theme.label}</span>
        </div>
        <div className="flex gap-3">
          {stack.length > 1 && (
            <button
              onClick={() => setStack((s) => s.slice(0, -1))}
              className="text-sm underline opacity-80 hover:opacity-100"
            >
              Back
            </button>
          )}
          {onClose && (
            <button onClick={onClose} className="text-sm underline opacity-80 hover:opacity-100">
              Close
            </button>
          )}
        </div>
      </div>

      <h2 className="text-xl font-bold">{entry.title}</h2>
      {entry.organization && <div className="text-sm opacity-80">{entry.organization}</div>}
      <div className="mt-1 text-sm opacity-70">
        {entry.startDate} &ndash; {entry.endDate ?? "Present"}
        {entry.location ? ` · ${entry.location}` : ""}
      </div>

      {entry.tags.length > 0 && (
        <div className="mt-3 flex flex-wrap gap-1">
          {entry.tags.map((tag) => (
            <span key={tag} className="rounded-full bg-neutral-800 px-2 py-0.5 text-xs">
              {tag}
            </span>
          ))}
        </div>
      )}

      <p className="mt-4 text-sm opacity-90">{entry.summary}</p>

      <div className="mt-4 grid gap-1 text-sm">
        <ExtraFields entry={entry} />
      </div>

      <div
        className="mt-4 space-y-2 text-sm leading-relaxed [&_a]:text-cyan-400 [&_a]:underline [&_li]:mb-1 [&_ul]:list-disc [&_ul]:pl-5"
        onClick={handleBodyClick}
        dangerouslySetInnerHTML={{ __html: entry.html }}
      />

      {entry.links.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-3">
          {entry.links.map((link) => (
            <ExternalLink key={link.url} href={link.url}>
              {link.label}
            </ExternalLink>
          ))}
        </div>
      )}
    </div>
  );
}
