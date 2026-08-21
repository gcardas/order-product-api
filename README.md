# Order Product API

A small REST API modeling a simple e-commerce domain — `Product` and `Order` — built with Java, Spring Boot, and MongoDB.

This README grows alongside the project. Right now it covers setup, running the app, and the reasoning behind a few early decisions. Sections on the domain model, the API endpoint, and testing will be filled in as those pieces land.

## Tech stack

- **Java 21**
- **Spring Boot 4.1.0**, built with **Gradle (Groovy DSL)**
- **Spring Data MongoDB**
- **MongoDB 7.0**, run via Docker
- **Spring Boot Actuator** (for a basic health endpoint)
- **Lombok** (cuts down on boilerplate in the domain classes)

## Prerequisites

- JDK 21 or later
- Docker Desktop (or Docker Engine + the Compose plugin on Linux)

You don't need MongoDB installed anywhere — it runs entirely inside a container, so there's nothing to set up on the host beyond Docker itself.

## Getting started

**1. Clone the repo and set up your local environment file**

```bash
git clone <repo-url>
cd order-product-api
cp .env.example .env
```

The defaults in `.env.example` work fine for local development as-is — no real secrets involved, just credentials for a throwaway local container.

**2. Start MongoDB**

```bash
docker compose up -d
```

First run will pull the `mongo:7.0` image, so it takes a moment. Check it's up with `docker ps` — you should see a container named `ecommerce-mongodb`.

**3. Run the application**

```bash
./gradlew bootRun
```

You should see a clean startup with no `MongoTimeoutException`, ending in something like `Started OrderProductApiApplication in X seconds`.

**4. Sanity check**

```bash
curl http://localhost:8080/actuator/health
```

Should return `{"status":"UP"}`.

## Configuration

Connection details live in `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: order-product-api
  mongodb:
    uri: mongodb://admin:adminpassword@localhost:27017/ecommerce?authSource=admin

server:
  port: 8080
```

Two things need to be explicitly enabled, or parts of the domain model silently don't work:

- **Auditing** — `@CreatedDate` / `@LastModifiedDate` stay `null` unless a `@Configuration` class is annotated with `@EnableMongoAuditing`.
- **Automatic index creation** — off by default in Spring Data MongoDB, so `@Indexed(unique = true)` on `Product.sku` does nothing until `spring.data.mongodb.auto-index-creation: true` is set.

As of **Spring Boot 4.0**, the core MongoDB connection properties moved from the `spring.data.mongodb.*` namespace to `spring.mongodb.*`.

## Running MongoDB locally

`docker-compose.yml` spins up a single MongoDB instance with a root user and a named volume so data survives restarts:

```yaml
services:
  mongodb:
    image: mongo:7.0
    container_name: ecommerce-mongodb
    restart: unless-stopped
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGO_ROOT_USERNAME}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_ROOT_PASSWORD}
      MONGO_INITDB_DATABASE: ${MONGO_DATABASE}
    volumes:
      - mongodb_data:/data/db

volumes:
  mongodb_data:
```

Credentials come from `.env` (git-ignored — `.env.example` is committed as a template).

## Architectural decisions & trade-offs

A few choices I made early on, and why:

**MongoDB in Docker, not installed locally, not Atlas.**
Docker keeps MongoDB fully disposable and reproducible — one command to start it, one to wipe it clean.

**Authentication enabled, even for local dev.**
Kept it on because it's closer to how Mongo actually runs anywhere that matters.

**Credentials via `.env`, with a known gap.**
Docker Compose reads `.env` automatically, but Spring Boot doesn't, so the same credentials are also hardcoded in `application.yml` — fine for local dev, not how I'd do it in production.

**Gradle with the Groovy DSL over Maven or Kotlin DSL.**
Mostly familiarity — Maven would've worked just as well.

**Hybrid embedding + referencing for the Product/Order relationship.**
Snapshotting name/price on the order keeps history accurate even if a product changes or gets discontinued, while the `productId` reference keeps a link back to the original.

**Product status is an enum with a history, not a boolean.**
Distinguishes "not yet available" from "no longer available," and costs nothing extra to track since Mongo has no schema to fight.

**Stock count is kept separate from status, not folded into it.**
Availability is computed from `stockQuantity` at read time so it can never drift out of sync with the real count.

**SKU uses a plain unique index, not a partial one.**
Products are soft-deleted, never removed, so a SKU never becomes free to reuse.

**Order status has no history, unlike Product's.**
An order is a one-time transaction, not a long-lived catalog item.

**`totalAmount` is computed once at order creation.**
Order items never change afterward, so there's nothing for a stored total to drift out of sync with.

**Price uses `BigDecimal`, not `double`.**
Avoids floating-point rounding errors on money.

**Filtering/sorting/pagination run as a MongoDB aggregation pipeline, not in-memory Java.**
Chosen over filtering in application code specifically because it exercises MongoDB's query engine, which is the point of this assignment.

**Custom repository (`OrderRepositoryCustom` + `Impl`) handles the aggregation query.**
Spring Data's standard pattern for queries it can't generate automatically — keeps Mongo-specific query code out of the service layer.

**Filter operators as a Strategy pattern, auto-collected into a registry.**
One small class per operator (`eq`, `neq`, `startsWith`, `contains`); adding a new one later means writing one class, nothing else changes.

**A whitelist enum (`OrderItemField`) maps allowed field names to document paths.**
Keeps raw client input from ever reaching the database as a literal query field.
---

## Domain model

### Product

| Field | Type | Notes |
|---|---|---|
| `id` | `String` | Mongo `ObjectId` |
| `name` | `String` | |
| `price` | `BigDecimal` | stored as `Decimal128` |
| `sku` | `String` | unique |
| `status` | enum: `DRAFT`, `ACTIVE`, `DISCONTINUED` | |
| `statusHistory` | `List<StatusChange>` | embedded, `{ status, changedAt }` |
| `stockQuantity` | `int` | |
| `createdAt` / `updatedAt` | `Instant` | via Spring Data auditing |

### Order

| Field | Type | Notes |
|---|---|---|
| `id` | `String` | |
| `createdDate` | `Instant` | |
| `status` | enum: `PLACED`, `COMPLETED`, `CANCELLED` | |
| `totalAmount` | `BigDecimal` | computed once at creation |
| `items` | `List<OrderItem>` | embedded, `{ productId, name, price, quantity }` |

### Product ↔ Order relationship

Conceptually many-to-many. Modeled as a **hybrid**: each `Order` embeds a snapshot of `name` and `price` for every product it contains, and also keeps a `productId` reference back to the canonical `Product`.
*Sections coming next: domain model (`Product` / `Order`, and how the relationship between them is modeled in MongoDB), the `GET /api/orders/{orderId}/products` endpoint with its filtering/sorting/pagination design, and the test suite.*


## Query parameters (products endpoint)

- `filter=field:operator:value` — comma-separated for multiple (e.g. `name:contains:mouse,price:eq:9.99`)
- `sort=field:direction` — comma-separated, order sets sort priority (e.g. `price:desc,name:asc`)
- `page`, `limit` — pagination

Supported operators: `eq`, `neq`, `startsWith`, `contains`.