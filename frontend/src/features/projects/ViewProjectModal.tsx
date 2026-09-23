import { useEffect, useState } from "react";
import { getContentForProject } from "./api";
import type { ProjectDTO, ContentDTO } from "./types";
import { VITE_API_BASE_URL } from "../../config";

interface ViewProjectModalProps {
  project: ProjectDTO;
  onClose: () => void;
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "long",
    day: "numeric",
    year: "numeric",
  });
}

// Server stores files as "<uuid>_<original-name>" to avoid collisions.
// Strip the uuid prefix back off so the visitor sees the real filename.
function getDisplayFileName(filePath: string): string {
  const lastSegment = filePath.split("/").pop() ?? filePath;
  const separatorIndex = lastSegment.indexOf("_");
  return separatorIndex === -1 ? lastSegment : lastSegment.substring(separatorIndex + 1);
}

function isMediaContent(item: ContentDTO): boolean {
  return item.contentType.startsWith("image") || item.contentType.startsWith("video");
}

export default function ViewProjectModal(props: ViewProjectModalProps) {
  const { project, onClose } = props;
  const [content, setContent] = useState<ContentDTO[]>([]);
  const [isLoadingContent, setIsLoadingContent] = useState(true);
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(function loadContent() {
    let cancelled = false;
    setIsLoadingContent(true);
    getContentForProject(project.projectId)
      .then(function handleContent(items) {
        if (!cancelled) setContent(items);
      })
      .catch(function handleError() {
        if (!cancelled) setContent([]);
      })
      .finally(function stopLoading() {
        if (!cancelled) setIsLoadingContent(false);
      });
    return function cleanup() {
      cancelled = true;
    };
  }, [project.projectId]);

  // Split into what belongs in the carousel (image/video) vs. the download list (everything else, e.g. PDF/ZIP).
  const mediaContent = content.filter(isMediaContent);
  const downloadableContent = content.filter((item) => !isMediaContent(item));

  function goPrev() {
    setCurrentIndex((index) => (index === 0 ? mediaContent.length - 1 : index - 1));
  }

  function goNext() {
    setCurrentIndex((index) => (index === mediaContent.length - 1 ? 0 : index + 1));
  }

  const current = mediaContent[currentIndex];
  const fieldLabelClasses = "block text-[11px] font-bold uppercase tracking-[0.08em] text-muted mb-[9px]";

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4 py-8 font-mono">
      <div className="relative flex w-full max-w-[1400px] max-h-[92vh] flex-col overflow-y-auto rounded-2xl border border-line bg-bg sm:flex-row">
        <button
          type="button"
          onClick={onClose}
          aria-label="Close"
          className="absolute right-4 top-4 z-10 text-muted transition-colors hover:text-text"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-5 w-5">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>

        {/* Media panel — top on mobile, right on desktop */}
        <div className="order-1 flex flex-col gap-3 p-6 sm:order-2 sm:w-[55%] sm:p-8 justify-center">
          <div className="relative flex h-[280px] items-center justify-center overflow-hidden rounded-[14px] border border-line bg-gradient-to-br from-accent/10 to-surface sm:h-[380px]">
            {isLoadingContent ? (
              <span className="text-[11px] uppercase tracking-[0.15em] text-muted/70">Loading…</span>
            ) : !current ? (
              <span className="text-[11px] uppercase tracking-[0.15em] text-muted/70">[ no content yet ]</span>
            ) : current.contentType.startsWith("video") ? (
              <video
                key={current.contentId}
                controls
                className="h-full w-full object-cover"
                src={`${VITE_API_BASE_URL}/${current.filePath}`}
              />
            ) : (
              <img
                key={current.contentId}
                src={`${VITE_API_BASE_URL}/${current.filePath}`}
                alt={`${project.projectName} screenshot`}
                className="h-full w-full object-cover"
              />
            )}

            {mediaContent.length > 1 && (
              <>
                <button
                  type="button"
                  onClick={goPrev}
                  aria-label="Previous"
                  className="absolute left-3 top-1/2 flex h-9 w-9 -translate-y-1/2 items-center justify-center rounded-full border border-line bg-bg/78 text-text backdrop-blur-sm transition-colors hover:border-accent hover:text-accent"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4">
                    <polyline points="15 18 9 12 15 6" />
                  </svg>
                </button>
                <button
                  type="button"
                  onClick={goNext}
                  aria-label="Next"
                  className="absolute right-3 top-1/2 flex h-9 w-9 -translate-y-1/2 items-center justify-center rounded-full border border-line bg-bg/78 text-text backdrop-blur-sm transition-colors hover:border-accent hover:text-accent"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4">
                    <polyline points="9 18 15 12 9 6" />
                  </svg>
                </button>
              </>
            )}
          </div>

          {mediaContent.length > 0 && (
            <div className="flex items-center justify-between px-1">
              <span className="text-[12px] text-muted">
                {currentIndex + 1} / {mediaContent.length}
              </span>
              <div className="flex gap-1.5">
                {mediaContent.map(function renderDot(item, index) {
                  return (
                    <button
                      key={item.contentId}
                      type="button"
                      onClick={() => setCurrentIndex(index)}
                      aria-label={`Go to item ${index + 1}`}
                      className={`h-1.5 w-1.5 rounded-full transition-colors ${
                        index === currentIndex ? "bg-accent" : "bg-line"
                      }`}
                    />
                  );
                })}
              </div>
            </div>
          )}
        </div>

        {/* Info panel — bottom on mobile, left on desktop */}
        <div className="order-2 flex flex-col p-6 sm:order-1 sm:w-[45%] sm:p-8">
                 <p className="mb-1 text-[13px] font-bold text-accent">$ view</p>
                 <h1 className="mb-4 text-2xl font-bold text-text">{project.projectName}</h1>
                 <div className="mb-5 max-h-[260px] overflow-y-auto overflow-x-hidden pr-2">
                   <p className="text-sm leading-relaxed text-muted break-words">{project.projectDescription}</p>
                 </div>

          <div className="mb-6 flex flex-wrap items-center gap-2">
            {!project.endDate ? (
              <span className="inline-flex w-fit items-center gap-2 rounded-full border border-accent/40 px-3 py-[5px] text-xs font-semibold text-accent">
                <span className="h-1.5 w-1.5 rounded-full bg-accent" />
                Ongoing
              </span>
            ) : (
              <span
                className={`inline-flex w-fit items-center gap-2 rounded-full border px-3 py-[5px] text-xs font-semibold ${
                  project.isActive ? "border-accent/40 text-accent" : "border-line text-muted"
                }`}
              >
                <span className={`h-1.5 w-1.5 rounded-full ${project.isActive ? "bg-accent" : "bg-muted"}`} />
                {project.isActive ? "Active" : "Inactive"}
              </span>
            )}
          </div>

         <div className="mb-5 grid grid-cols-2 gap-3">
           <div>
             <span className={fieldLabelClasses}>Start date</span>
             <p className="text-sm font-bold text-text">
               {formatDate(project.startDate)}
             </p>
           </div>

           {project.endDate && (
             <div>
               <span className={fieldLabelClasses}>End date</span>
               <p className="text-sm font-bold text-text">
                 {formatDate(project.endDate)}
               </p>
             </div>
           )}
         </div>

          <div className="mb-6">
            <span className={fieldLabelClasses}>Technologies</span>
            <div className="flex flex-wrap gap-2">
              {project.keywords.map(function renderKeyword(keyword) {
                return (
                  <span
                    key={keyword}
                    className="rounded-full border border-accent/25 bg-accent/8 px-3 py-[5px] text-xs font-semibold text-accent"
                  >
                    {keyword}
                  </span>
                );
              })}
            </div>
          </div>

          {downloadableContent.length > 0 && (
            <div className="mb-6">
              <span className={fieldLabelClasses}>Files</span>
              <ul className="space-y-1.5">
                {downloadableContent.map(function renderDownload(item) {
                  const downloadUrl = `${VITE_API_BASE_URL}/api/content/download/${project.projectId}/${item.contentId}`;
                  return (
                    <li
                      key={item.contentId}
                      className="flex items-center justify-between rounded-[8px] border border-line bg-surface px-3 py-2 text-[13px] text-text"
                    >
                      <span className="truncate">{getDisplayFileName(item.filePath)}</span>

                      <a href={downloadUrl}
                        target="_blank"
                        rel="noreferrer"
                        aria-label={`Download ${getDisplayFileName(item.filePath)}`}
                        className="flex h-7 w-7 flex-shrink-0 items-center justify-center rounded-lg border border-line text-muted transition-colors hover:border-accent hover:text-accent"
                      >
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-3.5 w-3.5">
                          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                          <polyline points="7 10 12 15 17 10" />
                          <line x1="12" y1="15" x2="12" y2="3" />
                        </svg>
                      </a>
                    </li>
                  );
                })}
              </ul>
            </div>
          )}

          <div className="mt-auto flex items-center justify-between border-t border-line pt-5">
            <button
              type="button"
              onClick={onClose}
              className="inline-flex items-center gap-2 text-[13px] font-semibold text-muted transition-colors hover:text-text"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-[15px] w-[15px]">
                <path d="M19 12H5" />
                <path d="M12 19l-7-7 7-7" />
              </svg>
              Back to projects
            </button>
            {project.projectURL && (
              <a href={project.projectURL}
                target="_blank"
                rel="noreferrer"
                aria-label="Visit project"
                className="flex h-9 w-9 items-center justify-center rounded-lg border border-line text-text transition-colors hover:border-accent hover:text-accent"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-[15px] w-[15px]">
                  <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6" />
                  <polyline points="15 3 21 3 21 9" />
                  <line x1="10" y1="14" x2="21" y2="3" />
                </svg>
              </a>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}