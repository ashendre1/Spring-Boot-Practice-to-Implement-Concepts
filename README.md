# Spring WebFlux Microservices Practice

A practice project featuring two reactive microservices built with **Spring WebFlux** and **PostgreSQL** using R2DBC (Reactive Relational Database Connectivity).

## 📋 Project Structure

```
spring-webflux-microservices/
├── product-service/          # Product management microservice (Port 8081)
├── order-service/            # Order management microservice (Port 8082)
├── docker-compose.yml        # PostgreSQL databases setup
└── pom.xml                   # Parent POM
```

## 🛠️ Prerequisites

| Tool | Status | Installation |
|------|--------|--------------|
| JDK 17 | ✅ Installed | - |
| Maven 3.9.12 | ✅ Installed | `C:\Users\Shendre\.maven\maven-3.9.12\bin` |
| Docker | ⚠️ Required | [Download Docker Desktop](https://www.docker.com/products/docker-desktop/) |

## 🚀 Getting Started

### Step 1: Install Docker Desktop

1. Download from: https://www.docker.com/products/docker-desktop/
2. Install and restart your PC if needed
3. Open Docker Desktop and wait for it to start

### Step 2: Start PostgreSQL Databases

```powershell
cd c:\Projects\Spring-Boot-practice
docker-compose up -d
```

This starts two PostgreSQL instances:
- **Product DB**: localhost:5432 (database: `productdb`)
- **Order DB**: localhost:5433 (database: `orderdb`)

### Step 3: Build the Project

```powershell
mvn clean install -DskipTests
```

### Step 4: Run the Services

**Terminal 1 - Product Service:**
```powershell
cd product-service
mvn spring-boot:run
```

**Terminal 2 - Order Service:**
```powershell
cd order-service
mvn spring-boot:run
```

## 📡 API Endpoints

### Product Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/category/{category}` | Get products by category |
| GET | `/api/products/search?name=...` | Search products by name |
| POST | `/api/products` | Create new product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

### Order Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/number/{orderNumber}` | Get order by order number |
| GET | `/api/orders/customer/{customerId}` | Get orders by customer |
| POST | `/api/orders` | Create new order |
| PATCH | `/api/orders/{id}/status?status=...` | Update order status |
| DELETE | `/api/orders/{id}` | Cancel order |

## 📝 Sample API Calls

### Create a Product
```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "Gaming laptop with RTX 4080",
    "price": 1999.99,
    "quantity": 10,
    "category": "Electronics"
  }'
```

### Create an Order
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "customerEmail": "customer@example.com",
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }'
```

## 🔍 Health Check Endpoints

- Product Service: http://localhost:8081/actuator/health
- Order Service: http://localhost:8082/actuator/health

## 📚 Technologies Used

- **Spring Boot 3.2.2**
- **Spring WebFlux** - Reactive web framework
- **R2DBC** - Reactive database connectivity
- **PostgreSQL 15** - Relational database
- **Project Lombok** - Reduce boilerplate code
- **Docker Compose** - Container orchestration

## 🏗️ Architecture

```
┌─────────────────┐         ┌─────────────────┐
│                 │         │                 │
│  Order Service  │────────▶│ Product Service │
│   (Port 8082)   │  HTTP   │   (Port 8081)   │
│                 │         │                 │
└────────┬────────┘         └────────┬────────┘
         │                           │
         │ R2DBC                     │ R2DBC
         │                           │
         ▼                           ▼
┌─────────────────┐         ┌─────────────────┐
│   Order DB      │         │   Product DB    │
│   (Port 5433)   │         │   (Port 5432)   │
└─────────────────┘         └─────────────────┘
```

## 🛑 Stopping the Services

```powershell
# Stop PostgreSQL containers
docker-compose down

# To remove data volumes as well
docker-compose down -v
```
