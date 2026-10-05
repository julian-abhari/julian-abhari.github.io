"use client";

import { useState } from "react";
import type { Category, RenderedContentEntry } from "@/content/schema";
import { ContentPanel } from "@/components/ContentPanel";

type Props = {
  entriesByCategory: Record<Category, RenderedContentEntry[]>;
};

export function DebugContentBrowser({ entriesByCategory }: Props) {
  const [selectedId, setSelectedId] = useState<string | null>(null);

  return (
    <div className="mt-6 grid grid-cols-1 gap-8 lg:grid-cols-[1fr_2fr]">
      <div className="space-y-6">
        {(Object.entries(entriesByCategory) as [Category, RenderedContentEntry[]][]).map(
          ([category, entries]) => (
            <div key={category}>
              <h2 className="text-sm font-semibold tracking-wide uppercase opacity-70">
                {category} ({entries.length})
              </h2>
              <ul className="mt-2 space-y-1">
                {entries.map((entry) => (
                  <li key={entry.id}>
                    <button
                      onClick={() => setSelectedId(entry.id)}
                      className={`text-left text-sm underline-offset-2 hover:underline ${
                        selectedId === entry.id ? "text-cyan-400" : "text-neutral-200"
                      }`}
                    >
                      {entry.title}
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          ),
        )}
      </div>

      <div className="lg:sticky lg:top-0 lg:max-h-[calc(100dvh-4rem)] lg:self-start lg:overflow-y-auto">
        {selectedId ? (
          <ContentPanel key={selectedId} entryId={selectedId} onClose={() => setSelectedId(null)} />
        ) : (
          <div className="text-sm opacity-60">Select an entry to preview it.</div>
        )}
      </div>
    </div>
  );
}
