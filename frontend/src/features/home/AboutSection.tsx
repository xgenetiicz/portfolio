import { Link } from "react-router-dom";

const TECH_STACK = ["Java", "Spring Boot", "PostgreSQL", "Docker", "React", "TypeScript"];

export default function AboutSection() {
  return (
    <section className="max-w-3xl mx-auto px-6 py-16 border-t border-line">
      <h2 className="text-2xl font-bold text-text mb-4">About me</h2>
      <p className="text-muted leading-relaxed mb-4">
        I'm a self-taught backend developer specializing in Java and Spring Boot.
        Before writing my first line of code, I spent years in team leadership
        logistics and port operations, plus a background in professional football.
        That path taught me discipline and how to operate under pressure, which
        carried straight over into how I approach building software.
      </p>
      <p className="text-muted leading-relaxed mb-6">
        My focus is backend systems: Java, Spring Boot, PostgreSQL, and Docker.
        I build things end-to-end — from database design and REST APIs to
        deployment — and this portfolio itself is where I'm expanding into
        React and TypeScript to round out the full stack.
      </p>

      <div className="mb-8 flex flex-wrap gap-2">
        {TECH_STACK.map(function renderTech(tech) {
          return (
            <span
              key={tech}
              className="rounded-full border border-accent/25 bg-accent/8 px-3 py-[5px] text-xs font-semibold text-accent"
            >
              {tech}
            </span>
          );
        })}
      </div>

      <div className="flex flex-wrap gap-3">
        <Link
          to="/projects"
          className="inline-flex items-center justify-center gap-1.5 rounded-[10px] border border-accent px-[22px] py-[13px] text-sm font-bold font-mono text-accent transition-colors hover:bg-accent hover:text-bg"
        >
          View my projects
        </Link>
        <Link
          to="/contact"
          className="inline-flex items-center justify-center gap-1.5 rounded-[10px] border border-accent px-[22px] py-[13px] text-sm font-bold font-mono text-accent transition-colors hover:bg-accent hover:text-bg"
        >
          Get in touch
        </Link>
      </div>
    </section>
  );
}