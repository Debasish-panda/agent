# Setup Guide

## Requirements

- Java 21+
- Maven 3.9+
- Node.js 20+
- Angular CLI 19+
- Docker Compose

## Local backend run

```bash
cd backend
./mvnw spring-boot:run
```

## Local frontend run

```bash
cd frontend
npm install
npm start
```

## Docker Compose

```bash
docker compose up --build
```

## Environment variables

Copy `.env.example` and adjust values.
