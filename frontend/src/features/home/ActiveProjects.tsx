import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import ProjectCard from "../projects/ProjectCard";
import ViewProjectModal from "../projects/ViewProjectModal";
import { getProjects } from "../projects/api";
import type { ProjectDTO } from "../projects/types";

export default function ActiveProjects() {
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedProject, setSelectedProject] = useState<ProjectDTO | null>(null);

  useEffect(function loadActive() {
    getProjects()
      .then(function handleProjects(fetchedProjects) {
        setProjects(fetchedProjects.filter((project) => project.isActive));
      })
      .catch(function handleError() {
        setProjects([]);
      })
      .finally(function stopLoading() {
        setIsLoading(false);
      });
  }, []);

  if (isLoading || projects.length === 0) {
    return null;
  }

  return (
    <section className="mx-auto max-w-6xl border-t border-line px-6 py-16">
      <div className="mb-8 flex items-center justify-between">
        <h2 className="text-2xl font-bold text-text">Active projects</h2>
        <Link to="/projects?category=SOFTWARE" className="text-sm font-semibold text-accent hover:underline">
          View all →
        </Link>
      </div>

      <p className="text-muted leading-relaxed mb-4">
        These projects are deployed and running in production today. I'm a strong advocate for
        open-source and self-hosted software - several of these also let me manage my own data,
        like photos and videos, on servers I control.
      </p>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {projects.map(function renderProject(project) {
          return (
            <ProjectCard
              key={project.projectId}
              project={project}
              isActiveBadge
              onView={setSelectedProject}
            />
          );
        })}
      </div>

      {selectedProject && <ViewProjectModal project={selectedProject} onClose={() => setSelectedProject(null)} />}
    </section>
  );
}