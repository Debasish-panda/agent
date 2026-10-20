# AI Voice Customer Service SaaS MVP

This repository contains a production-oriented MVP-1 for a multi-tenant AI voice customer service platform. It includes a Spring Boot backend, an Angular admin dashboard, PostgreSQL, Redis, Docker Compose integration, and a mock telephony/CRM environment for local demos.

## What is included

- Multitenant architecture designed from day one
- JWT authentication and role-based authorization
- AI provider abstraction with mock/default behavior
- Knowledge base and basic RAG-style retrieval
- Tool execution framework for support workflows
- Mock telephony, CRM, and AI providers for local demo
- Angular admin dashboard and call simulator
- Docker Compose for PostgreSQL, Redis, backend, and frontend

## Demo credentials

- Email: admin@democommerce.com
- Password: DemoPass123!

## Quick start

```bash
docker compose up --build
```

Then open:
- Frontend: http://localhost:4200
- Backend: http://localhost:8080

## Documentation

- `docs/ARCHITECTURE.md`
- `docs/MVP1_PLAN.md`
- `docs/API.md`
- `docs/DATABASE.md`
- `docs/SETUP.md`
- `docs/SECURITY.md`

## Notes

This is MVP-1 and intentionally relies on mock external providers. It is designed to run without API keys and to support future provider swaps.
