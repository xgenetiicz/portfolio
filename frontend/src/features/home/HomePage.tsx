import AboutSection from "./AboutSection";
import TerminalIntro from "./TerminalIntro";
import FeaturedProjects from "./FeaturedProjects";

export default function HomePage() {
  return (
    <main>
      <TerminalIntro />
      <AboutSection />
      <FeaturedProjects />
    </main>
  );
}