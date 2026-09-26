import { entriesByCategory } from "@/generated/content";
import { DebugContentBrowser } from "./DebugContentBrowser";

// Temporary route proving the content pipeline (parse -> validate -> render)
// in isolation, before any exhibit-tile/Java wiring exists. Delete or gate
// once the real in-world exhibit interaction UI lands.
export default function DebugContentPage() {
  return (
    <div className="min-h-dvh bg-neutral-950 p-8 text-neutral-100">
      <h1 className="text-2xl font-bold">Content Debug</h1>
      <p className="mt-1 text-sm opacity-70">
        Every entry from every category, generated at build time from{" "}
        <code>content/**/*.md</code>. Click one to preview its ContentPanel.
      </p>
      <DebugContentBrowser entriesByCategory={entriesByCategory} />
    </div>
  );
}
