import { useEffect, useState } from "react";
import ProjectCard from "./ProjectCard";
import { getProjects } from "./api";
import type { ProjectDTO } from "./types";

export default function ProjectsPage() {
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
    <main className="mx-auto max-w-6xl px-4 py-16 font-mono">
      <h1 className="mb-8 text-2xl font-bold text-text">Projects</h1>
      <section className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {isLoading && <p className="text-muted">Laster prosjekter…</p>}
        {!isLoading && projects.length === 0 && (
          <p className="text-muted">Ingen prosjekter enda.</p>
        )}
        {projects.map(function renderProject(project, index) {
          return <ProjectCard key={project.projectId} project={project} isFeatured={index < 3} />;
        })}
      </section>
    </main>
  );
}