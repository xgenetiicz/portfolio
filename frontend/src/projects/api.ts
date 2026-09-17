import client from "../../api/client"; // importing axios
import type { ProjectDTO } from "./types";

export async function getProjects(): Promise<ProjectDTO[]> {
  const response = await client.get<ProjectDTO[]>("/projects/fetchProjects"); //and call on backend here with axios and the rest of the API.
  return response.data;
}