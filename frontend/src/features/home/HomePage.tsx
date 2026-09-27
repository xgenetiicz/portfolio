import AboutSection from "./AboutSection";
import TerminalIntro from "./TerminalIntro";
import FeaturedProjects from "./FeaturedProjects";
import ActiveProjects from "./ActiveProjects";
import ViewAllProjectsButton from "./ViewAllProjectsButton";

export default function HomePage() {
  return (
    <main>
      <TerminalIntro />
      <AboutSection />
      <FeaturedProjects />
      <ActiveProjects />
      <ViewAllProjectsButton />
    </main>
  );
}