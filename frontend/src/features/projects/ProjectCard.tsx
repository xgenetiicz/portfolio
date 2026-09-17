import type { ProjectDTO } from "./types";

interface ProjectCardProps {
  project: ProjectDTO;
  isFeatured?: boolean;
}

export default function ProjectCard(props: ProjectCardProps) {
  return (
    <article className="relative w-full bg-[#0b0f0d] border border-[#1c2620] rounded-2xl overflow-hidden shadow-lg transition hover:-translate-y-1 hover:border-[#39ff9c] hover:shadow-2xl">
      <div className="relative h-44 border-b border-[#1c2620] bg-gradient-to-br from-[#14251d] to-[#0b0f0d] flex items-center justify-center">
        {props.isFeatured && (
          <span className="absolute top-3 left-3 bg-[#39ff9c] text-[#05070a] text-[10px] font-bold uppercase tracking-wide px-2.5 py-1 rounded-full">
            Fremhevet
          </span>
        )}
        {props.project.imagePath ? (
          <img
            src={`http://localhost:8080/${props.project.imagePath}`}
            alt={props.project.projectName}
            className="w-full h-full object-cover"
          />
        ) : (
          <span className="text-[11px] tracking-widest uppercase text-[#4f6a5e]">
            [ cover image ]
          </span>
        )}
      </div>

      <div className="p-5 flex flex-col gap-3">
        <div className="flex flex-wrap gap-1.5">
          <span className="text-[11px] text-[#39ff9c] border border-[#1c3629] bg-[#0d1a14] rounded-full px-2.5 py-0.5">
            Java
          </span>
          <span className="text-[11px] text-[#39ff9c] border border-[#1c3629] bg-[#0d1a14] rounded-full px-2.5 py-0.5">
            Spring Boot
          </span>
          <span className="text-[11px] text-[#39ff9c] border border-[#1c3629] bg-[#0d1a14] rounded-full px-2.5 py-0.5">
            PostgreSQL
          </span>
          <span className="text-[11px] text-[#39ff9c] border border-[#1c3629] bg-[#0d1a14] rounded-full px-2.5 py-0.5">
            Docker
          </span>
        </div>

        <h3 className="text-lg font-bold text-[#e7f3ec] hover:text-[#39ff9c] cursor-pointer">
          {props.project.projectName}
        </h3>

        <p className="text-sm text-[#93a89c] leading-relaxed line-clamp-2">
          {props.project.projectDescription}
        </p>

        <div className="flex items-center justify-between border-t border-[#171e1a] pt-3.5">
          <span className="inline-flex items-center gap-1.5 text-[#39ff9c] text-sm font-semibold">
            Visit project
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-3.5 h-3.5">
              <path d="M5 12h14" />
              <path d="M13 6l6 6-6 6" />
            </svg>
          </span>

            <a href={props.project.projectURL}
            target="_blank"
            rel="noreferrer"
            aria-label="Besøk live prosjekt"
            className="inline-flex items-center justify-center w-8 h-8 rounded-lg border border-[#1c2620] text-[#93a89c] hover:border-[#39ff9c] hover:text-[#39ff9c]"
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