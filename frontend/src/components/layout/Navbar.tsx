import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../features/auth/AuthContext";

const navLinkClasses = ({ isActive }: { isActive: boolean }) =>
  [
    "relative py-1.5 text-sm font-medium transition-colors",
    "after:content-[''] after:absolute after:left-0 after:bottom-0 after:h-0.5 after:bg-accent after:transition-all",
    isActive
      ? "text-text after:w-full"
      : "text-muted hover:text-text after:w-0 hover:after:w-full",
  ].join(" ");

const mobileNavLinkClasses = ({ isActive }: { isActive: boolean }) =>
  [
    "relative border-t border-line px-5 py-4 text-[15px] font-medium transition-colors",
    "before:content-[''] before:absolute before:left-0 before:top-0 before:h-full before:w-0 before:bg-accent before:transition-all",
    isActive
      ? "text-text before:w-[3px]"
      : "text-muted hover:text-text hover:before:w-[3px]",
  ].join(" ");

const logoutIconPath = (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-3.5 w-3.5">
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
    <polyline points="16 17 21 12 16 7" />
    <line x1="21" y1="12" x2="9" y2="12" />
  </svg>
);

export default function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  function closeMenu() {
    setIsMenuOpen(false);
  }

  function handleLogout() {
    logout();
    closeMenu();
    navigate("/");
  }

  return (
    <nav className="relative border-b border-line bg-surface font-mono">
      <div className="mx-auto flex h-[68px] max-w-[1120px] items-center justify-between px-5 md:h-[90px] md:px-8">
        <Link to="/" onClick={closeMenu} className="flex items-center gap-2.5 text-lg font-bold tracking-tight text-text md:text-[19px]">
          <span>Genti's Build<span className="text-accent">Hub</span></span>
          {isAuthenticated && (
            <span className="inline-flex items-center gap-1.5 rounded-full border border-accent/35 bg-accent/10 px-2 py-0.5 text-[10px] font-bold tracking-wide text-accent">
              <span className="h-1.5 w-1.5 rounded-full bg-accent" />
              ADMIN
            </span>
          )}
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
          {isAuthenticated ? (
            <button
              type="button"
              onClick={handleLogout}
              className="inline-flex items-center gap-1.5 rounded-lg border border-accent px-4 py-2 text-[13px] font-bold tracking-wide text-accent transition-colors hover:bg-accent hover:text-bg"
            >
              {logoutIconPath}
              Log out
            </button>
          ) : (
            <Link
              to="/login"
              className="rounded-lg border border-accent px-[18px] py-2 text-[13px] font-bold tracking-wide text-accent transition-colors hover:bg-accent hover:text-bg"
            >
              Login
            </Link>
          )}
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
          <span className="flex items-center gap-2 text-lg font-bold text-text">
            Build<span className="text-accent">Hub</span>
            {isAuthenticated && (
              <span className="inline-flex items-center gap-1.5 rounded-full border border-accent/35 bg-accent/10 px-2 py-0.5 text-[9px] font-bold tracking-wide text-accent">
                <span className="h-1 w-1 rounded-full bg-accent" />
                ADMIN
              </span>
            )}
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
        {isAuthenticated ? (
        <button
          type="button"
          onClick={handleLogout}
          className="inline-flex items-center gap-1.5 rounded-lg border border-accent px-4 py-2 text-[13px] font-bold tracking-wide text-accent transition-colors hover:bg-accent hover:text-bg"
        >
          {logoutIconPath}
          Log out
        </button>
        ) : (
          <Link to="/login" onClick={closeMenu} className="border-t border-line px-5 py-4 text-[15px] font-bold text-accent">
            Login
          </Link>
        )}
      </div>
    </nav>
  );
}