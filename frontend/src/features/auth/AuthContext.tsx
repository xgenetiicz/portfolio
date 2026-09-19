import { createContext, useContext, useState } from "react";
import type { ReactNode } from "react";

const TOKEN_STORAGE_KEY = "buildhub_token";

interface AuthContextValue {
  isAuthenticated: boolean;
  login: (token: string) => void;
  logout: () => void;
}

// null default so useAuth() below can detect "used outside a provider" and
// fail loudly instead of silently returning garbage.
const AuthContext = createContext<AuthContextValue | null>(null);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider(props: AuthProviderProps) {
  // Lazy initializer: this function only runs once, on first mount, so a
  // page refresh still picks up a token that was already stored — you
  // don't get logged out just by reloading the page.
  const [token, setToken] = useState<string | null>(function readStoredToken() {
    return localStorage.getItem(TOKEN_STORAGE_KEY);
  });

  function login(newToken: string) {
    localStorage.setItem(TOKEN_STORAGE_KEY, newToken);
    setToken(newToken);
  }

  function logout() {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    setToken(null);
  }

  const value: AuthContextValue = {
    isAuthenticated: token !== null,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{props.children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (context === null) {
    throw new Error("useAuth must be used inside an AuthProvider.");
  }
  return context;
}