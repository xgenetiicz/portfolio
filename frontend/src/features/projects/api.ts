import client from "../../api/client";
import type { ProjectDTO } from "./types";

export async function getProjects(): Promise<ProjectDTO[]> {
  const response = await client.get<ProjectDTO[]>("/projects/fetchProjects");
  return response.data;
}

export async function createProject(project: ProjectDTO): Promise<number> {
  const response = await client.post<number>("/projects/addproject", project);
  return response.data;
}

export async function uploadProjectCoverImage(projectId: number, file: File): Promise<string> {
  const formData = new FormData();
  formData.append("file", file);
  const response = await client.post<string>(`/content/upload/image/${projectId}`, formData);
  return response.data;
}

export async function deleteProject(projectId: number): Promise<void> {
  await client.delete(`/projects/delete/${projectId}`);
}