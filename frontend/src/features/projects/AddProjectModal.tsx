import { useRef, useState } from "react";
import type { FormEvent, KeyboardEvent } from "react";
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
  const [keywordInput, setKeywordInput] = useState("");
  const [isOngoing, setIsOngoing] = useState(false);
  const [coverImage, setCoverImage] = useState<File | null>(null);
  const [coverPreviewUrl, setCoverPreviewUrl] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  function handleCoverChange(file: File | null) {
    setCoverImage(file);
    setCoverPreviewUrl(function updatePreview(currentUrl) {
      if (currentUrl) URL.revokeObjectURL(currentUrl);
      return file ? URL.createObjectURL(file) : null;
    });
  }

  function addKeyword() {
    const trimmed = keywordInput.trim();
    if (trimmed.length === 0 || form.keywords.includes(trimmed)) {
      setKeywordInput("");
      return;
    }
    setForm({ ...form, keywords: [...form.keywords, trimmed] });
    setKeywordInput("");
  }

  function handleKeywordKeyDown(event: KeyboardEvent<HTMLInputElement>) {
    if (event.key === "Enter") {
      event.preventDefault();
      addKeyword();
    }
  }

  function removeKeyword(keyword: string) {
    setForm({
      ...form,
      keywords: form.keywords.filter(function keepOthers(existing) {
        return existing !== keyword;
      }),
    });
  }

  function handleOngoingToggle() {
    const next = !isOngoing;
    setIsOngoing(next);
    setForm({ ...form, endDate: next ? null : form.endDate });
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      const newProjectId = await createProject(form);

      if (coverImage) {
        await uploadProjectCoverImage(newProjectId, coverImage);
      }

      props.onCreated();
      props.onClose();
    } catch (submitError) {
      setError(extractErrorMessage(submitError, "Could not create the project."));
    } finally {
      setIsSubmitting(false);
    }
  }

  const fieldLabelClasses = "block text-[11px] font-bold uppercase tracking-[0.08em] text-muted mb-[9px]";
  const inputClasses =
    "w-full rounded-[10px] border border-line bg-surface px-3.5 py-3 text-sm text-text outline-none transition-colors placeholder:text-muted/65 focus:border-accent focus:ring-[3px] focus:ring-accent/20";
  const today = new Date().toISOString().split("T")[0];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4 py-8 font-mono">
      <form
        onSubmit={handleSubmit}
        className="flex w-full max-w-[820px] max-h-[92vh] flex-col overflow-y-auto rounded-2xl border border-line bg-bg"
      >
        <div className="mx-auto w-full max-w-[720px] px-8 pt-12 pb-16">
          <div className="mb-7 flex items-center justify-between">
            <button
              type="button"
              onClick={props.onClose}
              className="inline-flex items-center gap-2 text-[13px] font-semibold text-muted transition-colors hover:text-text"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-[15px] w-[15px]">
                <path d="M19 12H5" />
                <path d="M12 19l-7-7 7-7" />
              </svg>
              Back to Projects
            </button>
          </div>

          <h1 className="mb-8 text-2xl font-bold text-text">Create project</h1>

          <div className="mb-8">
            <span className={fieldLabelClasses}>Cover image</span>
            <div className="group relative flex h-[220px] items-center justify-center overflow-hidden rounded-[14px] border border-line bg-gradient-to-br from-accent/10 to-surface">
              {coverPreviewUrl ? (
                <img src={coverPreviewUrl} alt="Cover preview" className="h-full w-full object-cover" />
              ) : (
                <span className="text-[11px] uppercase tracking-[0.15em] text-muted/70">[ cover image ]</span>
              )}

              <div className="absolute bottom-3 right-3 flex translate-y-1 gap-2 opacity-0 transition-all group-hover:translate-y-0 group-hover:opacity-100">
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  className="inline-flex items-center gap-1.5 rounded-lg border border-line bg-bg/78 px-3 py-[7px] text-xs font-semibold text-text backdrop-blur-sm transition-colors hover:border-accent hover:text-accent"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-[13px] w-[13px]">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                    <polyline points="17 8 12 3 7 8" />
                    <line x1="12" y1="3" x2="12" y2="15" />
                  </svg>
                  Upload cover
                </button>
                {coverPreviewUrl && (
                  <button
                    type="button"
                    onClick={() => handleCoverChange(null)}
                    className="inline-flex items-center gap-1.5 rounded-lg border border-line bg-bg/78 px-3 py-[7px] text-xs font-semibold text-text backdrop-blur-sm transition-colors hover:border-[#ff5c5c] hover:text-[#ff5c5c]"
                  >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-[13px] w-[13px]">
                      <line x1="18" y1="6" x2="6" y2="18" />
                      <line x1="6" y1="6" x2="18" y2="18" />
                    </svg>
                    Remove
                  </button>
                )}
              </div>
            </div>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={(event) => handleCoverChange(event.target.files?.[0] ?? null)}
              className="hidden"
            />
          </div>

          <div className="mb-[22px]">
            <span className={fieldLabelClasses}>Project name</span>
            <input
              type="text"
              value={form.projectName}
              onChange={(event) => setForm({ ...form, projectName: event.target.value })}
              required
              className={inputClasses}
            />
          </div>

          <div className="mb-[22px]">
            <span className={fieldLabelClasses}>Description</span>
            <textarea
              value={form.projectDescription}
              onChange={(event) => setForm({ ...form, projectDescription: event.target.value })}
              required
              className={`${inputClasses} min-h-[90px] resize-y leading-relaxed`}
            />
          </div>

          <div className="mb-[22px]">
            <span className={fieldLabelClasses}>Technologies</span>
            {form.keywords.length > 0 && (
              <div className="mb-2.5 flex flex-wrap gap-2">
                {form.keywords.map(function renderKeyword(keyword) {
                  return (
                    <span
                      key={keyword}
                      className="inline-flex items-center gap-[7px] rounded-full border border-accent/25 bg-accent/8 py-[5px] pl-3 pr-2 text-xs font-semibold text-accent"
                    >
                      {keyword}
                      <button
                        type="button"
                        onClick={() => removeKeyword(keyword)}
                        aria-label={`Remove ${keyword}`}
                        className="flex h-4 w-4 items-center justify-center rounded-full bg-accent/18 text-xs leading-none text-accent transition-colors hover:bg-accent/30"
                      >
                        ×
                      </button>
                    </span>
                  );
                })}
              </div>
            )}
            <input
              type="text"
              value={keywordInput}
              onChange={(event) => setKeywordInput(event.target.value)}
              onKeyDown={handleKeywordKeyDown}
              placeholder="Type a technology and press Enter…"
              className="w-full rounded-[10px] border border-dashed border-line bg-surface px-3.5 py-[11px] text-[13px] text-text outline-none placeholder:text-muted/65 focus:border-solid focus:border-accent"
            />
            <p className="mt-2 text-[11.5px] text-muted">Press Enter to add, × to remove.</p>
          </div>

          <div className="mb-[22px]">
            <span className={fieldLabelClasses}>Project URL</span>
            <input
              type="url"
              value={form.projectURL}
              onChange={(event) => setForm({ ...form, projectURL: event.target.value })}
              required
              className={inputClasses}
            />
          </div>

          <div className="mb-[22px] grid grid-cols-2 gap-[18px]">
            <div>
              <span className={fieldLabelClasses}>Start date</span>
              <input
                type="date"
                value={form.startDate}
                onChange={(event) => setForm({ ...form, startDate: event.target.value })}
                max={today}
                required
                className={inputClasses}
              />
            </div>
            <div className={`transition-opacity ${isOngoing ? "pointer-events-none opacity-35" : ""}`}>
              <span className={fieldLabelClasses}>End date</span>
              <input
                type="date"
                value={form.endDate ?? ""}
                onChange={(event) => setForm({ ...form, endDate: event.target.value || null })}
                disabled={isOngoing}
                className={inputClasses}
              />
            </div>
          </div>

          <label className="-mt-[6px] mb-[22px] flex cursor-pointer items-center gap-2.5">
            <input
              type="checkbox"
              checked={isOngoing}
              onChange={handleOngoingToggle}
              className="sr-only peer"
            />
            <span className="flex h-[18px] w-[18px] flex-shrink-0 items-center justify-center rounded-[5px] border-[1.5px] border-line bg-surface transition-colors peer-checked:border-accent peer-checked:bg-accent">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" className={`h-3 w-3 text-bg ${isOngoing ? "block" : "hidden"}`}>
                <polyline points="20 6 9 17 4 12" />
              </svg>
            </span>
            <span className="text-[13px] font-semibold text-text">Ongoing project (no end date yet)</span>
          </label>

          <div className={`mb-[22px] transition-opacity ${isOngoing ? "pointer-events-none opacity-35" : ""}`}>
            <span className={fieldLabelClasses}>Status</span>
            <div className="inline-flex overflow-hidden rounded-[10px] border border-line">
              <button
                type="button"
                onClick={() => setForm({ ...form, isActive: true })}
                className={`flex items-center gap-[7px] px-[18px] py-[10px] text-[13px] font-bold transition-colors ${
                  form.isActive ? "bg-accent text-bg" : "text-muted"
                }`}
              >
                <span className="h-1.5 w-1.5 rounded-full bg-current" />
                Active
              </button>
              <button
                type="button"
                onClick={() => setForm({ ...form, isActive: false })}
                className={`flex items-center gap-[7px] px-[18px] py-[10px] text-[13px] font-bold transition-colors ${
                  !form.isActive ? "bg-muted/14 text-text" : "text-muted"
                }`}
              >
                <span className="h-1.5 w-1.5 rounded-full bg-current" />
                Inactive
              </button>
            </div>
            <p className="mt-2 text-[11.5px] text-muted">
              Active = the project is live. Inactive = finished, no longer running.
            </p>
          </div>

          {error && <p className="mb-4 text-sm text-[#ff8080]">{error}</p>}

          <div className="flex justify-end gap-3">
            <button
              type="button"
              onClick={props.onClose}
              disabled={isSubmitting}
              className="rounded-[10px] border border-line px-[22px] py-[13px] text-sm font-bold text-muted transition-colors hover:border-muted/50 hover:text-text"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="rounded-[10px] bg-accent px-[26px] py-[13px] text-sm font-bold text-bg transition-[filter] hover:brightness-110 disabled:opacity-50"
            >
              {isSubmitting ? "Creating…" : "Save changes"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}