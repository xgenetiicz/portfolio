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
  filePath: string;
  fileSize: number;
  contentType: ContentType;
}

export interface ProjectDTO {
  projectId: number;
  projectName: string;
  projectDescription: string;
  projectURL: string;
  startDate: string;
  endDate: string | null;
  imagePath: string | null;
  content: ContentDTO[];
}