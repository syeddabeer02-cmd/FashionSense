# Fashion Sense Architecture

## 1. Overview

Fashion Sense is a full-stack ecommerce application designed to demonstrate production-oriented software architecture using:

- Next.js

- React

- TypeScript

- Spring Boot

- PostgreSQL

- Redis

- Apache Kafka

- Docker

- JWT authentication

- Role-based access control

- Flyway

- GitHub Actions CI/CD

The architecture follows a layered design where the frontend handles presentation and browser interaction, Spring Boot handles business logic and security, PostgreSQL stores persistent data, Redis provides caching, and Kafka handles asynchronous events.

---

# 2. High-Level Architecture

```text

                         +----------------------+

                         |       Browser        |

                         +----------+-----------+

                                    |

                                    |

                              HTTP / HTTPS

                                    |

                                    v

                         +----------------------+

                         |       Next.js        |

                         | React + TypeScript   |

                         +----------+-----------+

                                    |

                                    |

                              REST / JSON

                                    |

                                    v

                         +----------------------+

                         |     Spring Boot      |

                         |     REST Backend     |

                         +----------+-----------+

                                    |

             +----------------------+----------------------+

             |                      |                      |

             v                      v                      v

    +----------------+     +----------------+     +----------------+

    |   PostgreSQL   |     |     Redis      |     |     Kafka      |

    | Persistent DB  |     |     Cache      |     | Event Broker   |

    +----------------+     +----------------+     +--------+-------+

                                                           |

                                                           v

                                                  +----------------+

                                                  | Kafka Consumer |

                                                  | Async Handling |

                                                  +----------------+

```

---

# 3. Frontend Architecture

The frontend is implemented using:

```text

Next.js

React

TypeScript

Tailwind CSS

```

The frontend is responsible for:

- rendering ecommerce pages

- product browsing

- category filtering

- occasion filtering

- authentication forms

- cart interaction

- wishlist interaction

- checkout

- promotion entry

- order history

- order details

The browser communicates with Next.js API routes.

Next.js API routes then communicate with the Spring Boot backend.

This creates an additional application boundary between browser code and backend APIs.

---

# 4. Frontend Request Flow

Example checkout flow:

```text

Browser

   |

   v

Next.js Checkout Page

   |

   v

Next.js /api/checkout

   |

   v

Spring Boot

/api/customers/me/checkout

   |

   v

CheckoutService

   |

   +----> PostgreSQL

   |

   +----> Kafka

```

The frontend does not directly modify the database.

All important business rules remain inside the backend.

---

# 5. Backend Architecture

The backend is implemented using Spring Boot.

The application package root is:

```text

com.fashionsense

```

The backend follows a layered structure similar to:

```text

Controller

    |

    v

Service

    |

    v

Repository

    |

    v

Database

```

Responsibilities are separated between layers.

---

# 6. Controller Layer

Controllers expose REST APIs.

Examples include:

```text

AuthController

ProductController

ProductVariantController

CartController

WishlistController

CheckoutController

OrderController

AddressController

```

Controllers are responsible for:

- receiving HTTP requests

- reading path parameters

- reading query parameters

- reading request bodies

- validating request structures

- extracting authentication information

- returning HTTP responses

Controllers should not contain the core business logic.

---

# 7. Service Layer

The service layer contains business rules.

Examples include:

```text

AuthService

ProductService

CheckoutService

CartService

WishlistService

OrderService

```

Responsibilities include:

- validating business rules

- coordinating repositories

- calculating totals

- checking stock

- applying promotions

- creating orders

- generating tokens

- publishing events

- controlling transactions

The service layer is the main business-logic layer.

---

# 8. Repository Layer

Spring Data JPA repositories handle database access.

Repositories abstract SQL operations behind Java interfaces.

Typical flow:

```text

Service

   |

   v

Repository

   |

   v

Hibernate / JPA

   |

   v

PostgreSQL

```

This avoids writing repetitive database-access code for standard operations.

Custom queries are used where more specialized behavior is required.

---

# 9. PostgreSQL Architecture

PostgreSQL is the system of record.

Important tables include:

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

PostgreSQL stores persistent ecommerce state.

---

# 10. Database Relationships

Important relationships include:

```text

User

 |

 +---- Addresses

 |

 +---- Cart

 |       |

 |       +---- Cart Items

 |

 +---- Wishlist Items

 |

 +---- Orders

         |

         +---- Order Items

```

Catalog relationships include:

```text

Brand

  |

  +---- Products

Category

  |

  +---- Products

Product

  |

  +---- Variants

  |

  +---- Images

  |

  +---- Occasions

```

---

# 11. Database Constraints

The database protects important invariants.

Examples include:

- unique user email

- unique product slug

- unique promotion code

- non-negative inventory

- valid user roles

- valid account statuses

- valid promotion types

- valid promotion scopes

- unique checkout idempotency keys per user

Example inventory rule:

```text

stock_quantity >= 0

```

Database constraints provide a final layer of protection even if application logic fails.

---

# 12. Flyway Migration Architecture

Database schema changes are versioned with Flyway.

Migration location:

```text

backend/src/main/resources/db/migration

```

Migrations are applied sequentially:

```text

V1

V2

V3

...

V13

```

Flyway provides:

- schema versioning

- reproducible database setup

- ordered migrations

- migration history

- checksum validation

This prevents developers from manually changing databases without tracking those changes.

---

# 13. Authentication Architecture

Fashion Sense uses JWT-based stateless authentication.

Authentication flow:

```text

User

 |

 v

POST /api/auth/login

 |

 v

AuthService

 |

 +---- Verify password using BCrypt

 |

 v

Jwt
