import AboutSection from "./AboutSection";
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
      <AboutSection />
       <section
              id="projects"
              className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto px-4 py-10 scroll-mt-[90px]"
            >
        {isLoading && <p className="text-[#9fb3a8]">Laster prosjekter…</p>}
        {!isLoading && projects.length === 0 && (
          <p className="text-[#9fb3a8]">Ingen prosjekter enda.</p>
        )}
        {projects.map(function renderProject(project, index) {
          return (
            <ProjectCard
              key={project.projectId}
              project={project}
              isFeatured={index < 3}
            />
          );
        })}
      </section>
    </main>
  );
}