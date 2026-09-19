import type { ProjectDTO } from "./types";

interface ProjectCardProps {
  project: ProjectDTO;
  isFeatured?: boolean;
}

export default function ProjectCard(props: ProjectCardProps) {
  return (
    <article className="relative w-full overflow-hidden rounded-2xl border border-line bg-surface shadow-lg transition hover:-translate-y-1 hover:border-accent hover:shadow-2xl">
      <div className="relative h-44 border-b border-line bg-gradient-to-br from-[#14251d] to-surface flex items-center justify-center">
        {props.isFeatured && (
          <span className="absolute top-3 left-3 bg-accent text-bg text-[10px] font-bold uppercase tracking-wide px-2.5 py-1 rounded-full">
            Featured
          </span>
        )}
        {props.project.imagePath ? (
          <img
            src={`http://localhost:8080/${props.project.imagePath}`}
            alt={props.project.projectName}
            className="w-full h-full object-cover"
          />
        ) : (
          <span className="text-[11px] tracking-widest uppercase text-muted">
            [ cover image ]
          </span>
        )}
      </div>

      <div className="p-5 flex flex-col gap-3">
        {props.project.keywords.length > 0 && (
          <div className="flex flex-wrap gap-1.5">
            {props.project.keywords.map(function renderKeyword(keyword) {
              return (
                <span
                  key={keyword}
                  className="text-[11px] text-accent border border-[#1c3629] bg-[#0d1a14] rounded-full px-2.5 py-0.5"
                >
                  {keyword}
                </span>
              );
            })}
          </div>
        )}

        <h3 className="text-lg font-bold text-text hover:text-accent cursor-pointer">
          {props.project.projectName}
        </h3>

        <p className="text-sm text-muted leading-relaxed line-clamp-2">
          {props.project.projectDescription}
        </p>

        <div className="flex items-center justify-between border-t border-line pt-3.5">
          <span className="inline-flex items-center gap-1.5 text-accent text-sm font-semibold">
            Visit project
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-3.5 h-3.5">
              <path d="M5 12h14" />
              <path d="M13 6l6 6-6 6" />
            </svg>
          </span>


            <a href={props.project.projectURL}
            target="_blank"
            rel="noreferrer"
            aria-label="Visit live project"
            className="inline-flex items-center justify-center w-8 h-8 rounded-lg border border-line text-muted hover:border-accent hover:text-accent"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-4 h-4">
              <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6" />
              <path d="M15 3h6v6" />
              <path d="M10 14L21 3" />
            </svg>
          </a>
        </div>
      </div>
    </article>
  );
}