const TECH_STACK = ["Java", "Spring Boot", "PostgreSQL", "Docker", "React", "TypeScript"];

export default function AboutSection() {
  return (
    <section className="max-w-6xl mx-auto px-6 py-16 border-t border-line">
      <h2 className="text-2xl font-bold text-text mb-4">About me</h2>
      <p className="text-muted leading-relaxed mb-4">
        I'm a backend developer who has always been drawn to figuring out how things work, fixing them when they don't,
        and even pushing them beyond what they were originally built to do.
        Whether it's code, a custom-built PC, a home server, or turning a Toyota Auris into a budget Tesla,
        the common denominator is that I've always preferred getting hands-on, experimenting, figuring things out, and solving problems myself.
      </p>
      <p className="text-muted leading-relaxed mb-6">
        My focus is backend development with <b>Java, Spring Boot, PostgreSQL, and Docker.</b> I work from database design and REST APIs through to deployment,
        while using this portfolio as a hands-on way to strengthen my full-stack skills by exploring <b>React and TypeScript.</b>
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
      </div>
    </section>
  );
}