import { useRef, useState } from "react";
import type { ClipboardEvent, FormEvent, KeyboardEvent } from "react";

const OTP_LENGTH = 6;

interface OtpFormProps {
  email: string;
  isSubmitting: boolean;
  error: string | null;
  onError: (message: string | null) => void;
  onSubmit: (otpCode: string) => void;
  onResend: () => Promise<void>;
  onClose: () => void;
}

export default function OtpForm(props: OtpFormProps) {
  const [otpDigits, setOtpDigits] = useState<string[]>(Array(OTP_LENGTH).fill(""));
  const otpInputRefs = useRef<Array<HTMLInputElement | null>>([]);

  function handleDigitChange(index: number, rawValue: string) {
    const digit = rawValue.replace(/\D/g, "").slice(-1);
    setOtpDigits(function updateDigits(previous) {
      const next = [...previous];
      next[index] = digit;
      return next;
    });
    if (digit && index < OTP_LENGTH - 1) {
      otpInputRefs.current[index + 1]?.focus();
    }
  }

  function handleKeyDown(index: number, event: KeyboardEvent<HTMLInputElement>) {
    if (event.key === "Backspace" && !otpDigits[index] && index > 0) {
      otpInputRefs.current[index - 1]?.focus();
    }
  }

  function handlePaste(event: ClipboardEvent<HTMLInputElement>) {
    const pasted = event.clipboardData.getData("text").replace(/\D/g, "").slice(0, OTP_LENGTH);
    if (!pasted) {
      return;
    }
    event.preventDefault();
    setOtpDigits(function fillFromPaste() {
      const next = Array(OTP_LENGTH).fill("");
      pasted.split("").forEach(function assignDigit(char, index) {
        next[index] = char;
      });
      return next;
    });
    otpInputRefs.current[Math.min(pasted.length, OTP_LENGTH - 1)]?.focus();
  }

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    const otpCode = otpDigits.join("");
    if (otpCode.length !== OTP_LENGTH) {
      props.onError("Enter all 6 digits.");
      return;
    }
    props.onSubmit(otpCode);
  }

  async function handleResendClick() {
    try {
      await props.onResend();
      setOtpDigits(Array(OTP_LENGTH).fill(""));
      otpInputRefs.current[0]?.focus();
    } catch {
      // onResend already reported the error to the parent via props.error
    }
  }

  return (
    <div
      onClick={props.onClose}
      className="fixed inset-0 z-50 flex items-end justify-center bg-bg/70 backdrop-blur-[1px] md:items-center md:p-6"
    >
      <form
        onSubmit={handleSubmit}
        onClick={(event) => event.stopPropagation()}
        className="relative w-full animate-[slideUp_0.22s_ease] rounded-t-[20px] border border-b-0 border-line bg-surface px-6 pb-8 pt-3.5 shadow-[0_-20px_60px_rgba(0,0,0,0.55)] md:w-[420px] md:animate-[modalPop_0.18s_ease] md:rounded-2xl md:border-b md:px-10 md:pb-8 md:pt-9 md:shadow-[0_30px_90px_rgba(0,0,0,0.55)]"
      >
        <div className="mx-auto mb-5 h-1 w-9 rounded-full bg-line md:hidden" />

        <button
          type="button"
          aria-label="Close"
          onClick={props.onClose}
          className="absolute right-[18px] top-[18px] hidden h-[26px] w-[26px] items-center justify-center rounded-md text-muted transition-colors hover:bg-bg hover:text-text md:flex"
        >
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round">
            <path d="M18 6 6 18M6 6l12 12" />
          </svg>
        </button>

        <span className="block text-xs font-bold tracking-wide text-accent">$ verify</span>
        <h1 className="mb-1 mt-2.5 text-xl font-bold text-text md:text-[22px]">Enter your code</h1>
        <p className="mb-6 text-[13px] leading-relaxed text-muted">
          We sent a 6-digit code to <strong className="font-semibold text-text">{props.email}</strong>
        </p>

        <div className="mb-[22px] flex gap-2.5">
          {otpDigits.map((digit, index) => (
            <input
              key={index}
              ref={(element) => {
                otpInputRefs.current[index] = element;
              }}
              value={digit}
              onChange={(event) => handleDigitChange(index, event.target.value)}
              onKeyDown={(event) => handleKeyDown(index, event)}
              onPaste={handlePaste}
              inputMode="numeric"
              maxLength={1}
              autoFocus={index === 0}
              className={`aspect-square w-full rounded-[10px] border bg-bg text-center text-lg font-bold text-text outline-none transition-[border-color,box-shadow] focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)] ${
                digit ? "border-accent" : "border-line"
              }`}
            />
          ))}
        </div>

        <div className="mb-6 flex items-center justify-end text-xs">
          <button
            type="button"
            onClick={handleResendClick}
            disabled={props.isSubmitting}
            className="font-semibold text-muted/80 transition-colors hover:text-text disabled:cursor-not-allowed"
          >
            Resend code
          </button>
        </div>

        {props.error && <p className="mb-4 text-xs font-medium text-red-400">{props.error}</p>}

        <button
          type="submit"
          disabled={props.isSubmitting}
          className="w-full rounded-[10px] bg-accent py-3 text-sm font-bold tracking-wide text-bg transition-[filter,transform] hover:brightness-110 active:translate-y-px disabled:cursor-not-allowed disabled:opacity-60"
        >
          {props.isSubmitting ? "Verifying…" : "Confirm & log in"}
        </button>

        <p className="mt-5 text-center text-[11px] text-muted">
          Wrong email?{" "}
          <button type="button" onClick={props.onClose} className="font-semibold text-muted hover:text-text hover:underline">
            Close and start over.
          </button>
        </p>
      </form>
    </div>
  );
}