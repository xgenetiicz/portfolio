# gentirudi.buildhubs.net (my own portofolio page based on Buildhubs project)

A personal portfolio platform built from scratch to showcase my projects and journey as a backend developer. The goal is simple: give visitors a clean, interactive way to explore what I have built, without having to dig through GitHub repositories.
The interacting part should include having the possibility to comment on each project, and give stars,
and also have the possibility to contact me through mail also by topic interests.

## **Changes from BuildHubs Project**
- There are roles, but only the admin that is me will provide projects here.
- The idea is to have the possibility to present myself - since the other project is very huge, and takes time.
- Soon to be done, and a lot can be reused - that is why i have duplicated the project into this as `portofolio`.

Thanks

## What I built so far
All of these changes exists still in this new project - but i will remove that others can register and log in - since i don't want
other users to have access login to my private page!

The backend is a RESTful API built with Java and Spring Boot, designed around security from the ground up. Authentication is handled with JSON Web Tokens (JWT) & OTP. 
These keywords are features from BuildHubs - but these are also perfect to be reused and have been a great way of learning on how to implement security features with different roles.

- users can register, verify their email with a one-time code. 
- users can log in after validating otp token, and their email needs to be verified.
- users gets issued a JWT token after successful validation on login with OTP.
- Visitors can now reach out through a contact form. Messages are routed to either my personal email, or my business account inbox depending on the topic selected, using a reply to header, so I can respond directly to the sender.

#### **BY FURTHER INTEREST ON IMPLEMENTATION:**
Check out [Docs](Docs), where this explains progress more in depth for each feature.

## API Endpoints

- POST /api/auth/register - register new user
- POST /api/auth/verify - verify email with code
- POST /api/auth/login - validate credentials, sends OTP
- POST /api/auth/verify/otp - verify OTP, returns JWT
- POST /api/projects/addproject - admin only, create project
- GET  /api/projects/fetchProjects - get all projects by user
- POST /api/mail/contact - public, send a message through contact form.

## Projects
The projects are shown in cards, and include information such as;

1. Project name
2. Description
3. Date start and end
4. URL for project (could be a repository, web-page or something else)
5. Image that visualize the card (project)

Could be maybe something else in the future, but this is the plan for now.

## Tech Stack

**Hardware & deployment**
Im going to use my own hardware, and use Caddy for reverse proxy and also run this as an stack with Docker on portainer(GUI -  for containerstacks).
- Raspberry Pi 16GB RAM
- Cloudflare with Caddyfile for reverseproxy
- Dockerfile and correct application.yaml services.

**Backend so far**
- Java 21 
- Spring Boot,Spring Security + JWT Token Auth
- BCrypt password hashing
- JavaMail (SMTP via Gmail)
- Docker
- PostgreSQL
- Maven
- Postman

**Frontend (future)**
- React + TypeScript
- Tailwind CSS

## Author
**Genti Rudi (xgenetiicz)** - Java Developer
