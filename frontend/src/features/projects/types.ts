export type ContentType =
  | "JPEG"
  | "PNG"
  | "HEIC"
  | "WEBP"
  | "GIF"
  | "MP4"
  | "MOV"
  | "WEBM"
  | "PDF";

export interface ContentDTO {
  contentId: number;
  contentType: string;
  filePath: string;
  fileSize: number;
  projectId: number;
  uploadDate: string;
}

export interface ProjectDTO {
  projectId: number; // It is here because frontend must have a way to fetch it to reveal the project from backend. Also contents get uploaded based on projectId.
  projectName: string;
  projectDescription: string;
  keywords: string[];
  isActive: boolean;
  projectURL: string;
  startDate: string;
  endDate: string | null;
  imagePath: string | null;
  content: ContentDTO[];
}