import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function AdminPage() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <main className="flex min-h-[60vh] flex-col items-center justify-center gap-4 px-6 font-mono text-text">
      <p className="text-muted">Dashboard — coming soon.</p>
      <button
        type="button"
        onClick={handleLogout}
        className="rounded-lg border border-line px-4 py-2 text-sm font-semibold text-muted transition-colors hover:text-text"
      >
        Log out
      </button>
    </main>
  );
}