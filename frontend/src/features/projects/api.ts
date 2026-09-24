import client from "../../api/client";
import type { ProjectDTO, ContentDTO } from "./types";


export type NewProjectInput = Omit<ProjectDTO, "projectId" | "imagePath" | "content">;

export async function getProjects(): Promise<ProjectDTO[]> {
  const response = await client.get<ProjectDTO[]>("/projects/fetchProjects");
  return response.data;
}

export async function createProject(project: NewProjectInput): Promise<number> {
  const response = await client.post<number>("/projects/addproject", project);
  return response.data;
}

export async function uploadProjectCoverImage(projectId: number, file: File): Promise<string> {
  const formData = new FormData();
  formData.append("file", file);
  const response = await client.post<string>(`/content/upload/image/${projectId}`, formData);
  return response.data;
}

export async function uploadProjectContentFiles(projectId: number, files: File[]): Promise<string[]> {
  const formData = new FormData();
  files.forEach((file) => formData.append("content", file));
  const response = await client.post<string[]>(`/content/upload/files/${projectId}`, formData);
  return response.data;
}

export async function getContentForProject(projectId: number): Promise<ContentDTO[]> {
  const response = await client.get<ContentDTO[]>(`/content/project/${projectId}`);
  return response.data;
}

export async function deleteProject(projectId: number): Promise<void> {
  await client.delete(`/projects/delete/${projectId}`);
}

export async function updateProject(projectId: number, project: ProjectDTO): Promise<string> {
  const response = await client.put<string>(`/projects/update/${projectId}`, project);
  return response.data;
}

export async function deleteContent(projectId: number, contentId: number): Promise<string> {
  const response = await client.delete<string>(`/content/delete/${projectId}/${contentId}`);
  return response.data;
}