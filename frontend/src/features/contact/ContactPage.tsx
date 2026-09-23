import { useState } from "react";
import type { FormEvent } from "react";
import axios from "axios";
import { sendContactForm } from "./api";
import type { ContactFormRequest, ContactTopic } from "./types";

const TOPIC_OPTIONS: ContactTopic[] = ["Offers", "Ideas", "Request", "Issues", "justSayHi"];

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

export default function ContactPage() {
  const [contactTopic, setContactTopic] = useState<ContactTopic>("justSayHi");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isSent, setIsSent] = useState(false);

  function resetForm() {
    setContactTopic("justSayHi");
    setFirstName("");
    setLastName("");
    setEmail("");
    setMessage("");
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    const contactForm: ContactFormRequest = {
      contactTopic: contactTopic,
      firstName: firstName,
      lastName: lastName,
      email: email,
      message: message,
    };

    try {
      await sendContactForm(contactForm);
      setIsSent(true);
      resetForm();
    } catch (submitError) {
      setError(extractErrorMessage(submitError, "Could not send your message. Try again shortly."));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="relative flex min-h-[calc(100vh-68px)] items-center justify-center overflow-hidden bg-bg bg-[radial-gradient(circle_at_50%_30%,color-mix(in_srgb,var(--color-surface)_60%,transparent)_0%,var(--color-bg)_70%)] px-6 py-12 font-mono md:min-h-[calc(100vh-90px)]">
      <div className="w-full max-w-[480px] rounded-2xl border border-line bg-surface p-8 shadow-[0_24px_70px_rgba(0,0,0,0.45)] md:p-10">
        <span className="text-xs font-bold tracking-wide text-accent">$ contact</span>
        <h1 className="mb-1 mt-2.5 text-xl font-bold text-text md:text-[22px]">Get in touch</h1>
        <p className="mb-7 text-[13px] leading-relaxed text-muted">Pick a topic and I'll get back to you.</p>

        {isSent ? (
          <div className="rounded-[10px] border border-accent/40 bg-accent/8 px-4 py-5 text-center">
            <p className="text-sm font-semibold text-accent">Message sent.</p>
            <p className="mt-1 text-[13px] text-muted">Thanks for reaching out — I'll reply by email.</p>
            <button
              type="button"
              onClick={() => setIsSent(false)}
              className="mt-4 text-xs font-semibold text-accent hover:underline"
            >
              Send another message
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="mb-[18px]">
              <label htmlFor="contactTopic" className="mb-[7px] block text-[11px] font-bold uppercase tracking-wider text-muted">
                Topic
              </label>
              <select
                id="contactTopic"
                required
                value={contactTopic}
                onChange={(event) => setContactTopic(event.target.value as ContactTopic)}
                className="w-full rounded-[10px] border border-line bg-bg px-3.5 py-3 text-sm text-text outline-none transition-[border-color,box-shadow] focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)]"
              >
                {TOPIC_OPTIONS.map(function renderTopicOption(topic) {
                  return (
                    <option key={topic} value={topic}>
                      {topic}
                    </option>
                  );
                })}
              </select>
            </div>

            <div className="mb-[18px] grid grid-cols-2 gap-3">
              <div>
                <label htmlFor="firstName" className="mb-[7px] block text-[11px] font-bold uppercase tracking-wider text-muted">
                  First name
                </label>
                <input
                  id="firstName"
                  type="text"
                  required
                  value={firstName}
                  onChange={(event) => setFirstName(event.target.value)}

                  className="w-full rounded-[10px] border border-line bg-bg px-3.5 py-3 text-sm text-text outline-none transition-[border-color,box-shadow] placeholder:text-muted/70 focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)]"
                />
              </div>

              <div>
                <label htmlFor="lastName" className="mb-[7px] block text-[11px] font-bold uppercase tracking-wider text-muted">
                  Last name
                </label>
                <input
                  id="lastName"
                  type="text"
                  required
                  value={lastName}
                  onChange={(event) => setLastName(event.target.value)}

                  className="w-full rounded-[10px] border border-line bg-bg px-3.5 py-3 text-sm text-text outline-none transition-[border-color,box-shadow] placeholder:text-muted/70 focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)]"
                />
              </div>
            </div>

            <div className="mb-[18px]">
              <label htmlFor="email" className="mb-[7px] block text-[11px] font-bold uppercase tracking-wider text-muted">
                Email
              </label>
              <input
                id="email"
                type="email"
                required
                autoComplete="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="you@example.com"
                className="w-full rounded-[10px] border border-line bg-bg px-3.5 py-3 text-sm text-text outline-none transition-[border-color,box-shadow] placeholder:text-muted/70 focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)]"
              />
            </div>

            <div className="mb-6">
              <label htmlFor="message" className="mb-[7px] block text-[11px] font-bold uppercase tracking-wider text-muted">
                Message
              </label>
              <textarea
                id="message"
                required
                minLength={10}
                rows={5}
                value={message}
                onChange={(event) => setMessage(event.target.value)}
                placeholder="What's on your mind?"
                className="w-full resize-none rounded-[10px] border border-line bg-bg px-3.5 py-3 text-sm text-text outline-none transition-[border-color,box-shadow] placeholder:text-muted/70 focus:border-accent focus:shadow-[0_0_0_3px_color-mix(in_srgb,var(--color-accent)_22%,transparent)]"
              />
              <p className="mt-1.5 text-[11px] text-muted">Minimum 10 characters.</p>
            </div>

            {error && <p className="mb-4 whitespace-pre-line text-xs font-medium text-red-400">{error}</p>}

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full rounded-[10px] bg-accent py-3 text-sm font-bold tracking-wide text-bg transition-[filter,transform] hover:brightness-110 active:translate-y-px disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isSubmitting ? "Sending…" : "Send message"}
            </button>
          </form>
        )}
      </div>
    </main>
  );
}