import AboutSection from "./AboutSection";
import TerminalIntro from "./TerminalIntro";
import FeaturedProjects from "./FeaturedProjects";
import ActiveProjects from "./ActiveProjects";

export default function HomePage() {
  return (
    <main>
      <TerminalIntro />
      <AboutSection />
      <FeaturedProjects />
      <ActiveProjects />
    </main>
  );
}