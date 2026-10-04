# Resume-to-implementation audit

## Verified in source

- Java26/Spring Boot4, REST controllers, JPA entities/repositories backed by
  PostgreSQL, Flyway migrations, validation and BCrypt password hashes.
- HS256 JWT issuing/decoding and CUSTOMER/ADMIN role mapping. Catalog writes are
  restricted to ADMIN; customer API identifiers derive from the JWT.
- Redis product-detail caching and transaction-aware invalidation.
- Kafka order-confirmed producer and consumer. Current consumer logs events;
  this is not a separate fulfillment/payment microservice.
- Checkout idempotency keys, a PostgreSQL advisory lock and unique user/key
  constraint, inventory protection, promotion calculation and order snapshots.
- Next.js server-side proxy routes, React/TypeScript/Tailwind presentation,
  Docker build files and GitHub Actions tests/builds.

## Changes made during this audit

- JWT validation now checks the expected issuer in addition to signature and
  timestamps. Configuration rejects secrets shorter than 32 decoded bytes.
- Added signed-token negative tests and HTTP authorization boundary tests.
- JaCoCo generates measured HTML/XML coverage with Maven verify; no 90% claim
  is made without measuring the entire relevant code scope.
- Docker frontend targets the backend service, waits for backend health, and
  local infrastructure ports bind localhost.
- Optional admin S3 upload API with size/type/signature checks, unique keys and
  mock tests. A real bucket and HTTPS image origin remain to be validated.
- Prepared hosted HTTPS configuration, Kafka persistence and corrected escaped
  Markdown documentation.

## Remaining gaps and account steps

The backend is a modular monolith with one deployable Spring Boot application.
It does not implement 10 independently deployed payment microservices. Extracting
services requires agreed boundaries, data ownership and failure handling rather
than renaming packages. Public API count is not proof of historical volume.

Payments are simulated. Merchant onboarding, real provider webhooks, payment
settlement and UPI/card/net-banking/wallet integrations remain separate work.
Choose and activate a provider sandbox with the required country/method support
before implementing and testing those flows. Email verification currently uses
a logging sender and needs a delivery provider for unattended public signup.

S3 bucket/CDN/credentials, the hosting server and hostname are user-dependent.
Live upload and deployment are not represented as completed by mock tests.
AWS Lambda/Glue/Redshift and Jenkins/GitLab execution are not implemented in
this ecommerce stack. Do not add unrelated services solely to enumerate skills.
The retail project has prepared alternate CI files and its own Azure deployment
handoff. AI/GraphRAG projects remain a separate scope.

Interview explanations should use the implemented code and demonstrations,
with historical employer measurements explained from the user's actual work.
