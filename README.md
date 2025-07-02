# Customer Management API

A Spring Boot RESTful API for managing customer data with tier calculation based on annual spend.

## Features

- CRUD operations for customer data
- Tier calculation (Silver, Gold, Platinum) based on annual spend and last purchase date
- H2 in-memory database for development
- OpenAPI documentation
- Detailed unit tests

## Technologies

- Java 24
- Spring Boot 3.x
- H2 Database
- Maven
- Lombok
- OpenAPI 3.0

## Setup and Running

1. **Prerequisites**
   - Java 24 JDK installed
   - Maven installed

2. **Clone the repository**
   ```bash
   git clone https://github.com/your-repo/customer-management-api.git
   cd customer-management-api
   
3. **Build the application**
   ```bash
   mvn clean install
      
4. **Run the application**
   ```bash
   mvn spring-boot:run
  ```
    The application will start on http://localhost:8080
