import client from "../../api/client";
import type { ContactFormRequest } from "./types";

export async function sendContactForm(contactForm: ContactFormRequest): Promise<string> {
  const response = await client.post<string>("/mail/contact", contactForm);
  return response.data;
}