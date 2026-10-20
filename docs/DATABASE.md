# Database Design

## Core tables

- tenant
- users
- ai_agents
- customers
- knowledge_documents
- knowledge_chunks
- conversations
- conversation_messages
- support_tickets
- escalations
- tool_executions
- audit_logs

## Database principles

- Every tenant-scoped entity contains `tenant_id`
- Add indexes for access patterns like tenant, customer, ticket status, and conversation lookup
- Use Flyway migrations for schema evolution
- Use JSONB for provider configuration where flexibility is required
