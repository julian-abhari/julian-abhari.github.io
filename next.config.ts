import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Static export for GitHub Pages — see README's Deployment section.
  // julian-abhari.github.io is a GitHub *user* page, served from the domain
  // root, so no basePath/assetPrefix is needed (unlike a project page such as
  // <user>.github.io/<repo>/).
  output: "export",
};

export default nextConfig;
