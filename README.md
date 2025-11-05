# Microservice E-Commerce System

Hệ thống microservice quản lý bán hàng với Spring Boot, hỗ trợ xác thực JWT, quản lý sản phẩm, đơn hàng và người dùng.

## 🏗️ Kiến trúc hệ thống

```
Client → API Gateway (8083)
            │
            ├─► Auth Service (8086) ──► User Service (8082)
            │   [Login/Register]         [Internal API]
            │
            └─► Client Server (8087) ──┬─► User Service (8082)
                [JWT Validation]       ├─► Order Service (8081)
                [Authorization]        └─► Product Service (8084)

Discovery Server (8761) - Service Registry & Load Balancing
PostgreSQL (5433) - Database per Service (1 Database chung cho tất cả services)
```
```
                    ┌──────────────────────────────┐
                    │          Client (FE)          │
                    └───────────────┬───────────────┘
                                    │  (HTTP REST)
                                    ▼
                        ┌──────────────────────────┐
                        │    API Gateway (8083)    │
                        │   (Route requests only)  │
                        └──────────────┬───────────┘
                                       │
                                       ▼
                 ┌────────────────────────────────────┐
                 │   CLIENT-SERVER (ESB) (8087)       │
                 │  + JWT Validation & Authorization  │
                 │  + FeignClient Error Handling      │
                 │  + Business Logic Orchestration    │
                 └──────┬────────────┬────────────────┘
                        │            │
        ┌───────────────┼────────────┼────────────┐
        ▼               ▼            ▼            ▼
   AUTH-SERVICE   USER-SERVICE  ORDER-SERVICE  PRODUCT-SERVICE
   (No DB)         (userdb)      (orderdb)     (productdb)
     (8086)          (8082)     + order_details  (8084)
                                    (8081)
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
Auth Service ──(Internal API)──► User Service
                                  /api/internal/users

Order Service ──(Internal API)──► Product Service
                                   /api/internal/products/{id}/decrease-stock
                                   /api/internal/products/{id}/increase-stock
```

## Thay đổi quan trọng trong kiến trúc

### 1. ✅ Gộp OrderDetail vào Order Service
- **Trước**: OrderDetail là service riêng (port 8085)
- **Sau**: OrderDetail được gộp vào Order Service (port 8081)
- **Lý do**: Giảm độ phức tạp, Order và OrderDetail luôn đi cùng nhau

### 2. ✅ Business Services không dùng Spring Security
- **User Service, Order Service, Product Service** không có Spring Security
- **Auth Service + Client Server** xử lý toàn bộ authentication & authorization
- **Lợi ích**: Separation of Concerns, giảm complexity

### 3. ✅ FeignClient Error Handling
- **FeignErrorDecoder**: Parse error từ microservices
- **GlobalExceptionHandler**: Catch và format error response
- **Custom Exceptions**: BadRequestException, ResourceNotFoundException, InsufficientStockException

### 4. ✅ Order Creation Flow
```
1. Client gửi OrderRequest → Client Server
2. Client Server validate:
   - Kiểm tra User (UserClient)
   - Kiểm tra Products & Stock (ProductClient)
   - Tính totalAmount và subtotal
3. Gửi MapOrderRequest (Order + OrderDetails) → Order Service
4. Order Service:
   - Tạo Order trong database
   - Tạo OrderDetails trong database
   - Gọi ProductClient để decrease stock
5. Trả về OrderDetailResponse (Order + All OrderDetails)
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
- **Clent Server (Orchestrator/BFF)**: 


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

## Client Server (Orchestrator / BFF)

- Vai trò:
  - Điều phối (orchestration) nhiều microservice để hoàn tất một nghiệp vụ end-to-end.
  - Lớp BFF đứng giữa API Gateway và các Business Service (User/Order/Product).
  - Tập trung xác thực/ủy quyền (JWT, @PreAuthorize), chuẩn hóa lỗi, biến đổi dữ liệu, logging.

- Vì sao cần:
  - Nếu để một service domain tự gọi nhiều service khác, flow sẽ rối, khó debug và khó bảo trì.
  - Orchestrator tách logic điều phối khỏi domain service, giúp mỗi service tập trung vào nghiệp vụ cốt lõi.
  - Frontend chỉ cần gọi 1 API; lỗi được chuẩn hóa một chỗ.

- Cách hoạt động (ví dụ tạo đơn hàng):
  1) Kiểm tra user (User Service)
  2) Lấy/validate sản phẩm, tính tiền (Product Service)
  3) Tạo đơn (Order Service)
  4) Giảm tồn kho (Product Service)
  - Toàn bộ lỗi từ các service được parse qua FeignErrorDecoder và trả về theo ErrorResponse thống nhất.

- Ưu điểm:
  - Tập trung flow nghiệp vụ → dễ quan sát, debug, logging.
  - Chuẩn hóa lỗi và bảo mật tập trung.
  - Giảm số lần gọi từ frontend, dữ liệu được “enrich” trước khi gửi sang service đích.
  - Dễ áp dụng bù trừ (compensation) như hủy đơn hoàn kho.

- Nhược điểm:
  - Có thể trở thành điểm nghẽn/điểm lỗi đơn (cần scale và LB).
  - Tăng độ trễ do nhiều network hops.
  - Ràng buộc chặt với hợp đồng API của các service; cần chiến lược bù trừ khi một bước thất bại.
  - Nguy cơ “God Object” nếu dồn quá nhiều logic vào một nơi (nên tách theo use case/domain).

- Khi nên dùng:
  - Flow đa bước, cần kiểm soát thứ tự/điều kiện, cần error handling và bảo mật thống nhất.

- Khi cân nhắc giải pháp khác:
  - Hệ thống throughput rất cao hoặc thiên về event-driven → ưu tiên Choreography (publish/subcribe).

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

### 3. Auth Service (Port: 8086)
**Chức năng**: Xác thực và tạo JWT Token

**API Endpoints**:
- `POST /api/v1/auth/register` - Đăng ký tài khoản
- `POST /api/v1/auth/login` - Đăng nhập (trả về JWT)
- `POST /api/v1/auth/refresh` - Refresh token

**Dependencies**: UserClient → User Service (Internal API)

**Security**: ❌ KHÔNG có Spring Security

### 4. Client Server (Port: 8087)
**Chức năng**: API Orchestrator - Xử lý nghiệp vụ

**Vai trò**:
- Nhận request từ API Gateway
- Xác thực JWT token
- Kiểm tra phân quyền (`@PreAuthorize`)
- Validate business logic (check product stock, calculate total)
- Forward request đến service tương ứng
- **FeignClient Error Handling** - Catch và format errors từ services

**FeignClient**: UserClient, OrderClient, ProductClient

**Error Handling**:
- `FeignErrorDecoder` - Parse errors từ microservices
- `GlobalExceptionHandler` - Catch và format error response
- Custom Exceptions: `BadRequestException`, `ResourceNotFoundException`, `InsufficientStockException`

### 5. User Service (Port: 8082)
**Chức năng**: Quản lý người dùng

**Public API**:
- `GET /api/v1/users/{id}` - Chi tiết user
- `PUT /api/v1/users/{id}` - Cập nhật
- `DELETE /api/v1/users/{id}` - Xóa (ADMIN)

**Internal API**:
- `POST /api/internal/users` - Tạo user (từ Auth)
- `GET /api/internal/users/by-username/{username}` - Lấy user theo username

**Security**: ❌ KHÔNG có Spring Security (Business Service)

**Database**: userdb (PostgreSQL)

### 6. Order Service (Port: 8081)
**Chức năng**: Quản lý đơn hàng và chi tiết đơn hàng (đã gộp OrderDetail)

**Public API** (via Client Server):
- `POST /api/v1/orders` - Tạo đơn hàng (bao gồm order details)
- `GET /api/v1/orders/{id}` - Chi tiết đơn hàng (Order + OrderDetails)
- `GET /api/v1/orders/user/{userId}` - Đơn hàng của user
- `DELETE /api/v1/orders/{id}` - Xóa đơn hàng (ADMIN)

**Internal API**:
- `GET /api/internal/orders/{id}` - Lấy order với details
- `POST /api/internal/orders` - Tạo order (nhận MapOrderRequest với OrderItemDetail)

**Models**:
- `Order` - Thông tin đơn hàng
- `OrderDetail` - Chi tiết sản phẩm trong đơn (gộp vào Order Service)

**Business Logic**:
- Nhận `MapOrderRequest` từ Client Server (đã có đầy đủ thông tin: totalAmount, subtotal)
- Tạo Order trong database
- Tạo OrderDetails trong database
- Gọi ProductClient để decrease stock
- Trả về OrderDetailResponse (Order + All OrderDetails)

**Security**: ❌ KHÔNG có Spring Security

**Database**: orderdb (PostgreSQL) - bao gồm 2 tables: `orders`, `order_details`

### 7. Product Service (Port: 8084)
**Chức năng**: Quản lý sản phẩm và kho

**Public API** (via Client Server):
- `POST /api/v1/products/insert` - Tạo sản phẩm (ADMIN)
- `GET /api/v1/products/{id}` - Chi tiết sản phẩm
- `PUT /api/v1/products/{id}` - Cập nhật (ADMIN)
- `DELETE /api/v1/products/{id}` - Xóa (ADMIN)
- `GET /api/v1/products/search` - Tìm kiếm sản phẩm

**Internal API**:
- `GET /api/internal/products/{id}` - Lấy thông tin product
- `PATCH /api/internal/products/{productId}/decrease-stock?quantity=X` - Trừ kho
- `PATCH /api/internal/products/{productId}/increase-stock?quantity=X` - Tăng kho

**Error Handling**:
- `ResourceNotFoundException` - Product not found
- `InsufficientStockException` - Không đủ hàng
- `BadRequestException` - Invalid input

**Security**: ❌ KHÔNG có Spring Security

**Database**: productdb (PostgreSQL)

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
# Tạo đơn hàng (bao gồm order details)
POST http://localhost:8083/api/v1/orders
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "shippingAddress": "123 ABC Street, Ho Chi Minh",
  "paymentMethod": "COD",
  "note": "Giao hang nhanh",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}

# Response (OrderDetailResponse):
{
  "code": 201,
  "message": "Order created successfully",
  "data": {
    "orderId": 1,
    "fullName": "Nguyen Van A",
    "phone": "0123456789",
    "shippingAddress": "123 ABC Street, Ho Chi Minh",
    "status": "PENDING",
    "totalAmount": 55000000,
    "orderDate": "2025-10-25T10:30:00",
    "items": [
      {
        "orderDetailId": 1,
        "productId": 1,
        "productName": "iPhone 15",
        "quantity": 2,
        "price": 20000000,
        "subtotal": 40000000
      },
      {
        "orderDetailId": 2,
        "productId": 2,
        "productName": "Samsung S24",
        "quantity": 1,
        "price": 15000000,
        "subtotal": 15000000
      }
    ]
  }
}

# Lấy chi tiết đơn hàng (Order + OrderDetails)
GET http://localhost:8083/api/v1/orders/1
Authorization: Bearer <access_token>

# Lấy tất cả đơn hàng của user
GET http://localhost:8083/api/v1/orders/user/1
Authorization: Bearer <access_token>
```

## Error Handling

### Error Response Format

```json
{
  "timestamp": "2025-10-25T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id=999",
  "path": "/api/v1/products/999"
}
```

### Common Error Codes

| Status | Error | Mô tả |
|--------|-------|-------|
| 400 | Bad Request | Invalid input data, validation failed |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Không có quyền truy cập resource |
| 404 | Not Found | Resource không tồn tại |
| 409 | Conflict | Insufficient stock, business rule violation |
| 500 | Internal Server Error | Unexpected server error |

### FeignClient Error Handling

**Client Server** sử dụng `FeignErrorDecoder` để parse errors từ microservices:

```java
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        // Parse error từ Product/Order/User Service
        return switch (response.status()) {
            case 400 -> new BadRequestException(message);
            case 404 -> new ResourceNotFoundException(message);
            case 409 -> new InsufficientStockException(message);
            default -> new RuntimeException(message);
        };
    }
}
```

**GlobalExceptionHandler** catch và format error response:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex) {
        // Format và trả về error cho client
    }
}
```

### Example Error Responses

**Insufficient Stock:**
```bash
POST http://localhost:8083/api/v1/orders

Response:
{
  "timestamp": "2025-10-25T10:31:00",
  "status": 409,
  "error": "Insufficient Stock",
  "message": "Không đủ số lượng hàng cho sản phẩm 'iPhone 15': có sẵn 10, yêu cầu 100",
  "path": "/api/v1/orders"
}
```

**Product Not Found:**
```bash
GET http://localhost:8083/api/v1/products/999

Response:
{
  "timestamp": "2025-10-25T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id=999",
  "path": "/api/v1/products/999"
}
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
| Auth Service | User Service | Internal | `/api/internal/users` | No Security |
| Client Server | User/Order/Product | Public | `/api/v1/**` | JWT Token (validated) |
| Order Service | Product Service | Internal | `/api/internal/products/{id}/decrease-stock` | No Security |

## Port Summary

| Service | Port | Exposed | Database |
|---------|------|---------|----------|
| API Gateway | 8083 | ✅ | - |
| Discovery Server | 8761 | ✅ | - |
| Auth Service | 8086 | ❌ (via Gateway) | - |
| User Service | 8082 | ❌ | userdb |
| Order Service | 8081 | ❌ | orderdb (orders + order_details) |
| Product Service | 8084 | ❌ | productdb |
| Client Server | 8087 | ❌ (via Gateway) | - |
| PostgreSQL | 5433 | ❌ | 1 DB cho tất cả services |

**Chỉ API Gateway và Eureka được expose ra ngoài để đảm bảo security.**

## Cần bổ sung

### Phase 2 - In Progress
- [x] ✅ Gộp OrderDetail vào Order Service
- [x] ✅ FeignClient Error Handling
- [x] ✅ Global Exception Handler
- [x] ✅ Order creation với validation đầy đủ
- [ ] Unit Tests cho các services
- [ ] Integration Tests

### Phase 3 - Planned
- [ ] Redis Cache cho performance
- [ ] Message Queue (RabbitMQ/Kafka) cho async processing
- [ ] Distributed Tracing (Zipkin) để debug
- [ ] Centralized Configuration (Spring Cloud Config)
- [ ] API Rate Limiting

### Phase 3 - Future
- [ ] Kubernetes deployment
### Phase 3 - Planned
- [ ] Redis Cache for performance
- [ ] Message Queue (RabbitMQ/Kafka)
- [ ] Distributed Tracing (Zipkin)
- [ ] Centralized Configuration (Spring Cloud Config)
- [ ] API Rate Limiting
- [ ] Kubernetes deployment
- [ ] CI/CD Pipeline (GitHub Actions)
- [ ] Monitoring (Prometheus + Grafana)
- [ ] ELK Stack cho centralized logging

## FAQ

**Q: Tại sao gộp OrderDetail vào Order Service?**  
A: Order và OrderDetail luôn đi cùng nhau, việc tách riêng tạo thêm network overhead và complexity không cần thiết. Gộp lại giúp transaction đơn giản hơn.

**Q: Tại sao Business Services không có Spring Security?**  
A: Auth Service + Client Server đã xử lý authentication/authorization. Business Services chỉ lo business logic, giảm complexity và separation of concerns.

**Q: Internal API có an toàn không khi không có security?**  
A: An toàn vì chỉ accessible trong Docker network, không expose port ra ngoài. Chỉ các services trong cùng network mới gọi được.

**Q: Làm sao handle errors từ microservices?**  
A: Sử dụng FeignErrorDecoder để parse errors từ services, sau đó GlobalExceptionHandler catch và format thành ErrorResponse chuẩn cho client.

**Q: Order creation flow hoạt động thế nào?**  
A: Client Server validate (check user, check products & stock, calculate total) → Gửi MapOrderRequest đến Order Service → Order Service tạo Order + OrderDetails → Gọi Product Service để decrease stock.

**Q: Làm sao scale service?**  
A: `docker-compose up -d --scale user-service=3` - Load balancing tự động qua Eureka.

---

**🚀 Microservice E-Commerce System - Spring Boot + Spring Cloud**