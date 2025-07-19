# 🎥 Livra — A Video Sharing Platform

Livra is a fully self-built, end-to-end video sharing platform.  
It allows users to upload, watch, and interact with videos through a mobile-first UI backed by a scalable, containerized microservice setup.

---

## 🚀 Live Demo

🔗 [avkarthick.in](https://avkarthick.in) | [livra.in](https://livra.in)

---

## 🧱 Tech Stack

### 🔹 Frontend
- **Angular 17**
- Scroll-snap autoplay feed (like Reels/Shorts)
- Hover-to-play thumbnails (desktop)
- **Fully responsive design** — works seamlessly on mobile, laptops, and larger screens
- OIDC authentication (angular-auth-oidc-client)
- **Client-side pagination** for optimized feed loading
- Secure multipart S3 uploads using pre-signed URLs

### 🔹 Backend
- **Spring Boot (Java 17)**
- RESTful APIs with pagination and modular routing
- JWT-based security with OIDC integration
- **Redis** caching for:
  - Personalized feed (with debounce-based refresh)
  - Thumbnail URL caching
  - Rate limiting based on IP/user
- FFmpeg integration (runs inside Docker) for thumbnail generation

### 🔹 Storage & Infrastructure
- **MongoDB Atlas** for user and video metadata
- **AWS S3** for video and image file storage
- **Redis** for caching and control logic
- **Docker & Docker Compose** for dev/production environment
- **NGINX** as reverse proxy and SSL handler (via Certbot)
- **AWS EC2 (Free Tier)** hosting

---

## 🔑 Core Features

### 👤 User Experience
- Guests can browse public/trending videos
- Logged-in users get **personalized feed** based on:
  - Watch history
  - Likes
  - Subscriptions

### 📺 Video Feed
- Shorts-style vertical scrollable feed
- Autoplay using Intersection Observer
- Hover-to-preview thumbnails on desktop
- **Paginated API** for scalable feed loading
- **Watch history & liked videos** saved per user

### ⏫ Upload System
- Multipart video upload using pre-signed S3 URLs
- Automatic thumbnail extraction via FFmpeg
- File validation and retry logic on client side

### 🛡 Security
- OAuth2 OIDC integration (client + server)
- Backend JWT validation
- Redis-backed IP rate limiting on APIs

### ⚙️ Developer Experience
- Fully containerized setup (Angular, Spring Boot, Redis)
- Modular codebase for future scalability (comments, playlists, live chat, etc.)
- Clean separation of concerns between API layers and services

---

## 🧪 Challenges Tackled

- Redis connection issues from Docker containers in EC2
- FFmpeg container failures with vague logs (Exit code 8)
- OIDC secureRoutes breaking silently behind NGINX reverse proxy
- Memory constraints on EC2 Free Tier under real-time load

---

## 🧰 Running Locally

### Prerequisites
- Docker + Docker Compose installed
- MongoDB Atlas URI
- AWS S3 bucket + IAM credentials
- Redis setup (locally or containerized)
- OIDC client credentials (Keycloak/Auth0 or similar)

### Clone & Run

```bash
git clone https://github.com/Karthick6079/video-sharing-app.git
cd video-sharing-app
docker-compose up --build
