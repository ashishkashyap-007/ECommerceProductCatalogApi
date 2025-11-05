# E-Commerce Product Catalog API — Proof of Concept

A focused proof-of-concept REST API that demonstrates building a product catalog service with pagination, sorting, filtering and searching using Spring Boot, PostgreSQL and Flyway.

This repository is intended as a learning playground: implement, measure, and iterate on API design and query performance while keeping the code simple and readable.

---

## Problem statement

An e-commerce storefront needs a Product Catalog service that can return product lists for UI components (paged lists, infinite scroll, search panels). The service must support:

- Fast and safe pagination for the UI (offset pagination and an optional cursor/keyset mode for large datasets)
- Multi-field sorting and stable tie-breaker ordering
- Flexible filtering (category, price range, etc.) and searching across name/description
- Predictable, documented API responses and validation
- Repeatable schema management using Flyway for easy setup and testing

This POC provides a clear implementation you can use to learn and extend these concepts.

---

## Goals

Primary goals:
- Implement CRUD endpoints for a Product entity
- Implement a list endpoint that supports offset pagination, sorting, filtering and searching
- Keep defaults safe (default page size, max page size) and return useful pagination metadata
- Use Flyway for schema + sample data so the project can be run locally quickly

Optional/advanced goals (suggested learning extensions):
- Cursor (keyset) pagination for efficient deep paging
- PostgreSQL full-text search with GIN index
- DTOs / projections for lightweight listing responses
- Caching (e.g., Caffeine) for GET-by-id and selective list caching
- API versioning and OpenAPI documentation

---

## What this repo contains

- Java Spring Boot application (package: `com.ashish.ecommerce`)
- Controller: `ProductController` — HTTP endpoints
- Service: `ProductService` — business logic and mapping to DTOs
- Repository: `ProductRepository` — JPA repository + Specifications support
- Specification: `ProductSpecification` — dynamic predicate composition for filters/search
- Model: `Product` JPA entity
- Flyway migrations: `src/main/resources/db/migration` — schema + sample data

Project structure (top-level paths of interest):

- src/main/java/com/ashish/ecommerce/
  - controller/
  - service/
  - repository/
  - model/
  - specification/

- src/main/resources/db/migration
  - V1__create_product_table.sql
  - V2__insert_sample_products.sql

---

## Functional API summary

Base path: /api/v1/products

Endpoints (high level):
- POST /api/v1/products — Create product (body: ProductCreateDTO)
- GET /api/v1/products/{id} — Get product by id
- PUT /api/v1/products/{id} — Update product
- DELETE /api/v1/products/{id} — Delete product
- GET /api/v1/products — List products (supports pagination, sorting, filtering, search)

List query parameters (offset pagination)
- page (int, default = 0)
- size (int, default = 20; max = 100)
- sort (string) — multi-field support: e.g. `?sort=price,desc&sort=name,asc` or comma-separated `price,desc,name,asc`
- category (string) — exact or case-insensitive match
- minPrice (decimal)
- maxPrice (decimal)
- search (string) — partial match on name + description; optionally switch to full-text mode (see advanced options)
- mode (string, optional) — `fulltext` to enable DB full-text search where implemented

Example list request:

GET /api/v1/products?page=0&size=20&sort=price,desc&category=Accessories&minPrice=10

Example response (offset pagination):
- content: list of ProductDTO (or ProductSummaryDTO)
- pageable: { page, size, sort }
- totalElements
- totalPages
- number (current page)

Cursor (keyset) pagination (optional advanced mode)
- Use `cursor` param and `size` to page efficiently. Response includes `nextCursor` token to fetch the following page.
- Cursor is an opaque token (POC uses base64 JSON with last-seen sort values). Not suitable for production without signing/encryption.

---

## Data model (high level)

Product (suggested fields):
- id: BIGSERIAL (Long)
- name: VARCHAR(255) NOT NULL
- description: TEXT
- price: NUMERIC(13,2) NOT NULL — map to BigDecimal in Java
- category: VARCHAR(100)
- created_at: TIMESTAMPTZ DEFAULT now()
- updated_at: TIMESTAMPTZ

Flyway migration files in `src/main/resources/db/migration` create the table and insert example rows so you can run the app locally without manual SQL.

---

## Implementation notes (concise)

- Repository
  - `ProductRepository extends JpaRepository<Product, Long> & JpaSpecificationExecutor<Product>`
  - Use JPA Specifications to compose filtering and search predicates dynamically

- DTOs & Validation
  - Use request DTOs (e.g., ProductCreateDTO, ProductUpdateDTO) and response DTOs (ProductDTO, ProductSummaryDTO) to decouple API from persistence model
  - Use `@Valid` + validation annotations to enforce required fields and constraints

- Pagination & Sorting
  - Parse multiple `sort` parameters or comma-separated values into Spring `Sort`
  - Enforce a configurable max page size to avoid expensive queries

- Cursor Pagination (advanced)
  - When requested, build keyset predicates using the last-seen row values and sort directions
  - Generate `nextCursor` from last returned row; decode to continue paging

- Full-Text Search (advanced)
  - Optionally add a tsvector column and a GIN index (migration provided as an optional step)
  - Use `to_tsvector` and `plainto_tsquery` for relevance-ranked results

---

## Setup & run (local)

Prerequisites:
- JDK 17+ (project uses the JDK version specified in `pom.xml`)
- PostgreSQL (local or container)

1) Configure database connection
- Edit `src/main/resources/application.properties` and set the JDBC URL, username and password for your PostgreSQL instance.

2) Run the application (Windows example)

```powershell
# from repo root on Windows (uses bundled mvnw.cmd)
.\mvnw.cmd spring-boot:run
```

Flyway will run automatically on startup and apply migrations from `src/main/resources/db/migration`.

3) Run tests

```powershell
.\mvnw.cmd test
```

---

## Tests and acceptance criteria (what to validate)

Minimal verification to consider this POC successful:
- CRUD operations work and validation errors are returned for invalid requests
- List endpoint returns correct pagination metadata and enforces max page size
- Sorting and multi-field sorting work (stable tie-breaker ordering)
- Filtering by category and price range works as expected
- Search returns matching results (and full-text mode returns ranked results if enabled)
- Cursor pagination returns `nextCursor` and subsequent pages when used

Tests to implement:
- Unit tests for service mapping and business rules
- Repository/specification tests (use `@DataJpaTest` or Testcontainers Postgres)
- Controller integration tests (`@WebMvcTest` or full context tests)

---

## Suggested next improvements (pick one or more as follow-ups)

- Implement cursor/keyset pagination and demonstrate performance vs offset
- Add PostgreSQL full-text search and a GIN index; measure relevance and latency
- Add DTO projections and MapStruct for mapping
- Add caching for GET-by-id and carefully for list responses
- Add OpenAPI documentation with `springdoc-openapi` for interactive API docs
- Add a small GitHub Actions workflow to run tests on PRs

---

