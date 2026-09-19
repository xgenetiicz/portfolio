import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { login, verifyOtp } from "./api";
import { useAuth } from "./AuthContext";
import CredentialsForm from "./CredentialsForm";
import OtpForm from "./OtpForm";

function extractErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === "string" && data.trim().length > 0) {
      return data;
    }
    if (data && typeof data === "object" && "message" in data) {
      const message = (data as { message?: unknown }).message;
      if (typeof message === "string" && message.trim().length > 0) {
        return message;
      }
    }
  }
  return fallback;
}

export default function LoginPage() {
  const navigate = useNavigate();
  const { login: setAuthenticated } = useAuth();

  const [step, setStep] = useState<"credentials" | "otp">("credentials");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleCredentialsSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      await login({ email, password });
      setStep("otp");
    } catch (submitError) {
      setError(extractErrorMessage(submitError, "Wrong email or password."));
    } finally {
      setIsSubmitting(false);
    }
  }

  // Called by OtpForm's "Resend code" button. Re-throws on failure so the
  // form knows NOT to clear its digit boxes when the resend itself failed.
  async function handleResendCode() {
    setError(null);
    setIsSubmitting(true);
    try {
      await login({ email, password });
    } catch (resendError) {
      setError(extractErrorMessage(resendError, "Could not resend the code."));
      throw resendError;
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleOtpSubmit(otpCode: string) {
    setError(null);
    setIsSubmitting(true);
    try {
      const loginResponse = await verifyOtp({ email, otpCode });
      setAuthenticated(loginResponse.token);
      navigate("/admin");
    } catch (verifyError) {
      setError(extractErrorMessage(verifyError, "Invalid or expired code."));
    } finally {
      setIsSubmitting(false);
    }
  }

  function closeOtpStep() {
    setStep("credentials");
    setError(null);
    setPassword("");
  }

  return (
    <main className="relative flex min-h-[calc(100vh-68px)] items-center justify-center overflow-hidden bg-bg bg-[radial-gradient(circle_at_50%_30%,color-mix(in_srgb,var(--color-surface)_60%,transparent)_0%,var(--color-bg)_70%)] px-6 py-12 font-mono md:min-h-[calc(100vh-90px)]">
      <div
        className={`w-full max-w-[400px] transition-[filter,opacity] duration-200 ${
          step === "otp" ? "pointer-events-none blur-[2px] opacity-30" : ""
        }`}
      >
        <CredentialsForm
          email={email}
          onEmailChange={setEmail}
          password={password}
          onPasswordChange={setPassword}
          isSubmitting={isSubmitting}
          error={step === "credentials" ? error : null}
          onSubmit={handleCredentialsSubmit}
        />
      </div>

      {step === "otp" && (
        <OtpForm
          email={email}
          isSubmitting={isSubmitting}
          error={error}
          onError={setError}
          onSubmit={handleOtpSubmit}
          onResend={handleResendCode}
          onClose={closeOtpStep}
        />
      )}
    </main>
  );
}