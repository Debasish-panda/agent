# Setup Guide

## Requirements

- Java 21+
- Maven 3.9+
- Node.js 22.22.3+ or 24.15+
- Angular CLI 22+
- Docker Compose

## Local backend run

```bash
cd backend
mvn spring-boot:run
```

## Local frontend run

```bash
cd frontend
npm install
npm start
```

The frontend runs at `http://localhost:4200` and the API at
`http://localhost:8080`. Configure local database, Redis, and JWT settings with
the environment variables shown in `.env.example`; Docker Compose provides
PostgreSQL and Redis for a local demo.

## Docker Compose

```bash
docker compose up --build
```

## Environment variables

Copy `.env.example` and adjust values.

The bundled demo credentials are intended for local development only. Change
the demo password and JWT secret before exposing the application outside a
trusted development environment.
