# Fashion Sense

Fashion Sense is a full-stack ecommerce platform inspired by modern retail applications such as Myntra and Amazon.

The project demonstrates the design and implementation of a production-oriented ecommerce system using **Spring Boot, Next.js, PostgreSQL, Redis, Apache Kafka, Docker, JWT authentication, role-based access control, automated testing, and GitHub Actions CI/CD**.

The application supports product discovery, category and occasion-based filtering, cart and wishlist management, promotions, checkout, order processing, inventory management, event-driven order processing, and secure customer/admin APIs.

---

## Features

### Product Catalog

- Browse products without authentication

- Product detail pages

- Brand-based organization

- Hierarchical categories

- Occasion-based product discovery

- Filtering by:

  - Category

  - Occasion

  - Brand

  - Price

  - Size

  - Color

  - Style

  - Material

- Pagination and sorting

- Product variants

- Product images

- Availability-aware variant selection

Customers are intentionally not shown exact remaining inventory quantities.

The storefront only exposes availability information such as whether a product variant is available or out of stock.

---

## Authentication and Security

Fashion Sense implements stateless JWT authentication using Spring Security.

Features include:

- Customer registration

- Login

- Email verification workflow

- JWT access tokens

- BCrypt password hashing

- Stateless authentication

- Role-based access control

- Customer and admin roles

Supported roles:

```text

CUSTOMER

ADMIN

```

Admin endpoints are protected using Spring Security authorization rules and method-level authorization with:

```java

@PreAuthorize("hasRole('ADMIN')")

```

The JWT contains claims such as:

```text

userId

role

subject

issuedAt

expiresAt

```

---

## Shopping Cart

Authenticated customers can:

- Add products to cart

- Change item quantities

- Remove items

- View cart subtotal

- Maintain cart state between requests

Inventory availability is validated before checkout.

---

## Wishlist

Customers can:

- Add products to their wishlist

- View saved products

- Remove products

- Persist wishlist data in PostgreSQL

Duplicate wishlist entries are prevented by the application/database design.

---

## Promotions and Discounts

Fashion Sense supports a flexible promotion system.

Supported promotion types include:

```text

PERCENTAGE

FIXED_AMOUNT

BUY_X_GET_Y

```

Promotions can operate at:

```text

PRODUCT

CART

```

Example seeded cart promotion:

```text

SAVE10

```

which applies a 10% cart discount.

The checkout frontend allows customers to submit promotion codes while the backend remains the authoritative source for promotion validation and pricing calculations.

---

## Checkout

Checkout supports:

- Saved shipping addresses

- Standard shipping

- Express shipping

- Simulated card payments

- Simulated PayPal payments

- Promotion codes

- Tax calculation

- Shipping calculation

- Order creation

- Inventory decrement

- Order event publishing

Payment integration is simulated because this project focuses on ecommerce system architecture rather than connecting to a real payment provider.

---

## Checkout Idempotency

Checkout requests require an:

```text

Idempotency-Key

```

This prevents duplicate orders when the same checkout request is retried because of:

- network retries

- duplicate browser requests

- frontend retries

- temporary client/server communication failures

The system protects idempotency at both the application and database layers.

The database contains a unique constraint based on:

```text

user_id + idempotency_key

```

This ensures the same customer cannot create multiple orders using the same checkout idempotency key.

---

## Inventory Management

Product inventory is maintained at the variant level.

Each variant contains information such as:

```text

SKU

size

color

style

material

price

stock quantity

availability

```

Checkout performs protected inventory updates so concurrent orders cannot reduce stock below zero.

Database constraints also enforce:

```text

stock_quantity >= 0

```

Customers do not receive the exact stock count.

Admin APIs can access detailed inventory information.

---

## Order Management

Customers can:

- Place orders

- View order history

- View individual order details

Order records contain:

- Order number

- Customer

- Shipping method

- Payment method

- Payment status

- Subtotal

- Discount

- Shipping amount

- Tax

- Final total

- Shipping address snapshot

- Order items

- Creation timestamp

Shipping address information is copied into the order so historical orders remain accurate even if the customer later changes their saved address.

---

## Event-Driven Architecture with Kafka

Fashion Sense uses Apache Kafka for asynchronous order events.

After checkout succeeds, the backend publishes an:

```text

order-confirmed

```

event.

A Kafka consumer processes the event asynchronously.

This demonstrates separation between synchronous transaction processing and asynchronous downstream processing.

Current Kafka flow:

```text

Checkout

   |

   v

Order Created

   |

   v

Kafka Producer

   |

   v

order-confirmed topic

   |

   v

Kafka Consumer

```

This architecture could later support services such as:

- email notifications

- analytics

- recommendations

- fulfillment

- fraud detection

- loyalty systems

---

## Redis Caching

Redis is used through Spring Cache.

Product detail responses are cached using:

```java

@Cacheable(

    cacheNames = "productDetails",

    key = "#slug"

)

```

The current cache configuration uses:

```text

TTL: 10 minutes

Serialization: JSON

Null caching: disabled

```

Product-related write operations evict the associated cached product after the database transaction commits.

This reduces repeated database work for frequently accessed product pages.

---

## PostgreSQL

PostgreSQL is the primary persistent datastore.

Major tables include:

```text

users

addresses

brands

categories

occasions

products

product_variants

product_images

product_occasions

carts

cart_items

wishlist_items

promotions

orders

order_items

email_verification_tokens

flyway_schema_history

```

Relational constraints are used to protect data integrity.

---

## Flyway Database Migrations

Fashion Sense uses Flyway for database versioning.

Migration files are stored under:

```text

backend/src/main/resources/db/migration

```

Flyway automatically validates and applies database schema migrations when the backend starts.

The project currently contains migrations through version:

```text

V13

```

This provides deterministic database evolution across environments.

---

## Backend

Backend technologies:

- Java 26

- Spring Boot

- Spring Web

- Spring Data JPA

- Hibernate

- Spring Security

- Spring OAuth2 Resource Server

- JWT

- PostgreSQL

- Flyway

- Redis

- Spring Cache

- Apache Kafka

- Spring Boot Actuator

- Springdoc OpenAPI

- Maven

Backend package root:

```text

com.fashionsense

```

---

## Frontend

Frontend technologies:

- Next.js 16

- React

- TypeScript

- Tailwind CSS

Major pages include:

```text

/

 /login

 /register

 /verify-email

 /profile

 /products/[slug]

 /cart

 /wishlist

 /checkout

 /orders

 /orders/[orderNumber]

```

Next.js API routes act as a frontend-facing API layer between the browser and Spring Boot backend.

---

## API Documentation

Fashion Sense exposes OpenAPI documentation.

OpenAPI JSON:

```text

http://localhost:8080/v3/api-docs

```

Swagger UI:

```text

http://localhost:8080/swagger-ui/index.html

```

Swagger documents APIs for:

- authentication

- customers

- addresses

- products

- brands

- categories

- occasions

- variants

- images

- wishlist

- cart

- checkout

- orders

JWT-secured operations support Bearer authentication.

---

## Health Checks and Observability

Spring Boot Actuator provides application health endpoints.

Available endpoints include:

```text

/actuator/health

/actuator/health/liveness

/actuator/health/readiness

```

These endpoints can be used by:

- Docker health checks

- load balancers

- Kubernetes probes

- deployment platforms

- monitoring systems

---

## Docker Infrastructure

Local infrastructure is managed using Docker Compose.

Services include:

```text

PostgreSQL

Redis

Apache Kafka

Frontend

```

Typical infrastructure directory:

```text

infrastructure/

```

Start infrastructure with:

```bash

cd infrastructure

docker compose up -d

```

Check service status:

```bash

docker compose ps

```

Stop services:

```bash

docker compose down

```

---

## Local Development

### Prerequisites

Install:

- Java 26

- Node.js 24+

- npm

- Git

- Docker Desktop

---

### Clone the Repository

```bash

git clone https://github.com/syeddabeer02-cmd/FashionSense.git

cd FashionSense

```

---

### Environment Variables

Create:

```text

infrastructure/.env

```

based on your environment configuration.

Important secrets include values such as:

```text

POSTGRES_PASSWORD

JWT_SECRET

```

Do not commit `.env` files or secrets to Git.

The project `.gitignore` prevents environment files from being committed.

---

### Start Infrastructure

```bash

cd infrastructure

docker compose up -d

```

Verify:

```bash

docker compose ps

```

---

### Start Backend

From the project root in PowerShell:

```powershell

$env:POSTGRES_PASSWORD = ((Get-Content .\infrastructure\.env | Where-Object { $_ -match '^POSTGRES_PASSWORD=' }) -replace '^POSTGRES_PASSWORD=','')

$env:JWT_SECRET = ((Get-Content .\infrastructure\.env | Where-Object { $_ -match '^JWT_SECRET=' }) -replace '^JWT_SECRET=','')

cd .\backend

.\mvnw.cmd spring-boot:run

```

Backend:

```text

http://localhost:8080

```

---

### Start Frontend

```bash

cd frontend

npm install

npm run dev

```

Frontend:

```text

http://localhost:3000

```

If port 3000 is already occupied, Next.js may automatically select another port such as:

```text

http://localhost:3001

```

---

## Testing

### Backend Tests

```powershell

cd backend

.\mvnw.cmd test

```

Current verified test result:

```text

Tests run: 9

Failures: 0

Errors: 0

Skipped: 0

BUILD SUCCESS

```

---

### Frontend Lint

```bash

cd frontend

npm run lint

```

---

### Frontend Production Build

```bash

npm run build

```

The current production build successfully generates all application routes and pages.

---

## CI/CD

Fashion Sense uses GitHub Actions.

Workflow:

```text

.github/workflows/

```

The CI pipeline validates both the frontend and backend.

### Frontend CI

The pipeline:

1. Checks out the repository

2. Configures Node.js

3. Installs dependencies

4. Runs ESLint

5. Builds the Next.js production application

### Backend CI

The pipeline:

1. Checks out the repository

2. Configures Java

3. Starts PostgreSQL

4. Starts Redis

5. Starts Kafka

6. Runs backend tests

7. Shuts down infrastructure

Latest validated CI run:

```text

Fashion Sense CI

Frontend - Lint and Build: SUCCESS

Backend - Test: SUCCESS

```

---

## Architecture Overview

```text

                         +----------------------+

                         |       Browser        |

                         +----------+-----------+

                                    |

                                    v

                         +----------------------+

                         |       Next.js        |

                         | React + TypeScript   |

                         +----------+-----------+

                                    |

                              REST / JSON

                                    |

                                    v

                         +----------------------+

                         |     Spring Boot      |

                         |       Backend        |

                         +---+---------+--------+

                             |         |

                 +-----------+         +-----------+

                 |                                 |

                 v                                 v

        +------------------+              +------------------+

        |    PostgreSQL    |              |      Redis       |

        | Persistent Data  |              | Product Cache    |

        +------------------+              +------------------+

                 |

                 |

                 +----------------+

                                  |

                                  v

                         +------------------+

                         |      Kafka       |

                         |  Order Events    |

                         +--------+---------+

                                  |

                                  v

                         +------------------+

                         | Kafka Consumer   |

                         | Async Processing |

                         +------------------+

```

---

## System Design Concepts Demonstrated

Fashion Sense intentionally incorporates several production system-design concepts.

### Stateless Authentication

JWT authentication allows backend instances to validate requests without storing HTTP sessions.

### Caching

Redis reduces repeated database access for frequently requested product details.

### Event-Driven Processing

Kafka decouples order creation from downstream asynchronous processing.

### Database Constraints

PostgreSQL constraints protect critical invariants such as:

- unique values

- valid roles

- non-negative inventory

- checkout idempotency

### Idempotency

Repeated checkout requests with the same key cannot create duplicate orders.

### Concurrency Protection

Inventory updates prevent multiple concurrent customers from overselling the same product variant.

### Health Checks

Actuator readiness and liveness endpoints support deployment orchestration.

### API Documentation

OpenAPI/Swagger provides machine-readable and interactive API documentation.

### CI/CD

GitHub Actions automatically validates backend and frontend changes before they are considered healthy.

---

## Repository Structure

```text

FashionSense/

│

├── .github/

│   └── workflows/

│

├── backend/

│   ├── src/

│   ├── pom.xml

│   ├── mvnw

│   └── mvnw.cmd

│

├── frontend/

│   ├── src/

│   ├── package.json

│   └── next.config.ts

│

├── infrastructure/

│   ├── compose.yaml

│   └── .env.example

│

├── docs/

│

├── .gitignore

│

└── README.md

```

---

## Verified End-to-End Flows

The following flows have been manually validated:

```text

Authentication

        ↓

Product Discovery

        ↓

Category / Occasion Filtering

        ↓

Product Detail

        ↓

Cart

        ↓

Wishlist

        ↓

Promotion Code

        ↓

Checkout

        ↓

Inventory Update

        ↓

Order Creation

        ↓

Kafka Event

        ↓

Order History

        ↓

Order Detail

```

Additional verified behavior includes:

- checkout idempotency

- out-of-stock protection

- inventory decrement

- customer/admin RBAC

- promotion calculations

- Redis connectivity

- health endpoints

- OpenAPI documentation

- frontend production build

- backend automated tests

- GitHub Actions CI

---

## Future Enhancements

Potential future improvements include:

- S3-compatible product image storage

- Gift cards

- Real payment gateway integration

- Email delivery provider

- Recommendation engine

- Search engine integration

- Distributed tracing

- Metrics dashboards

- Kubernetes deployment

- Cloud deployment

- Dedicated microservices for selected domains

Gift cards were intentionally removed from the current project scope and may be added later if needed.

---

## Project Purpose

Fashion Sense was built as a portfolio-quality full-stack engineering project demonstrating practical implementation of:

- backend development

- frontend development

- relational database design

- caching

- asynchronous messaging

- authentication and authorization

- distributed-system concepts

- Docker infrastructure

- testing

- CI/CD

- API design

- production-oriented engineering practices

The goal is not simply to demonstrate individual technologies, but to show how they work together inside a realistic ecommerce architecture.

## Audit, uploads and hosting

See [the implementation audit](docs/resume-audit.md),
[S3 image-upload API](docs/s3-images.md) and [hosting guide](docs/hosting.md).
Run `./mvnw verify` (Windows: `.\mvnw.cmd verify`) for backend tests and
JaCoCo reports under `backend/target/site/jacoco/`. GitHub Actions retains
coverage and test reports as downloadable artifacts.
