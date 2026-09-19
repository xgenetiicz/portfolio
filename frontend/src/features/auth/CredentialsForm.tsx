import { useState } from "react";
import type { FormEvent } from "react";

interface CredentialsFormProps {
  email: string;
  onEmailChange: (value: string) => void;
  password: string;
  onPasswordChange: (value: string) => void;
  isSubmitting: boolean;
  error: string | null;
  onSubmit: (event: FormEvent) => void;
}

export default function CredentialsForm(props: CredentialsFormProps) {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="rounded-2xl border border-line bg-surface p-8 shadow-[0_24px_70px_rgba(0,0,0,0.45)] md:p-10">
      <span className="text-xs font-bold tracking-wide text-accent">$ login</span>
      <h1 className="mb-1 mt-2.5 text-xl font-bold text-text md:text-[22px]">Welcome back</h1>
      <p className="mb-7 text-[13px] leading-relaxed text-muted">Sign in to manage your projects.</p>

      <form onSubmit={props.onSubmit}>
        <div className="mb-[18px]">
          <label htmlFor="email" className="mb-[7px] block text-[11px] font-bold uppercase tracking-wider text-muted">
            Email
          </label>
          <input
            id="email"
            type="email"
            required
            autoComplete="email"
            value={props.email}
            onChange={(event) => props.onEmailChange(event.target.value)}
            placeholder="you@example.com"
            className="w-full rounded-[10px] border border-line bg-bg px-3.5 py-3 text-sm text-text outline-none transition-[border-color,box-shadow] placeholder:text-muted/70 focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)]"
          />
        </div>

        <div className="mb-[18px]">
          <label htmlFor="password" className="mb-[7px] block text-[11px] font-bold uppercase tracking-wider text-muted">
            Password
          </label>
          <div className="relative">
            <input
              id="password"
              type={showPassword ? "text" : "password"}
              required
              autoComplete="current-password"
              value={props.password}
              onChange={(event) => props.onPasswordChange(event.target.value)}
              placeholder="••••••••"
              className="w-full rounded-[10px] border border-line bg-bg px-3.5 py-3 pr-11 text-sm text-text outline-none transition-[border-color,box-shadow] placeholder:text-muted/70 focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)]"
            />
            <button
              type="button"
              aria-label={showPassword ? "Hide password" : "Show password"}
              onClick={() => setShowPassword((previous) => !previous)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-muted transition-colors hover:text-text"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="h-[18px] w-[18px]"
              >
                <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
            </button>
          </div>
        </div>

        <div className="mb-6 flex justify-end">
          <button type="button" className="text-xs font-semibold text-accent hover:underline">
            Forgot password?
          </button>
        </div>

        {props.error && <p className="mb-4 text-xs font-medium text-red-400">{props.error}</p>}

        <button
          type="submit"
          disabled={props.isSubmitting}
          className="w-full rounded-[10px] bg-accent py-3 text-sm font-bold tracking-wide text-bg transition-[filter,transform] hover:brightness-110 active:translate-y-px disabled:cursor-not-allowed disabled:opacity-60"
        >
          {props.isSubmitting ? "Sending…" : "Send login code"}
        </button>
      </form>

      <p className="mt-[22px] text-center text-[11px] tracking-wide text-muted">Admin access only</p>
    </div>
  );
}