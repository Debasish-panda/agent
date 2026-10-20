# Backend API contract

All API routes except login and health require `Authorization: Bearer <token>`. All resource reads and writes are scoped to the tenant of the authenticated user.

## Authentication

- `POST /api/auth/login` — `{ "email": "...", "password": "..." }`; responds with `{ "token": "...", "user": { "id", "email", "role", "tenantId" } }`.
- `GET /api/auth/me` — authenticated user.

## Knowledge

- `POST /api/knowledge` — `{ "title": "...", "content": "..." }`. Sensitive email, phone, and card-like values are redacted before persistence.
- `GET /api/knowledge` — tenant documents.
- `PUT /api/knowledge/{id}` — update a tenant document and rebuild its search chunks.
- `DELETE /api/knowledge/{id}` — delete a tenant document and its chunks.
- `POST /api/knowledge/search` — `{ "query": "..." }`; returns matching documents with redaction applied.

## Customers and conversations

- `GET /api/customers`; `POST /api/customers` — `{ "name": "...", "email": "...", "phone": "..." }`.
- `POST /api/conversations` — `{ "customerId": 1 }` or customer fields (`customerName`, `customerEmail`, `customerPhone`), plus required `message` and optional `channel`.
- `GET /api/conversations` — tenant conversation cards; `GET /api/conversations/{id}` — persisted conversation and transcript.
- `POST /api/conversations/{id}/messages` — `{ "content": "..." }`; saves the customer turn, mock tool/AI output, and updated conversation.

Conversation messages automatically invoke mock tools for order IDs, complaints/delays, and requests to speak with a person. `ORD-10001` returns `OUT_FOR_DELIVERY` with expected delivery tomorrow.

## Operations and demo tools

- `GET /api/tickets` or `GET /api/support-tickets` — tenant support tickets.
- `GET /api/escalations` — persisted handoffs.
- `GET /api/tools/executions` — tool execution audit history.
- `POST /api/tools/get_order_status` — `{ "conversationId": 1, "arguments": { "orderId": "ORD-10001" } }`.
- `POST /api/tools/create_support_ticket` — conversation ID and `arguments` containing `subject`, `description`, optional `priority`.
- `POST /api/tools/transfer_to_human` — conversation ID and `arguments` containing `reason` and optional `context`.
- `GET /api/analytics/summary` — conversation, open-ticket, escalation, tool, customer, and knowledge totals.
- `GET /api/system/health` — public liveness and Redis status.

## Local demo

Default seeded login: `admin@democommerce.com` / `DemoPass123!`. Configure database and Redis through `SPRING_DATASOURCE_*`, `REDIS_HOST`, `REDIS_PORT`, and `REDIS_PASSWORD`. Set a secure `JWT_SECRET` (at least 32 bytes) outside local development. AI/CRM/telephony providers are mock-only and require no API keys.
