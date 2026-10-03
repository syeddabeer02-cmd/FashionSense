\# Fashion Sense Architecture



\## 1. Overview



Fashion Sense is a full-stack ecommerce application designed to demonstrate production-oriented software architecture using:



\- Next.js

\- React

\- TypeScript

\- Spring Boot

\- PostgreSQL

\- Redis

\- Apache Kafka

\- Docker

\- JWT authentication

\- Role-based access control

\- Flyway

\- GitHub Actions CI/CD



The architecture follows a layered design where the frontend handles presentation and browser interaction, Spring Boot handles business logic and security, PostgreSQL stores persistent data, Redis provides caching, and Kafka handles asynchronous events.



\---



\# 2. High-Level Architecture



```text

&#x20;                        +----------------------+

&#x20;                        |       Browser        |

&#x20;                        +----------+-----------+

&#x20;                                   |

&#x20;                                   |

&#x20;                             HTTP / HTTPS

&#x20;                                   |

&#x20;                                   v

&#x20;                        +----------------------+

&#x20;                        |       Next.js        |

&#x20;                        | React + TypeScript   |

&#x20;                        +----------+-----------+

&#x20;                                   |

&#x20;                                   |

&#x20;                             REST / JSON

&#x20;                                   |

&#x20;                                   v

&#x20;                        +----------------------+

&#x20;                        |     Spring Boot      |

&#x20;                        |     REST Backend     |

&#x20;                        +----------+-----------+

&#x20;                                   |

&#x20;            +----------------------+----------------------+

&#x20;            |                      |                      |

&#x20;            v                      v                      v

&#x20;   +----------------+     +----------------+     +----------------+

&#x20;   |   PostgreSQL   |     |     Redis      |     |     Kafka      |

&#x20;   | Persistent DB  |     |     Cache      |     | Event Broker   |

&#x20;   +----------------+     +----------------+     +--------+-------+

&#x20;                                                          |

&#x20;                                                          v

&#x20;                                                 +----------------+

&#x20;                                                 | Kafka Consumer |

&#x20;                                                 | Async Handling |

&#x20;                                                 +----------------+

```



\---



\# 3. Frontend Architecture



The frontend is implemented using:



```text

Next.js

React

TypeScript

Tailwind CSS

```



The frontend is responsible for:



\- rendering ecommerce pages

\- product browsing

\- category filtering

\- occasion filtering

\- authentication forms

\- cart interaction

\- wishlist interaction

\- checkout

\- promotion entry

\- order history

\- order details



The browser communicates with Next.js API routes.



Next.js API routes then communicate with the Spring Boot backend.



This creates an additional application boundary between browser code and backend APIs.



\---



\# 4. Frontend Request Flow



Example checkout flow:



```text

Browser

&#x20;  |

&#x20;  v

Next.js Checkout Page

&#x20;  |

&#x20;  v

Next.js /api/checkout

&#x20;  |

&#x20;  v

Spring Boot

/api/customers/me/checkout

&#x20;  |

&#x20;  v

CheckoutService

&#x20;  |

&#x20;  +----> PostgreSQL

&#x20;  |

&#x20;  +----> Kafka

```



The frontend does not directly modify the database.



All important business rules remain inside the backend.



\---



\# 5. Backend Architecture



The backend is implemented using Spring Boot.



The application package root is:



```text

com.fashionsense

```



The backend follows a layered structure similar to:



```text

Controller

&#x20;   |

&#x20;   v

Service

&#x20;   |

&#x20;   v

Repository

&#x20;   |

&#x20;   v

Database

```



Responsibilities are separated between layers.



\---



\# 6. Controller Layer



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



\- receiving HTTP requests

\- reading path parameters

\- reading query parameters

\- reading request bodies

\- validating request structures

\- extracting authentication information

\- returning HTTP responses



Controllers should not contain the core business logic.



\---



\# 7. Service Layer



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



\- validating business rules

\- coordinating repositories

\- calculating totals

\- checking stock

\- applying promotions

\- creating orders

\- generating tokens

\- publishing events

\- controlling transactions



The service layer is the main business-logic layer.



\---



\# 8. Repository Layer



Spring Data JPA repositories handle database access.



Repositories abstract SQL operations behind Java interfaces.



Typical flow:



```text

Service

&#x20;  |

&#x20;  v

Repository

&#x20;  |

&#x20;  v

Hibernate / JPA

&#x20;  |

&#x20;  v

PostgreSQL

```



This avoids writing repetitive database-access code for standard operations.



Custom queries are used where more specialized behavior is required.



\---



\# 9. PostgreSQL Architecture



PostgreSQL is the system of record.



Important tables include:



```text

users

addresses

brands

categories

occasions

products

product\_variants

product\_images

product\_occasions

carts

cart\_items

wishlist\_items

promotions

orders

order\_items

email\_verification\_tokens

flyway\_schema\_history

```



PostgreSQL stores persistent ecommerce state.



\---



\# 10. Database Relationships



Important relationships include:



```text

User

&#x20;|

&#x20;+---- Addresses

&#x20;|

&#x20;+---- Cart

&#x20;|       |

&#x20;|       +---- Cart Items

&#x20;|

&#x20;+---- Wishlist Items

&#x20;|

&#x20;+---- Orders

&#x20;        |

&#x20;        +---- Order Items

```



Catalog relationships include:



```text

Brand

&#x20; |

&#x20; +---- Products



Category

&#x20; |

&#x20; +---- Products



Product

&#x20; |

&#x20; +---- Variants

&#x20; |

&#x20; +---- Images

&#x20; |

&#x20; +---- Occasions

```



\---



\# 11. Database Constraints



The database protects important invariants.



Examples include:



\- unique user email

\- unique product slug

\- unique promotion code

\- non-negative inventory

\- valid user roles

\- valid account statuses

\- valid promotion types

\- valid promotion scopes

\- unique checkout idempotency keys per user



Example inventory rule:



```text

stock\_quantity >= 0

```



Database constraints provide a final layer of protection even if application logic fails.



\---



\# 12. Flyway Migration Architecture



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



\- schema versioning

\- reproducible database setup

\- ordered migrations

\- migration history

\- checksum validation



This prevents developers from manually changing databases without tracking those changes.



\---



\# 13. Authentication Architecture



Fashion Sense uses JWT-based stateless authentication.



Authentication flow:



```text

User

&#x20;|

&#x20;v

POST /api/auth/login

&#x20;|

&#x20;v

AuthService

&#x20;|

&#x20;+---- Verify password using BCrypt

&#x20;|

&#x20;v

Jwt
