import type { ButtonHTMLAttributes, ReactNode } from "react";

type ButtonVariant = "primary" | "outline" | "secondary" | "danger" | "ghost";
type ButtonSize = "sm" | "md";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  children: ReactNode;
}

const baseClasses =
  "inline-flex items-center justify-center gap-1.5 rounded-[10px] font-bold font-mono transition-colors disabled:opacity-50 disabled:pointer-events-none";

const variantClasses: Record<ButtonVariant, string> = {
  primary: "bg-accent text-bg hover:brightness-110",
  outline: "border border-accent text-accent hover:bg-accent hover:text-bg",
  secondary: "border border-line text-text hover:border-accent hover:text-accent",
  danger: "border border-line text-text hover:border-[#ff5c5c] hover:text-[#ff5c5c]",
  ghost: "text-muted hover:text-text",
};

const sizeClasses: Record<ButtonSize, string> = {
  sm: "px-3 py-[7px] text-xs rounded-lg",
  md: "px-[22px] py-[13px] text-sm",
};

export default function Button({
  variant = "secondary",
  size = "md",
  className = "",
  children,
  ...rest
}: ButtonProps) {
  return (
    <button
      className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      {...rest}
    >
      {children}
    </button>
  );
}