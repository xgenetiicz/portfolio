import { Link } from "react-router-dom";

export default function ViewAllProjectsButton() {
  return (
    <section className="mx-auto max-w-6xl px-6 py-16 text-center">
      <p className="mb-4 text-text">Interested in more projects?</p>
      <Link
        to="/projects"
        className="inline-flex items-center gap-2 rounded-lg border border-line px-6 py-3 text-sm font-bold text-accent transition-colors hover:border-accent hover:bg-accent/10"
      >
        View all
      </Link>
    </section>
  );
}