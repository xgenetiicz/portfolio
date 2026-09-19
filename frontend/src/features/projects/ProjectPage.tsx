import { useEffect, useState } from "react";
import ProjectCard from "./ProjectCard";
import AddProjectModal from "./AddProjectModal";
import { getProjects, deleteProject } from "./api";
import type { ProjectDTO } from "./types";
import Button from "../../components/Button";
import FabButton from "../../components/FabButton";
import { useAuth } from "../auth/AuthContext";

export default function ProjectsPage() {
  const { isAuthenticated } = useAuth();
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function loadProjects() {
    setIsLoading(true);
    setError(null);
    getProjects()
      .then(function handleProjects(fetchedProjects) {
        setProjects(fetchedProjects);
      })
      .catch(function handleError() {
        setError("Could not load projects - could be empty.");
      })
      .finally(function stopLoading() {
        setIsLoading(false);
      });
  }

  useEffect(loadProjects, []);

  async function handleDelete(projectId: number) {
    const confirmed = window.confirm("Are you sure that you want to delete this project?.");
    if (!confirmed) return;

    try {
      await deleteProject(projectId);
      setProjects(function removeDeleted(currentProjects) {
        return currentProjects.filter(function keepOthers(project) {
          return project.projectId !== projectId;
        });
      });
    } catch {
      setError("Could not delete project");
    }
  }

  return (
    <main className="mx-auto max-w-6xl px-4 py-16 font-mono">
      <div className="mb-8 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-text">Projects</h1>
        {isAuthenticated && (
          <FabButton onClick={() => setIsAddModalOpen(true)} aria-label="Create project">
            +
          </FabButton>
        )}
      </div>

      {error && <p className="mb-4 text-sm text-red-400">{error}</p>}

      <section className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {isLoading && <p className="text-muted"></p>}
        {!isLoading && projects.length === 0 && (
          <p className="text-muted">No projects yet</p>
        )}
        {projects.map(function renderProject(project, index) {
          return (
            <ProjectCard
              key={project.projectId}
              project={project}
              isFeatured={index < 3}
              onDelete={isAuthenticated ? handleDelete : undefined}
            />
          );
        })}
      </section>

      {isAddModalOpen && (
        <AddProjectModal
          onClose={() => setIsAddModalOpen(false)}
          onCreated={loadProjects}
        />
      )}
    </main>
  );
}