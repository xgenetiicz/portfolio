import type { ProjectDTO } from "./types";

interface ProjectCardProps {
  project: ProjectDTO;
}

export default function ProjectCard(props: ProjectCardProps) {
  return (
    <article className="project-card">
      {props.project.imagePath && (
        <img
          src={`http://localhost:8080/${props.project.imagePath}`}
          alt={props.project.projectName}
        />
      )}
      <h3>{props.project.projectName}</h3>
      <p>{props.project.projectDescription}</p>
      <a href={props.project.projectURL} target="_blank" rel="noreferrer">
        Besøk prosjekt
      </a>
    </article>
  );
}