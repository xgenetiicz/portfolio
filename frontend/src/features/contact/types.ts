export type ContactTopic = "Offers" | "Request" | "Ideas" | "justSayHi" | "Issues";

export interface ContactFormRequest {
  contactTopic: ContactTopic;
  firstName: string;
  lastName: string;
  email: string;
  message: string;
}