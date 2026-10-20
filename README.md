# AI Voice Customer Service SaaS MVP

This repository contains MVP-1 for a multi-tenant AI voice customer service platform. It includes a Java 21 / Spring Boot backend, an Angular 22 admin dashboard, PostgreSQL, Redis, Docker Compose integration, and mock AI, CRM, and telephony providers for local demos.

## What is included

- Multitenant architecture designed from day one
- JWT authentication and role-based authorization
- AI provider abstraction with mock/default behavior
- Knowledge base and basic RAG-style retrieval
- Tool execution framework for support workflows
- Mock telephony, CRM, and AI providers for local demo
- Angular 22 admin dashboard and call simulator
- Docker Compose for PostgreSQL, Redis, backend, and frontend

## Requirements

For Docker Compose, install Docker with the Compose plugin. For local development,
install Java 21+, Maven 3.9+, and Node.js 22.22.3+ or 24.15+.

## Demo credentials

- Email: admin@democommerce.com
- Password: DemoPass123!

## Quick start with Docker

```bash
docker compose up --build
```

Open the frontend at http://localhost:4200 and the backend at
http://localhost:8080. The backend applies its Flyway migration and seeds the
demo tenant and administrator on startup. Mock providers require no API keys.

To run the applications locally, start PostgreSQL and Redis first, then follow
the backend and frontend steps in [docs/SETUP.md](docs/SETUP.md).

## Demo workflow

Sign in with the demo credentials, then use the Call Simulator:

1. Ask: `Hi, where is my order ORD-10001?` The mock order tool returns
   `OUT_FOR_DELIVERY` with delivery expected tomorrow.
2. Say: `I still want to complain about the delay.` A support ticket is
   created and appears in the ticket view.
3. Say: `I want to speak with someone.` An escalation and handoff context are
   persisted and shown in the dashboard.

Conversation transcripts, analytics, and tool results are persisted by the
backend and scoped to the authenticated tenant.

## Documentation

- `docs/ARCHITECTURE.md`
- `docs/MVP1_PLAN.md`
- `docs/API.md`
- `docs/DATABASE.md`
- `docs/SETUP.md`
- `docs/SECURITY.md`

The included demo account and local defaults are for development only. Set a
strong `JWT_SECRET` and replace the demo credentials before deployment.
