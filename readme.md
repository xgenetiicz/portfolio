# gentirudi.buildhubs.net (my own portfolio page based on Buildhubs project)

A personal portfolio platform built from scratch to showcase my projects and journey as a backend developer. The goal is simple: give visitors a clean, interactive way to explore what I have built, without having to dig through GitHub repositories.
The visitors have the possibility too see videos, images and other attachments listed to their unique projects posted by me, and also provided URL for some applications.
The interacting part should include having the possibility to give stars(planned) and contact me through email by interest.

## **Changes from BuildHubs Project**
- There are roles, but only the admin that is me will provide projects here.
- The idea is to have the possibility to present myself - since the scope of the other project is too big right now.
- A lot has been reused - that is why i have duplicated the project into this as `portfolio`.

## What I built so far
All of these changes exists still in this new project - but i will remove that others can register and log in - since i don't want
other users to have access login to my private page!

The backend is a RESTful API built with Java and Spring Boot, designed around security from the ground up. Authentication is handled with JSON Web Tokens (JWT) & OTP. 
These keywords are features from BuildHubs - but these are also perfect to be reused and have been a great way of learning on how to implement security features with different roles.

#### **BY FURTHER INTEREST ON IMPLEMENTATION:**
Check out [Docs](Docs), where this explains progress more in depth for each feature. (PS: This contains also documentation from `BuildHubs` project)

## API Endpoints

- POST /api/auth/register - register new user // **Removed**
- POST /api/auth/verify - verify email with code // **Removed**

- POST /api/auth/login - validate credentials, sends OTP
- POST /api/auth/verify/otp - verify OTP, returns JWT 
- POST /api/projects/addproject - admin only, create project
- GET  /api/projects/fetchProjects - fetch all projects
- POST /api/mail/contact - public, send a message through contact form.
- POST /api/auth/forgot/password - request password reset code
- POST /api/auth/reset/password - reset password with OTP code
- DELETE /api/projects/delete/{projectId} - admin only, delete project
- POST /api/content/upload/image/{projectId} - admin only, upload cover image
- GET /api/content/project/{projectId} - fetch all content within the project
- POST /api/content/upload/files/{projectId} - upload files/content to the assigned project
- DELETE /api/content/delete/{projectId}/{contentId} - delete the selected content based on assigned project.
- DELETE /api/content/delete/image/{projectId} - delete the cover image of the project.
- UPDATE /api/projects/update/{projectId} - update the current project that is published.

## Projects
The projects are shown in cards, and include information such as;

1. Project name
2. Description
3. Date start and end
4. URL for project (could be a repository, web-page or something else)
5. Image that visualize the card (project)
6. Attachments with possibility for download of .zip
   & also videos and images


## Tech Stack

**Hardware & deployment**
Im going to use my own hardware, and use Caddy for reverse proxy and also run this as an stack with Docker on portainer(GUI -  for containerstacks).
- Raspberry Pi 16GB RAM
- Cloudflare with Caddyfile for reverseproxy
- Dockerfile and correct application.yaml services.
- GitOps for automatic updates and deployment

**Backend so far**
- Java  
- Spring Boot,Spring Security + JWT Token Auth
- BCrypt password hashing
- Multipart file
- JavaMail (SMTP via Gmail)
- Docker
- PostgreSQL
- Maven
- Postman
- React + Vite + Tailwind CSS
- Typescript

  
## Author
**Genti Rudi (xgenetiicz)** - Java Developer
