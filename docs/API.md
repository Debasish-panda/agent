# API Reference

## Authentication

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
    "role": "TENANT_ADMIN"
  }
}
```

### GET /api/auth/me

Returns the currently authenticated user.

## Knowledge

### POST /api/knowledge

Creates a knowledge document.

### GET /api/knowledge

Lists knowledge documents for the authenticated tenant.

### POST /api/knowledge/search

Executes a search across relevant knowledge chunks.

## Customers

### GET /api/customers

List customers for the current tenant.

## Conversations

### POST /api/conversations

Create a conversation record.

## Analytics

### GET /api/analytics/summary

Returns summary KPIs for the tenant.
