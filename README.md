# Lab Collaboration & Weekly Report Management System (Spring Boot + Vue)

## Overview
This repository contains a full-stack system for:
- User authentication (JWT)
- Multi-lab/group management
- Project task collaboration
- Weekly report submission & supervisor review
- Audit & traceability

## Structure
- `backend/` Spring Boot API
- `frontend/` Vue 3 (Vite) web app
- `docker-compose.yml` local dev stack

## Quick Start (Docker)
```bash
docker compose up --build
```
- Frontend: http://localhost:5173
- Backend:  http://localhost:8080
- MySQL:    localhost:3306

## Default Accounts (seed)
- admin / admin123 (ADMIN)
- mentor / mentor123 (MENTOR)
- student / student123 (STUDENT)

## Dev without Docker
### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm i
npm run dev
```
