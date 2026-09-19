import type {ProjectDTO} from "./types";

export type ProjectStatus = "ongoing" | "active" | "inactive";

export function getProjectStatus(project: ProjectDTO): ProjectStatus {
    if (!project.isActive){
        return "inactive";
        } else if(project.endDate) {
            return "active"
            } else {
                return "ongoing"
                }
    }
