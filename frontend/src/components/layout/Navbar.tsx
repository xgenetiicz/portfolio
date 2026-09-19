import { useState } from "react";
import { Link, NavLink } from "react-router-dom";

const navLinkClasses = ({ isActive }: { isActive: boolean }) =>
  [
    "relative py-1.5 text-sm font-medium transition-colors",
    "after:content-[''] after:absolute after:left-0 after:bottom-0 after:h-0.5 after:bg-accent after:transition-all",
    isActive
      ? "text-text after:w-full"
      : "text-muted hover:text-text after:w-0 hover:after:w-full",
  ].join(" ");

const mobileLinkClasses =
  "border-t border-line px-5 py-4 text-[15px] font-medium text-muted transition-colors hover:text-text";

export default function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  function closeMenu() {
    setIsMenuOpen(false);
  }

  return (
    <nav className="relative border-b border-line bg-surface font-mono">
      <div className="mx-auto flex h-[68px] max-w-[1120px] items-center justify-between px-5 md:h-[90px] md:px-8">
        <Link
          to="/"
          onClick={closeMenu}
          className="text-lg font-bold tracking-tight text-text md:text-[19px]"
        >
          Build<span className="text-accent">Hub</span>
        </Link>

        {/* Desktop links */}
        <div className="hidden items-center gap-9 md:flex">
          <NavLink to="/" end className={navLinkClasses}>
            Home
          </NavLink>

           <a href="#projects"
            className="py-1.5 text-sm font-medium text-muted transition-colors hover:text-text"
          >
            Projects
          </a>
          <NavLink to="/contact" className={navLinkClasses}>
            Contact
          </NavLink>
          <Link
            to="/login"
            className="rounded-lg border border-accent px-[18px] py-2 text-[13px] font-bold tracking-wide text-accent transition-colors hover:bg-accent hover:text-bg"
          >
            Login
          </Link>
        </div>

        {/* Burger toggle, mobile only */}
        <button
          type="button"
          aria-label={isMenuOpen ? "Close menu" : "Open menu"}
          aria-expanded={isMenuOpen}
          onClick={() => setIsMenuOpen((open) => !open)}
          className="flex h-9 w-9 flex-col items-center justify-center gap-1.5 rounded-lg border border-line md:hidden"
        >
          <span
            className={`h-0.5 w-[18px] rounded-full bg-text transition-transform ${
              isMenuOpen ? "translate-y-2 rotate-45" : ""
            }`}
          />
          <span
            className={`h-0.5 w-[18px] rounded-full bg-text transition-opacity ${
              isMenuOpen ? "opacity-0" : ""
            }`}
          />
          <span
            className={`h-0.5 w-[18px] rounded-full bg-text transition-transform ${
              isMenuOpen ? "-translate-y-2 -rotate-45" : ""
            }`}
          />
        </button>
      </div>

      {/* Mobile menu */}
      <div
        className={`flex flex-col overflow-hidden transition-[max-height] duration-[250ms] ease-in-out md:hidden ${
          isMenuOpen ? "max-h-[260px]" : "max-h-0"
        }`}
      >
        <Link to="/" onClick={closeMenu} className={mobileLinkClasses}>
          Home
        </Link>
        <a href="#projects" onClick={closeMenu} className={mobileLinkClasses}>
          Projects
        </a>
        <Link to="/contact" onClick={closeMenu} className={mobileLinkClasses}>
          Contact
        </Link>
        <Link
          to="/login"
          onClick={closeMenu}
          className="border-t border-line px-5 py-4 text-[15px] font-bold text-accent"
        >
          Login
        </Link>
      </div>
    </nav>
  );
}