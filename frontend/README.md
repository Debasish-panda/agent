# Relay admin frontend

Standalone Angular 22 admin dashboard. The app uses the same-origin `/api` routes; `npm start` proxies them to a local backend at `http://localhost:8080`, while the Docker image proxies to the Compose `backend` service.

```bash
npm install
npm start
npm run build
npm test
```

The demo login is `admin@democommerce.com` / `DemoPass123!`. No demo conversation, ticket, or knowledge data is synthesized in the UI; records and assistant/tool output are read from the backend.
