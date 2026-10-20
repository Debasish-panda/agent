# MVP 1 Plan

## Status Key

- `[COMPLETE]`: implementation exists and the requirement was verified.
- `[PARTIAL]`: implementation exists, but the requirement is not fully implemented or verified.
- `[NOT IMPLEMENTED]`: no implementation or passing verification is present in this checkout.

The backend and Angular 22 frontend implementations, database migration, tests, and Dockerfiles are now present. Frontend build, three unit tests, and dev-server HTTP readiness were reported passing with Node 24.15. This machine currently has Node 24.14, which is below Angular 22's minimum; a local rebuild/test attempt was blocked by that version check. Java, Maven, and Docker are not installed, so backend compilation, database migration, service startup, and end-to-end demo behavior remain unverified.

## MVP Requirements

### Foundations

- `[COMPLETE]` Repository setup and documentation.
- `[PARTIAL]` Docker Compose and environment defaults. Both application build contexts now exist, but Compose validation and service startup are unavailable in this environment.
- `[PARTIAL]` Java 21 / Spring Boot backend application scaffolding; compilation is unverified.
- `[COMPLETE]` Angular 22 frontend scaffolding; build, unit tests, and development server were reported passing with Node 24.15.

### Authentication, Tenancy, and Data

- `[PARTIAL]` Spring Boot application and backend API; compilation and runtime are unverified.
- `[PARTIAL]` Spring Security, JWT login, BCrypt password hashing, and authenticated user lookup; runtime behavior is unverified.
- `[PARTIAL]` Tenant/user entities, tenant-scoped queries, and demo seed data; database execution and isolation tests are unverified.
- `[PARTIAL]` Flyway migration and persisted application schema; migration execution is unverified.
- `[PARTIAL]` Tenant isolation is implemented in application queries but has not been verified across tenants.
- `[NOT IMPLEMENTED]` PostgreSQL migration execution has not been verified.
- `[PARTIAL]` Redis connection and health check are implemented but have not been run.

### Knowledge and AI

- `[PARTIAL]` AI provider abstraction and mock AI provider are implemented; backend execution is unverified.
- `[PARTIAL]` Tenant-filtered knowledge service, search, create/update/delete, and ingestion endpoints are implemented; backend execution is unverified.
- `[PARTIAL]` Sensitive-data redaction and unit tests are implemented; backend tests could not run in this environment.
- `[PARTIAL]` Mock AI response and conversation persistence are implemented; database-backed behavior is unverified.

### Tools, CRM, Telephony, and Orchestration

- `[PARTIAL]` Tool registry and mock tool execution are implemented; backend tests could not run here.
- `[PARTIAL]` `get_order_status` mock lookup is implemented for `ORD-10001`, with `OUT_FOR_DELIVERY` and expected delivery tomorrow; runtime result is unverified.
- `[PARTIAL]` `create_support_ticket` and persistence are implemented; database behavior is unverified.
- `[PARTIAL]` CRM provider abstraction and mock provider are implemented; runtime behavior is unverified.
- `[PARTIAL]` Telephony abstraction and mock provider are implemented; runtime behavior is unverified.
- `[PARTIAL]` `transfer_to_human` and persisted escalation/handoff context are implemented; database behavior is unverified.
- `[PARTIAL]` Conversation orchestration and transcript persistence are implemented; runtime behavior is unverified.

### Frontend and Simulator

- `[COMPLETE]` Angular 22 admin shell and Material-styled layout.
- `[COMPLETE]` Dashboard and call simulator use backend APIs.
- `[COMPLETE]` Conversation transcript view reads persisted messages from the API.
- `[COMPLETE]` Support ticket view reads ticket data from the API.
- `[COMPLETE]` Escalation/handoff context view reads escalation data from the API.
- `[COMPLETE]` Analytics view reads tenant summary data from the API.
- `[COMPLETE]` Fake UI actions and hardcoded demo responses are replaced with API calls.

## Acceptance Checks

- `[PARTIAL]` Backend compilation; Java and Maven are unavailable on this machine.
- `[COMPLETE]` Angular frontend build was reported passing with Node 24.15; this machine's Node 24.14 fails Angular's version guard.
- `[NOT IMPLEMENTED]` PostgreSQL migrations execute successfully.
- `[NOT IMPLEMENTED]` Application connects to Redis successfully.
- `[PARTIAL]` Docker Compose configuration and application build contexts are present; validation and service startup are unverified.
- `[PARTIAL]` Authentication is implemented with the documented demo credentials but has not been run.
- `[NOT IMPLEMENTED]` Tenant isolation has not been verified across tenants.
- `[NOT IMPLEMENTED]` Demo data loading has not been run.
- `[NOT IMPLEMENTED]` Call Simulator API submission has not been run.
- `[NOT IMPLEMENTED]` Conversation and transcript persistence has not been run.
- `[NOT IMPLEMENTED]` Mock AI response has not been exercised.
- `[NOT IMPLEMENTED]` `get_order_status` response has not been exercised.
- `[NOT IMPLEMENTED]` Support-ticket persistence has not been exercised.
- `[NOT IMPLEMENTED]` Escalation persistence has not been exercised.
- `[NOT IMPLEMENTED]` Persisted transcript rendering against the running backend has not been exercised.
- `[NOT IMPLEMENTED]` Ticket rendering against the running backend has not been exercised.
- `[NOT IMPLEMENTED]` Escalation rendering against the running backend has not been exercised.
- `[NOT IMPLEMENTED]` Analytics updates after a running conversation have not been exercised.

## Required Demo Scenario

- `[PARTIAL]` Customer: "Hi, where is my order ORD-10001?" Implementation invokes `get_order_status`, returning `OUT_FOR_DELIVERY` and expected tomorrow; scenario has not been run.
- `[PARTIAL]` Customer: "I still want to complain about the delay." Implementation invokes `create_support_ticket`; scenario has not been run.
- `[PARTIAL]` Customer: "I want to speak with someone." Implementation invokes `transfer_to_human`; scenario has not been run.

The backend and full-stack demo could not be executed in this environment because Java, Maven, and Docker are unavailable. Run the Docker Compose workflow or install the documented toolchain to verify persistence and the end-to-end scenario.
