# Microservices E-Commerce Platform

A full-featured e-commerce backend built as a set of independent Spring Boot microservices, demonstrating service discovery, API gateway security, synchronous and asynchronous inter-service communication, and full containerization with Docker.

## Architecture

```
                        ┌─────────────────┐
                        │  Eureka Server   │  (Service Discovery)
                        │   :8761          │
                        └────────▲─────────┘
                                 │ registers
        ┌────────────────────────┼────────────────────────┐
        │                        │                         │
┌───────▼────────┐      ┌────────▼────────┐      ┌─────────▼────────┐
│  API Gateway    │      │  User Service   │      │  Product Service  │
│  :8080          │      │  :8081          │      │  :8082             │
│  (JWT security) │      └─────────────────┘      └────────────────────┘
└───────┬─────────┘
        │ routes
        │                ┌─────────────────┐      ┌────────────────────┐
        └───────────────▶│  Order Service  │─────▶│  Product Service    │
                          │  :8083          │ Feign │  (sync stock check) │
                          └────────┬────────┘      └────────────────────┘
                                   │ publishes
                                   ▼
                          ┌─────────────────┐
                          │  Kafka Topic    │
                          │  order-created  │
                          └────────┬────────┘
                                   │ consumes
                                   ▼
                          ┌─────────────────────┐
                          │ Notification Service │
                          │  :8084                │
                          └───────────────────────┘
```

## Services

| Service | Port | Responsibility |
|---|---|---|
| **eureka-server** | 8761 | Service discovery — every other service registers here |
| **api-gateway** | 8080 | Single entry point; routes requests via Eureka; enforces JWT authentication on protected routes |
| **user-service** | 8081 | User registration, login, JWT issuance (BCrypt password hashing) |
| **product-service** | 8082 | Product catalog CRUD |
| **order-service** | 8083 | Places orders; calls product-service synchronously (FeignClient) to check stock and price; publishes an `order-created` event to Kafka on success |
| **notification-service** | 8084 | Consumes `order-created` events from Kafka and logs an order confirmation (stands in for a real email/SMS integration) |

## Tech Stack

- **Java 17**, **Spring Boot 4.1.1**
- **Spring Cloud** (Eureka, Gateway, OpenFeign) — 2025.1.3
- **Spring Data JPA** + **PostgreSQL** (separate database per service)
- **Spring Security** + **JWT** (via `jjwt`) for authentication
- **Apache Kafka** + **Zookeeper** for asynchronous event-driven communication
- **Docker** & **Docker Compose** for containerization
- **Lombok** for boilerplate reduction

## Key Design Decisions

- **Security at the perimeter**: JWT verification happens once, at the API Gateway. Individual services trust the gateway and stay stateless — they don't re-validate tokens themselves.
- **Sync vs. async communication**: Order Service calls Product Service synchronously via FeignClient because it needs an immediate stock/price answer before completing the order. It publishes to Kafka asynchronously for anything that doesn't need to block the response (order confirmation notifications).
- **Database per service**: Each service owns its own PostgreSQL database (`userdb`, `productsdb`, `ordersdb`), keeping services independently deployable and avoiding tight coupling through a shared schema.
- **DTOs everywhere**: No service ever exposes its JPA entities directly over the network — request/response DTOs decouple the API contract from the database schema.

## Running the Project

### Prerequisites
- Docker Desktop installed and running

### Start everything
```bash
git clone https://github.com/abhinavpayyan/microservices-ecommerce.git
cd microservices-ecommerce
docker-compose up --build
```

This builds all six services and starts them alongside three PostgreSQL databases, Kafka, Zookeeper, and a Kafka UI — all in one command.

First build takes several minutes (each service compiles inside its own container). Subsequent runs are much faster thanks to Docker layer caching.

### Verify it's running
- **Eureka dashboard**: http://localhost:8761 — should show all five services (Gateway, User, Product, Order, Notification) as `UP`
- **Kafka UI**: http://localhost:8090 — inspect the `order-created` topic and its messages

## API Walkthrough

All requests go through the API Gateway at `http://localhost:8080`.

**1. Register a user**
```
POST /api/users/register
{
  "username": "abhinav",
  "email": "abhinav@example.com",
  "password": "secret123"
}
```

**2. Log in to get a JWT**
```
POST /api/users/login
{
  "username": "abhinav",
  "password": "secret123"
}
```
Response includes a `token` — use it as `Authorization: Bearer <token>` on every request below.

**3. Create a product**
```
POST /api/products
{
  "productName": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "price": 799.00,
  "stockQuantity": 50
}
```

**4. Place an order**
```
POST /api/orders
{
  "productId": 1,
  "quantity": 2
}
```
This triggers the full pipeline: Order Service checks stock via FeignClient → saves the order → publishes an `order-created` event → Notification Service consumes it and logs a confirmation.

**5. Watch it happen**
```bash
docker logs notification-service --tail 20
```
You'll see the event arrive automatically, with no direct call ever made between Order Service and Notification Service.

## What I'd Add Next

- Circuit breaker (Resilience4j) around the Product Service Feign call
- Public product browsing (currently all `/api/products/**` routes require auth — a real storefront would let anyone view the catalog and only require login to order)
- A Payment Service consuming `order-created` alongside Notification Service
- Centralized logging/tracing (e.g. Zipkin) across the service chain

---

Built by [Abhinav PK](https://github.com/abhinavpayyan) as a hands-on project to learn microservices architecture, Spring Cloud, and event-driven design with Kafka.
