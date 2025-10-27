# Microservice E-Commerce System

Hệ thống microservice quản lý bán hàng với Spring Boot, hỗ trợ xác thực JWT, quản lý sản phẩm, đơn hàng và người dùng.

## 🏗️ Kiến trúc hệ thống

```
Client → API Gateway (8083)
            │
            ├─► Auth Service (8081) ──► User Service (8082)
            │   [Login/Register]         [Internal API]
            │
            └─► Client Server (8087) ──┬─► User Service (8082)
                [JWT Validation]       ├─► Order Service (8083)
                                      ├─► Product Service (8085)
                                      └─► OrderDetail Service (8084)

Discovery Server (8761) - Service Registry & Load Balancing
PostgreSQL (5432) - Database per Service
```
```
                    ┌──────────────────────────────┐
                    │          Client (FE)          │
                    └───────────────┬───────────────┘
                                    │  (HTTP REST)
                                    ▼
                        ┌──────────────────────────┐
                        │       API Gateway(8083)  │
                        │ (Chỉ định tuyến request) │
                        └──────────────┬───────────┘
                                       │
                                       ▼
                 ┌───────────────────────────────┐
                 │   CLIENT-SERVER (ESB) (8087)  │
                 │  Điều hướng và gọi service    │
                 │  + Gọi AuthService để check   │
                 └──────┬────────────┬───────────┘
                        │            │
        ┌───────────────┼────────────┼────────────────────┐
        ▼               ▼            ▼                    ▼
   USER-SERVICE   ORDER-SERVICE   PRODUCT-SERVICE   ORDERDETAIL-SERVICE
   (DB: users)     (DB: orders)   (DB: products)   (DB: order_details)
     (8082)           (8083)        (8085)              (8084)
```
### Luồng xử lý request

#### 1. **Authentication Flow (Đăng ký/Đăng nhập)**
```
Client → API Gateway (8083) → Auth Service (8081)
                                     │
                                     └─► UserClient (Internal)
                                             │
                                             ▼
                                      User Service (8082)
                                      /api/internal/users
```

#### 2. **Business API Flow (User/Order/Product)**
```
Client → API Gateway (8083) → Client Server (8087)
              │                      │
              │                      ├─► JWT Validation
              │                      ├─► Authorization (@PreAuthorize)
              │                      │
              │                      ▼
              │               FeignClient Forward
              │                      │
              ├──────────────────────┼─────────────────┐
              │                      │                 │
              ▼                      ▼                 ▼
         User Service          Order Service     Product Service
         /api/v1/users         /api/v1/orders   /api/v1/products
```

#### 3. **Internal Service Communication**
```
Order Service ──(Internal API)──► User Service
                                   /api/internal/users

OrderDetail Service ──(Internal API)──┬──► Order Service
                                       │    /api/internal/orders
                                       │
                                       └──► Product Service
                                            /api/internal/products
```

## Công nghệ sử dụng

### Backend Framework
- **Spring Boot 3.5.5**: Framework chính
- **Spring Cloud 2025.0.0**: Microservices infrastructure
- **Java 21**: Programming language

### Microservices Components
- **Spring Cloud Gateway**: API Gateway
- **Spring Cloud Netflix Eureka**: Service Discovery
- **Spring Cloud OpenFeign**: Declarative REST Client
- **Spring Cloud LoadBalancer**: Client-side load balancing

### Security
- **Spring Security**: Authentication & Authorization
- **JWT (JSON Web Token)**: Token-based authentication
- **BCrypt**: Password hashing

### Database
- **PostgreSQL 15**: Relational database
- **MyBatis**: SQL Mapper Framework

### Build & Deploy
- **Maven**: Dependency management và build tool
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration

### Other Libraries
- **Lombok**: Reduce boilerplate code
- **Validation API**: Request validation
- **Spring Boot Actuator**: Monitoring & Health check

## Best Practices Implemented

### 1. Microservices Patterns
- ✅ **API Gateway Pattern**: Single entry point
- ✅ **Service Registry Pattern**: Eureka Discovery
- ✅ **Database per Service**: Isolated databases
- ✅ **API Composition Pattern**: Client Server orchestration
- ✅ **Circuit Breaker**: Resilience (via Feign)

### 2. Security Best Practices
- ✅ JWT-based authentication
- ✅ Role-based authorization (RBAC)
- ✅ API Key for internal services
- ✅ Password encryption (BCrypt)
- ✅ Separate public and internal APIs

### 3. Code Quality
- ✅ Separation of Concerns
- ✅ DTO Pattern (Request/Response)
- ✅ Repository Pattern
- ✅ Service Layer Pattern
- ✅ Exception Handling (GlobalExceptionHandler)

### 4. DevOps
- ✅ Containerization with Docker
- ✅ Environment variables configuration
- ✅ Health check endpoints
- ✅ Centralized logging

## Testing

### Manual Testing với Postman/cURL

#### 1. Đăng ký user
```bash
curl -X POST http://localhost:8083/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "USER"
  }'
```

#### 2. Đăng nhập
```bash
curl -X POST http://localhost:8083/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

#### 3. Sử dụng token để gọi API
```bash
# Lưu token vào biến
TOKEN="<access_token_from_login>"

# Gọi API với token
curl -X GET http://localhost:8083/api/v1/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Unit Testing
```bash
# Run tests cho một service
cd user-service
mvn test

# Run tests cho tất cả services
mvn test
```

## Chi tiết các Service

### 1. API Gateway (Port: 8083)
- **Entry point** cho toàn bộ hệ thống
- **Routing**: `/api/v1/auth/**` → Auth Service | `/api/v1/**` → Client Server
- **Load balancing** tích hợp Eureka

### 2. Discovery Server (Port: 8761)
- **Eureka Server** - Service registry & discovery
- **Dashboard**: http://localhost:8761

### 3. Auth Service (Port: 8081)
**Chức năng**: Xác thực và tạo JWT Token

**API Endpoints**:
- `POST /api/v1/auth/register` - Đăng ký tài khoản
- `POST /api/v1/auth/login` - Đăng nhập (trả về JWT)
- `POST /api/v1/auth/refresh` - Refresh token

**Dependencies**: UserClient → User Service (Internal API)

### 4. Client Server (Port: 8087)
**Chức năng**: API Orchestrator - Xử lý nghiệp vụ

**Vai trò**:
- Nhận request từ API Gateway
- Xác thực JWT token
- Kiểm tra phân quyền (`@PreAuthorize`)
- Forward request đến service tương ứng

**FeignClient**: UserClient, OrderClient, ProductClient

### 5. User Service (Port: 8082)
**Chức năng**: Quản lý người dùng

**Public API**:
- `GET /api/v1/users/{id}` - Chi tiết user
- `PUT /api/v1/users/{id}` - Cập nhật
- `DELETE /api/v1/users/{id}` - Xóa (ADMIN)

**Internal API**:
- `POST /api/internal/users` - Tạo user (từ Auth)
- `GET /api/internal/users/by-username/{username}`

**Security**: ❌ KHÔNG có Spring Security (Business Service)

### 6. Order Service (Port: 8083)
**Chức năng**: Quản lý đơn hàng

**Public API**:
- `POST /api/v1/orders` - Tạo đơn hàng
- `GET /api/v1/orders/{id}` - Chi tiết
- `GET /api/v1/orders/user/{userId}` - Đơn của user
- `DELETE /api/v1/orders/{id}` - Xóa (ADMIN)

**Internal API**:
- `PUT /api/internal/orders/{orderId}/increase-total`
- `PUT /api/internal/orders/{orderId}/decrease-total`

**Dependencies**: UserClient → User Service

### 7. OrderDetail Service (Port: 8084)
**Chức năng**: Quản lý chi tiết đơn hàng

**API Endpoints**:
- `POST /api/v1/order-details` - Thêm sản phẩm vào đơn
- `PUT /api/v1/order-details/{id}` - Cập nhật số lượng
- `DELETE /api/v1/order-details/{id}` - Xóa sản phẩm

**Business Logic**:
- Thêm sản phẩm → Trừ kho + Tăng tổng tiền
- Xóa sản phẩm → Hoàn kho + Giảm tổng tiền

**Dependencies**: OrderClient, ProductClient

### 8. Product Service (Port: 8085)
**Chức năng**: Quản lý sản phẩm và kho

**Public API**:
- `POST /api/v1/products/insert` - Tạo sản phẩm (ADMIN)
- `GET /api/v1/products/{id}` - Chi tiết
- `PUT /api/v1/products/{id}` - Cập nhật (ADMIN)
- `DELETE /api/v1/products/{id}` - Xóa (ADMIN)
- `GET /api/v1/products/search` - Tìm kiếm

**Internal API**:
- `PUT /api/internal/products/{productId}/decrease-stock` - Trừ kho
- `PUT /api/internal/products/{productId}/increase-stock` - Tăng kho

## Cài đặt và Chạy

### Yêu cầu
- Java 21+
- Maven 3.8+
- Docker & Docker Compose

### Chạy hệ thống

```bash
# 1. Clone repository
git clone <repository-url>
cd Microservice-be

# 2. Build tất cả service
mvn clean package -DskipTests

# 3. Chạy Docker Compose
docker-compose up -d

# 4. Xem logs
docker-compose logs -f

# 5. Kiểm tra Eureka Dashboard
# http://localhost:8761
```

### Dừng hệ thống

```bash
# Dừng containers
docker-compose down

# Reset database
docker-compose down -v
```

## API Examples

### 1. Authentication

```bash
# Đăng ký
POST http://localhost:8083/api/v1/auth/register
Content-Type: application/json
{
  "username": "admin",
  "password": "admin123",
  "email": "admin@example.com",
  "firstName": "Admin",
  "lastName": "User",
  "roleId": 1
}

# Đăng nhập
POST http://localhost:8083/api/v1/auth/login
Content-Type: application/json
{
  "userName": "admin",
  "password": "admin123"
}

# Response
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 3600
}
```

### 2. User Management

```bash
# Lấy thông tin user (ADMIN or USER)
GET http://localhost:8083/api/v1/users/1
Authorization: Bearer <access_token>

# Cập nhật user
PUT http://localhost:8083/api/v1/users/1
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "firstName": "Updated",
  "lastName": "Name",
  "email": "updated@example.com"
}
```

### 3. Product Management

```bash
# Tạo sản phẩm (ADMIN only)
POST http://localhost:8083/api/v1/products/insert
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "name": "Laptop Dell XPS 15",
  "description": "High performance laptop",
  "price": 35000000,
  "stock": 50,
  "categoryId": 1
}

# Tìm kiếm sản phẩm
GET http://localhost:8083/api/v1/products/search?keyword=laptop&page=0&size=10
Authorization: Bearer <access_token>
```

### 4. Order Management

```bash
# Tạo đơn hàng
POST http://localhost:8083/api/v1/orders
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "userId": 1
}

# Thêm sản phẩm vào đơn
POST http://localhost:8083/api/v1/order-details
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "orderId": 1,
  "productId": 1,
  "quantity": 2
}

# Lấy đơn hàng của user
GET http://localhost:8083/api/v1/orders/user/1
Authorization: Bearer <access_token>
```

## Security Architecture

### JWT Authentication
- **Access Token**: Thời gian sống 1 giờ
- **Refresh Token**: Thời gian sống 7 ngày
- **Header**: `Authorization: Bearer <token>`

### Role-based Authorization
| Role | Quyền hạn |
|------|-----------|
| **ADMIN** | Toàn quyền CRUD tất cả resources |
| **USER** | Xem và quản lý thông tin cá nhân, tạo đơn hàng |

### Internal API Security
- **KHÔNG dùng API Key** - Business Services không có Spring Security
- **Network Isolation** - Chỉ accessible trong Docker network
- **Separate Endpoints** - `/api/internal/**` vs `/api/v1/**`

**Lý do**: Auth Service + Client Server đã xử lý authentication/authorization, các Business Services chỉ lo business logic.

## Service Communication

| From | To | Type | Path | Auth |
|------|----|----|------|------|
| Auth | User | Internal | `/api/internal/users` | No Security |
| Client Server | User/Order/Product | Public | `/api/v1/**` | JWT Token |
| Order | User | Internal | `/api/internal/users` | No Security |
| OrderDetail | Order/Product | Internal | `/api/internal/**` | No Security |

## Port Summary

| Service | Port | Exposed |
|---------|------|---------|
| API Gateway | 8083 | ✅ |
| Discovery Server | 8761 | ✅ |
| Auth Service | 8081 | ❌ (via Gateway) |
| User Service | 8082 | ❌ |
| Order Service | 8083 | ❌ |
| OrderDetail Service | 8084 | ❌ |
| Product Service | 8085 | ❌ |
| Client Server | 8087 | ❌ (via Gateway) |
| PostgreSQL | 5432 | ❌ |

**Chỉ API Gateway và Eureka được expose ra ngoài để đảm bảo security.**

## Cần bổ sung

### Phase 2 - Planned
- [ ] Redis Cache cho performance
- [ ] Message Queue (RabbitMQ/Kafka) cho async processing
- [ ] Distributed Tracing (Zipkin) để debug
- [ ] Centralized Configuration (Spring Cloud Config)
- [ ] API Rate Limiting

### Phase 3 - Future
- [ ] Kubernetes deployment
- [ ] CI/CD Pipeline (GitHub Actions)
- [ ] Monitoring (Prometheus + Grafana)
- [ ] ELK Stack cho centralized logging
- [ ] Unit & Integration Tests

## FAQ

**Q: Tại sao Business Services không có Spring Security?**  
A: Auth Service + Client Server đã xử lý authentication/authorization. Business Services chỉ lo business logic, giảm complexity.

**Q: Internal API có an toàn không khi không có security?**  
A: An toàn vì chỉ accessible trong Docker network, không expose port ra ngoài.

**Q: Làm sao scale service?**  
A: `docker-compose up -d --scale user-service=3` - Load balancing tự động qua Eureka.

---

**🚀 Microservice E-Commerce System - Spring Boot + Spring Cloud**