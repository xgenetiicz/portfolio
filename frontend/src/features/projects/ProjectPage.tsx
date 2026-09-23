import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import ProjectCard from "./ProjectCard";
import AddProjectModal from "./AddProjectModal";
import EditProjectModal from "./EditProjectModal";
import ViewProjectModal from "./ViewProjectModal";
import { getProjects, deleteProject } from "./api";
import type { ProjectDTO, ProjectCategory } from "./types";
import FabButton from "../../components/FabButton";
import { useAuth } from "../auth/AuthContext";

const categoryFilters: { value: ProjectCategory; label: string }[] = [
  { value: "SOFTWARE", label: "Software" },
  { value: "HARDWARE", label: "Hardware" },
  { value: "OTHER", label: "Other" },
];

export default function ProjectsPage() {
  const { isAuthenticated } = useAuth();
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingProject, setEditingProject] = useState<ProjectDTO | null>(null);
  const [selectedProject, setSelectedProject] = useState<ProjectDTO | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [searchParams, setSearchParams] = useSearchParams();

  const activeCategory = searchParams.get("category") as ProjectCategory | null;

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

  function handleCategoryClick(category: ProjectCategory) {
    setSearchParams(function updateParams(prev) {
      const next = new URLSearchParams(prev);
      if (activeCategory === category) next.delete("category");
      else next.set("category", category);
      return next;
    });
  }

  const filteredProjects = activeCategory
    ? projects.filter((project) => project.projectCategory === activeCategory)
    : projects;

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

      <div className="mb-6 flex gap-2">
        {categoryFilters.map(function renderFilter(filter) {
          const isSelected = activeCategory === filter.value;
          return (
            <button
              key={filter.value}
              type="button"
              onClick={() => handleCategoryClick(filter.value)}
              className={`rounded-full border px-3.5 py-1.5 text-xs font-bold transition-colors ${
                isSelected ? "border-accent bg-accent text-bg" : "border-line text-muted hover:border-accent hover:text-accent"
              }`}
            >
              {filter.label}
            </button>
          );
        })}
      </div>

      <section className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {isLoading && <p className="text-muted"></p>}
        {!isLoading && filteredProjects.length === 0 && (
          <p className="text-muted">No projects yet</p>
        )}
        {filteredProjects.map(function renderProject(project, index) {
          return (
            <ProjectCard
              key={project.projectId}
              project={project}
              isFeatured={project.isFeatured}
              onDelete={isAuthenticated ? handleDelete : undefined}
              onEdit={isAuthenticated ? setEditingProject : undefined}
              onView={setSelectedProject}
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

      {editingProject && (
        <EditProjectModal
          project={editingProject}
          onClose={() => setEditingProject(null)}
          onUpdated={loadProjects}
        />
      )}

      {selectedProject && (
        <ViewProjectModal
          project={selectedProject}
          onClose={() => setSelectedProject(null)}
        />
      )}
    </main>
  );
}