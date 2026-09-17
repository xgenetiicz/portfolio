import { useEffect, useState } from "react";
import TerminalIntro from "./TerminalIntro";
import ProjectCard from "../projects/ProjectCard";
import { getProjects } from "../projects/api";
import type { ProjectDTO } from "../projects/types";

export default function HomePage() {
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(function loadProjects() {
    getProjects()
      .then(function handleProjects(fetchedProjects) {
        setProjects(fetchedProjects);
      })
      .finally(function stopLoading() {
        setIsLoading(false);
      });
  }, []);

  return (
    <main>
      <TerminalIntro />
      <section className="project-grid">
        {isLoading && <p>Laster prosjekter…</p>}
        {!isLoading && projects.length === 0 && <p>Ingen prosjekter enda.</p>}
        {projects.map(function renderProject(project) {
          return <ProjectCard key={project.projectId} project={project} />;
        })}
      </section>
    </main>
  );
}