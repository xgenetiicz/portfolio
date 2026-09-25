import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import ProjectCard from "../projects/ProjectCard";
import ViewProjectModal from "../projects/ViewProjectModal";
import { getProjects } from "../projects/api";
import type { ProjectDTO } from "../projects/types";

export default function FeaturedProjects() {
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedProject, setSelectedProject] = useState<ProjectDTO | null>(null);

  useEffect(function loadFeatured() {
    getProjects()
      .then(function handleProjects(fetchedProjects) {
        setProjects(fetchedProjects.filter((project) => project.isFeatured));
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
        <h2 className="text-2xl font-bold text-text">Featured projects</h2>
        <Link to="/projects?category=SOFTWARE" className="text-sm font-semibold text-accent hover:underline">
          View all →
        </Link>
      </div>

      <p className="text-muted leading-relaxed mb-4">
       These projects are listed as featured - larger, more complex work that best showcases what
            I've been building. Most are personal projects done in my free time, though a few, like my
            bachelor's thesis, mean a lot more to me than that.
       </p>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {projects.map(function renderProject(project) {
          return (
            <ProjectCard
              key={project.projectId}
              project={project}
              isFeatured={project.isFeatured}
              onView={setSelectedProject}
            />
          );
        })}
      </div>

      {selectedProject && <ViewProjectModal project={selectedProject} onClose={() => setSelectedProject(null)} />}
    </section>
  );
}