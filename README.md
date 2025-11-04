# E-commerce Product Catalog API POC

## Objective
Build a Spring Boot REST API to manage products in an e-commerce catalog, demonstrating pagination, sorting, filtering, searching, and integration with PostgreSQL and Flyway.

---

## Features

1. **Database Integration**
   - Use PostgreSQL for data storage.
   - Manage schema migrations with Flyway.

2. **Product Entity**
   - Fields: `id` (Long, PK), `name` (String), `description` (String), `price` (BigDecimal), `category` (String), `createdDate` (Timestamp).

3. **CRUD Operations**
   - Create, Read, Update, Delete products via REST endpoints.

4. **Pagination**
   - List products with page number and size parameters.

5. **Sorting**
   - Sort products by `price`, `name`, or `createdDate` (ascending/descending).

6. **Filtering**
   - Filter products by `category` and by `price` range (`minPrice`, `maxPrice`).

7. **Searching**
   - Search products by partial match on `name` or `description`.

8. **DTOs**
   - Use Data Transfer Objects for API requests and responses.

9. **Validation**
   - Validate product data on create/update (e.g., non-empty name, positive price).

10. **Global Exception Handling**
    - Return meaningful error responses for invalid requests.

11. **API Documentation**
    - Use Swagger/OpenAPI for endpoint documentation.

12. **Unit Tests**
    - Write tests for controller and service layers.

---

## Explanation

- **Why these features?**  
  They cover real-world requirements for listing, searching, and managing products, and demonstrate key Spring Boot concepts.

- **How to implement?**  
  - Define the `Product` entity and repository.
  - Use Spring Data JPA for data access.
  - Implement REST controllers for CRUD and listing endpoints.
  - Use request parameters for pagination, sorting, filtering, and searching.
  - Apply validation annotations in DTOs.
  - Configure Flyway for DB migrations.
  - Add Swagger for API docs.
  - Write unit tests for business logic and endpoints.

This POC will help you learn how to build robust, maintainable APIs with Spring Boot and best practices for data handling.

