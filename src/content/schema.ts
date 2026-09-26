import { z } from "zod";

const dateString = z
  .string()
  .regex(/^\d{4}(-\d{2})?$/, "must be YYYY or YYYY-MM");

const link = z.object({
  label: z.string(),
  url: z.string(),
});

const commonFields = {
  id: z.string(),
  title: z.string(),
  organization: z.string().nullable().optional(),
  startDate: dateString,
  endDate: dateString.nullable(),
  location: z.string().optional(),
  tags: z.array(z.string()),
  summary: z.string(),
  featured: z.boolean(),
  links: z.array(link),
  media: z.array(z.string()),
};

export const workSchema = z.object({
  ...commonFields,
  category: z.literal("work"),
  role: z.string(),
  employmentType: z.enum(["full-time", "contract", "advisor", "internship"]),
});

export const researchSchema = z.object({
  ...commonFields,
  category: z.literal("research"),
  institution: z.string(),
  advisor: z.string().optional(),
  lab: z.string().optional(),
});

export const projectsSchema = z.object({
  ...commonFields,
  category: z.literal("projects"),
  techStack: z.array(z.string()),
  repoUrl: z.string().nullable(),
  demoUrl: z.string().nullable(),
});

export const educationSchema = z.object({
  ...commonFields,
  category: z.literal("education"),
  institution: z.string(),
  degree: z.string(),
  field: z.string(),
  gpa: z.number().optional(),
});

export const publicationsSchema = z
  .object({
    ...commonFields,
    category: z.literal("publications"),
    authors: z.array(z.string()),
    venue: z.string(),
    doi: z.string().optional(),
    publicationDate: dateString,
  })
  .refine((entry) => Boolean(entry.doi) || entry.links.length > 0, {
    message: "publications must have a doi or at least one link",
    path: ["doi"],
  });

export const patentsSchema = z.object({
  ...commonFields,
  category: z.literal("patents"),
  patentNumber: z.string(),
  status: z.enum(["filed", "granted"]),
  inventors: z.array(z.string()),
});

export const awardsSchema = z.object({
  ...commonFields,
  category: z.literal("awards"),
  awardingBody: z.string(),
  dateReceived: dateString,
});

export const contentEntrySchema = z.discriminatedUnion("category", [
  workSchema,
  researchSchema,
  projectsSchema,
  educationSchema,
  publicationsSchema,
  patentsSchema,
  awardsSchema,
]);

export const categorySchema = z.enum([
  "work",
  "research",
  "projects",
  "education",
  "publications",
  "patents",
  "awards",
]);

export type Category = z.infer<typeof categorySchema>;
export type WorkEntry = z.infer<typeof workSchema>;
export type ResearchEntry = z.infer<typeof researchSchema>;
export type ProjectsEntry = z.infer<typeof projectsSchema>;
export type EducationEntry = z.infer<typeof educationSchema>;
export type PublicationsEntry = z.infer<typeof publicationsSchema>;
export type PatentsEntry = z.infer<typeof patentsSchema>;
export type AwardsEntry = z.infer<typeof awardsSchema>;
export type ContentEntry = z.infer<typeof contentEntrySchema>;

export const CATEGORIES: Category[] = [
  "work",
  "research",
  "projects",
  "education",
  "publications",
  "patents",
  "awards",
];

/** A validated entry with its markdown body compiled to HTML at build time. */
export type RenderedContentEntry = ContentEntry & { html: string };

export const schemaByCategory: Record<Category, z.ZodType<ContentEntry>> = {
  work: workSchema as unknown as z.ZodType<ContentEntry>,
  research: researchSchema as unknown as z.ZodType<ContentEntry>,
  projects: projectsSchema as unknown as z.ZodType<ContentEntry>,
  education: educationSchema as unknown as z.ZodType<ContentEntry>,
  publications: publicationsSchema as unknown as z.ZodType<ContentEntry>,
  patents: patentsSchema as unknown as z.ZodType<ContentEntry>,
  awards: awardsSchema as unknown as z.ZodType<ContentEntry>,
};
