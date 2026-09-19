import { useState } from "react";
import type { FormEvent } from "react";
import axios from "axios";
import { createProject, uploadProjectCoverImage } from "./api";
import type { NewProjectInput } from "./api";

interface AddProjectModalProps {
  onClose: () => void;
  onCreated: () => void;
}

const emptyForm: NewProjectInput = {
  projectName: "",
  projectDescription: "",
  keywords: [],
  isActive: true,
  projectURL: "",
  startDate: "",
  endDate: null,
};

function extractErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === "string" && data.trim().length > 0) {
      return data;
    }
  }
  return fallback;
}

export default function AddProjectModal(props: AddProjectModalProps) {
  const [form, setForm] = useState<NewProjectInput>(emptyForm);
  const [keywordsInput, setKeywordsInput] = useState("");
  const [coverImage, setCoverImage] = useState<File | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      const keywords = keywordsInput
        .split(",")
        .map(function trimKeyword(keyword) {
          return keyword.trim();
        })
        .filter(function hasContent(keyword) {
          return keyword.length > 0;
        });

      const newProjectId = await createProject({ ...form, keywords });

      if (coverImage) {
        await uploadProjectCoverImage(newProjectId, coverImage);
      }

      props.onCreated();
      props.onClose();
    } catch (submitError) {
      setError(extractErrorMessage(submitError, "Could not create the project"));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4 font-mono">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md rounded-2xl border border-line bg-surface p-6 shadow-2xl"
      >
        <h2 className="mb-5 text-lg font-bold text-text">Create project</h2>

        <div className="flex flex-col gap-3.5">
          <input
            type="text"
            placeholder="Project name"
            value={form.projectName}
            onChange={(event) => setForm({ ...form, projectName: event.target.value })}
            required
            className="rounded-lg border border-line bg-bg px-3 py-2 text-sm text-text outline-none focus:border-accent"
          />

          <textarea
            placeholder="Description"
            value={form.projectDescription}
            onChange={(event) => setForm({ ...form, projectDescription: event.target.value })}
            required
            rows={3}
            className="resize-none rounded-lg border border-line bg-bg px-3 py-2 text-sm text-text outline-none focus:border-accent"
          />

          <input
            type="text"
            placeholder="Add keywords"
            value={keywordsInput}
            onChange={(event) => setKeywordsInput(event.target.value)}
            className="rounded-lg border border-line bg-bg px-3 py-2 text-sm text-text outline-none focus:border-accent"
          />

          <input
            type="url"
            placeholder="Project-URL"
            value={form.projectURL}
            onChange={(event) => setForm({ ...form, projectURL: event.target.value })}
            required
            className="rounded-lg border border-line bg-bg px-3 py-2 text-sm text-text outline-none focus:border-accent"
          />

          <div className="flex gap-3">
            <input
              type="date"
              value={form.startDate}
              onChange={(event) => setForm({ ...form, startDate: event.target.value })}
              required
              className="flex-1 rounded-lg border border-line bg-bg px-3 py-2 text-sm text-text outline-none focus:border-accent"
            />
            <input
              type="date"
              value={form.endDate ?? ""}
              onChange={(event) => setForm({ ...form, endDate: event.target.value || null })}
              className="flex-1 rounded-lg border border-line bg-bg px-3 py-2 text-sm text-text outline-none focus:border-accent"
            />
          </div>

          <input
            type="file"
            accept="image/*"
            onChange={(event) => setCoverImage(event.target.files?.[0] ?? null)}
            className="text-sm text-muted file:mr-3 file:rounded-lg file:border file:border-line file:bg-bg file:px-3 file:py-1.5 file:text-text"
          />

          <label className="flex items-center gap-2 text-sm text-muted">
            <input
              type="checkbox"
              checked={form.isActive}
              onChange={(event) => setForm({ ...form, isActive: event.target.checked })}
            />
            Active project
          </label>
        </div>

        {error && <p className="mt-3 text-sm text-red-400">{error}</p>}

        <div className="mt-5 flex justify-end gap-2.5">
          <button
            type="button"
            onClick={props.onClose}
            disabled={isSubmitting}
            className="rounded-lg border border-line px-4 py-2 text-sm font-semibold text-muted transition-colors hover:text-text"
          >
            Stop
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="rounded-lg border border-accent bg-accent px-4 py-2 text-sm font-bold text-bg transition-colors hover:bg-transparent hover:text-accent disabled:opacity-50"
          >
            {isSubmitting ? "Saving" : "Create"}
          </button>
        </div>
      </form>
    </div>
  );
}