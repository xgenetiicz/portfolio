import type { ButtonHTMLAttributes, ReactNode } from "react";

interface FabButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
}

export default function FabButton({ className = "", children, ...rest }: FabButtonProps) {
  return (
    <button
      className={`fixed bottom-8 right-8 z-40 flex h-14 w-14 items-center justify-center rounded-full bg-accent text-2xl font-bold text-bg shadow-lg shadow-accent/30 transition-transform duration-150 ease-out hover:scale-110 active:scale-95 ${className}`}
      {...rest}
    >
      {children}
    </button>
  );
}