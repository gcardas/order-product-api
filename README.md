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
---

*Sections coming next: domain model (`Product` / `Order`, and how the relationship between them is modeled in MongoDB), the `GET /api/orders/{orderId}/products` endpoint with its filtering/sorting/pagination design, and the test suite.*