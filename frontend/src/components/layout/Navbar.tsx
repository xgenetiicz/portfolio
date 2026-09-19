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

const mobileNavLinkClasses = ({ isActive }: { isActive: boolean }) =>
  [
    "relative border-t border-line px-5 py-4 text-[15px] font-medium transition-colors",
    "before:content-[''] before:absolute before:left-0 before:top-0 before:h-full before:w-0 before:bg-accent before:transition-all",
    isActive
      ? "text-text before:w-[3px]"
      : "text-muted hover:text-text hover:before:w-[3px]",
  ].join(" ");

export default function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  function closeMenu() {
    setIsMenuOpen(false);
  }

  return (
    <nav className="relative border-b border-line bg-surface font-mono">
      <div className="mx-auto flex h-[68px] max-w-[1120px] items-center justify-between px-5 md:h-[90px] md:px-8">
        <Link to="/" onClick={closeMenu} className="text-lg font-bold tracking-tight text-text md:text-[19px]">
          Build<span className="text-accent">Hub</span>
        </Link>

        {/* Desktop links */}
        <div className="hidden items-center gap-9 md:flex">
          <NavLink to="/" end className={navLinkClasses}>
            Home
          </NavLink>
         <NavLink to="/projects" className={navLinkClasses}>
           Projects
         </NavLink>
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

        {/* Burger button, mobile only */}
        <button
          type="button"
          aria-label="Open menu"
          onClick={() => setIsMenuOpen(true)}
          className="flex h-9 w-9 flex-col items-center justify-center gap-1.5 rounded-lg border border-line md:hidden"
        >
          <span className="h-0.5 w-[18px] rounded-full bg-text" />
          <span className="h-0.5 w-[18px] rounded-full bg-text" />
          <span className="h-0.5 w-[18px] rounded-full bg-text" />
        </button>
      </div>

      {/* Scrim */}
      <div
        onClick={closeMenu}
        aria-hidden="true"
        className={`fixed inset-0 z-40 bg-black/60 transition-opacity duration-300 md:hidden ${
          isMenuOpen ? "opacity-100" : "pointer-events-none opacity-0"
        }`}
      />

      {/* Slide-in drawer */}
      <div
        className={`fixed inset-y-0 right-0 z-50 flex w-[82%] max-w-xs flex-col border-l border-line bg-surface transition-transform duration-300 ease-in-out md:hidden ${
          isMenuOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        <div className="flex h-[68px] items-center justify-between border-b border-line px-5">
          <span className="text-lg font-bold text-text">
            Build<span className="text-accent">Hub</span>
          </span>
          <button
            type="button"
            aria-label="Close menu"
            onClick={closeMenu}
            className="flex h-9 w-9 items-center justify-center rounded-lg border border-line text-muted transition-colors hover:text-text"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" className="h-3.5 w-3.5">
              <path d="M18 6 6 18M6 6l12 12" />
            </svg>
          </button>
        </div>

        <NavLink to="/" end onClick={closeMenu} className={mobileNavLinkClasses}>
          Home
        </NavLink>
        <NavLink to="/projects" onClick={closeMenu} className={mobileNavLinkClasses}>
          Projects
        </NavLink>
        <NavLink to="/contact" onClick={closeMenu} className={mobileNavLinkClasses}>
          Contact
        </NavLink>
        <Link to="/login" onClick={closeMenu} className="border-t border-line px-5 py-4 text-[15px] font-bold text-accent">
          Login
        </Link>
      </div>
    </nav>
  );
}