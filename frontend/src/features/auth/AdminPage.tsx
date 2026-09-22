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
      <h1><p className="text-[40px]">Welcome back, Genti!</p></h1>
    </main>
  );
}