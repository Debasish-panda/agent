# API Reference

## Authentication

All routes except login and `GET /api/system/health` require a bearer token.
Resource access is scoped to the authenticated user's tenant.

### POST /api/auth/login

Request body:

```json
{
  "email": "admin@democommerce.com",
  "password": "DemoPass123!"
}
```

Response:

```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "email": "admin@democommerce.com",
    "role": "TENANT_ADMIN",
    "tenantId": 1
  }
}
```

### GET /api/auth/me

Returns the currently authenticated user.

## Knowledge

- `POST /api/knowledge` — create a document with `{ "title": "...", "content": "..." }`. Sensitive email, phone, and card-like values are redacted before persistence.
- `GET /api/knowledge` — list documents for the authenticated tenant.
- `PUT /api/knowledge/{id}` — update a tenant document and rebuild its search chunks.
- `DELETE /api/knowledge/{id}` — delete a tenant document and its chunks.
- `POST /api/knowledge/search` — search with `{ "query": "..." }`.

## Customers

- `GET /api/customers` — list customers for the current tenant.
- `POST /api/customers` — create a customer with `{ "name": "...", "email": "...", "phone": "..." }`.

## Conversations

- `POST /api/conversations` — create a conversation with a `message` and either `customerId` or customer fields (`customerName`, `customerEmail`, `customerPhone`); `channel` is optional.
- `GET /api/conversations` — list tenant conversation cards.
- `GET /api/conversations/{id}` — retrieve a conversation and persisted transcript.
- `POST /api/conversations/{id}/messages` — append a customer message as `{ "content": "..." }`; the mock workflow can invoke an order lookup, create a support ticket, or transfer the conversation to a human.

For order `ORD-10001`, the mock lookup returns `OUT_FOR_DELIVERY` and expected delivery tomorrow.

## Support and operations

- `GET /api/tickets` or `GET /api/support-tickets` — list tenant support tickets.
- `GET /api/escalations` — list persisted handoffs.
- `GET /api/tools/executions` — list tool execution history.
- `POST /api/tools/get_order_status` — execute with `{ "conversationId": 1, "arguments": { "orderId": "ORD-10001" } }`.
- `POST /api/tools/create_support_ticket` — execute with a conversation ID and `arguments` containing `subject`, `description`, and optional `priority`.
- `POST /api/tools/transfer_to_human` — execute with a conversation ID and `arguments` containing `reason` and optional `context`.

## Analytics and health

- `GET /api/analytics/summary` — return tenant totals for conversations, open tickets, escalations, tool executions, customers, and knowledge documents.
- `GET /api/system/health` — public liveness and Redis status.

The demo login is `admin@democommerce.com` / `DemoPass123!`. Full request and
response details are also maintained in [backend/API.md](../backend/API.md).
