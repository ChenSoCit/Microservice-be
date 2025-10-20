# Microservice E-Commerce System

## Giới thiệu
Đây là một hệ thống microservice được xây dựng bằng Spring Boot, quản lý việc bán hàng với các tính năng như xác thực người dùng, quản lý sản phẩm, đơn hàng và thanh toán.

## Kiến trúc hệ thống

### Sơ đồ kiến trúc
```
                          Client Request
                                │
                                ▼
                      ┌──────────────────┐
                      │   API Gateway    │ (Port: 8083)
                      │  (Entry Point)   │
                      └────────┬─────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ▼                             ▼
    ┌─────────────────────┐      ┌─────────────────────┐
    │   Auth Service      │      │   Client Server     │ (Port: 8087)
    │   (Port: 8081)      │      │  (API Orchestrator) │
    │  - Login/Register   │      │  - JWT Validation   │
    │  - JWT Generation   │      │  - Authorization    │
    └─────────┬───────────┘      └──────────┬──────────┘
              │                              │
              │                   ┌──────────┼──────────┐
              │                   │          │          │
              ▼                   ▼          ▼          ▼
    ┌──────────────────┐   ┌──────────┐ ┌──────┐ ┌─────────┐
    │  User Service    │◄──│  Order   │ │Order │ │ Product │
    │  (Port: 8082)    │   │ Service  │ │Detail│ │ Service │
    │  - User CRUD     │   │ (8083)   │ │(8084)│ │ (8085)  │
    │  - Internal API  │   └────┬─────┘ └──┬───┘ └────┬────┘
    └──────────────────┘        │           │         │
                                └───────────┴─────────┘
                                 Service-to-Service
                                  (Internal API)

                      ┌──────────────────┐
                      │ Discovery Server │ (Port: 8761)
                      │  (Eureka Server) │
                      │  - Service Reg   │
                      │  - Load Balance  │
                      └──────────────────┘
```

### Luồng xử lý request

#### 1. **Authentication Flow (Đăng ký/Đăng nhập)**
```
Client → API Gateway (8083) → Auth Service (8081)
                                     │
                                     ├─► UserClient (Internal)
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
- **Chức năng**: Entry point cho toàn bộ hệ thống
- **Routing**:
  - `/api/v1/auth/**` → Auth Service (8081)
  - `/api/v1/**` → Client Server (8087)
- **Công nghệ**: Spring Cloud Gateway
- **Load balancing**: Tích hợp với Eureka

### 2. Discovery Server (Port: 8761)
- **Chức năng**: Service registry và discovery
- **Công nghệ**: Eureka Server
- **URL Dashboard**: http://localhost:8761
- **Tính năng**:
  - Đăng ký service
  - Health monitoring
  - Load balancing

### 3. Auth Service (Port: 8081)
- **Chức năng**: Xác thực và phân quyền
- **API Endpoints**:
  - `POST /api/v1/auth/register` - Đăng ký tài khoản
  - `POST /api/v1/auth/login` - Đăng nhập
  - `POST /api/v1/auth/refresh` - Refresh token
  - `GET /api/v1/auth/verify` - Xác thực token
- **Dependencies**: 
  - UserClient → User Service (Internal API)
- **Security**: JWT Token Generation

### 4. Client Server (Port: 8087)
- **Chức năng**: API Orchestrator - Lớp trung gian xử lý nghiệp vụ
- **Vai trò**:
  - Nhận request từ API Gateway
  - Xác thực JWT token
  - Kiểm tra phân quyền (@PreAuthorize)
  - Forward request đến service tương ứng
- **FeignClient**:
  - UserClient → User Service
  - OrderClient → Order Service
  - ProductClient → Product Service
  - AuthClient → Auth Service (verify token)
- **Security**: JWT Validation, Role-based Authorization

### 5. User Service (Port: 8082)
- **Chức năng**: Quản lý thông tin người dùng
- **Public API** (`/api/v1/users`):
  - `GET /api/v1/users/{id}` - Lấy thông tin user
  - `GET /api/v1/users/{userId}/orders` - Lấy đơn hàng của user
  - `PUT /api/v1/users/{id}` - Cập nhật user
  - `DELETE /api/v1/users/{id}` - Xóa user
- **Internal API** (`/api/internal/users`):
  - `POST /api/internal/users` - Tạo user (từ Auth Service)
  - `GET /api/internal/users/by-username/{username}` - Lấy user theo username
  - `GET /api/internal/users/check-role/{userId}` - Kiểm tra role
- **Dependencies**: 
  - OrderClient → Order Service (Internal API)
- **Database**: PostgreSQL (userdb)

### 6. Order Service (Port: 8083)
- **Chức năng**: Quản lý đơn hàng
- **Public API** (`/api/v1/orders`):
  - `POST /api/v1/orders` - Tạo đơn hàng
  - `GET /api/v1/orders/{id}` - Chi tiết đơn hàng
  - `GET /api/v1/orders/user/{userId}` - Đơn hàng của user
  - `GET /api/v1/orders/statistics/by-user/{userId}` - Thống kê
  - `DELETE /api/v1/orders/{id}` - Xóa đơn hàng
- **Internal API** (`/api/internal/orders`):
  - `PUT /api/internal/orders/{orderId}/increase-total` - Tăng tổng tiền
  - `PUT /api/internal/orders/{orderId}/decrease-total` - Giảm tổng tiền
  - `DELETE /api/internal/orders/{orderId}` - Xóa đơn (internal)
- **Dependencies**: 
  - UserClient → User Service (Internal API)
- **Database**: PostgreSQL (orderdb)

### 7. Order Detail Service (Port: 8084)
- **Chức năng**: Quản lý chi tiết đơn hàng (sản phẩm trong đơn)
- **API Endpoints**:
  - `POST /api/v1/order-details` - Thêm sản phẩm vào đơn
  - `PUT /api/v1/order-details/{id}` - Cập nhật số lượng
  - `DELETE /api/v1/order-details/{id}` - Xóa sản phẩm khỏi đơn
  - `GET /api/v1/order-details/statistics/top-products` - Top sản phẩm bán chạy
- **Dependencies**: 
  - OrderClient → Order Service (Internal API)
  - ProductClient → Product Service (Internal API)
- **Business Logic**:
  - Thêm sản phẩm → Trừ kho + Tăng tổng tiền đơn
  - Xóa sản phẩm → Tăng kho + Giảm tổng tiền đơn
- **Database**: PostgreSQL (orderdetaildb)

### 8. Product Service (Port: 8085)
- **Chức năng**: Quản lý sản phẩm và kho
- **Public API** (`/api/v1/products`):
  - `POST /api/v1/products/insert` - Tạo sản phẩm
  - `GET /api/v1/products/{id}` - Chi tiết sản phẩm
  - `PUT /api/v1/products/{id}` - Cập nhật sản phẩm
  - `DELETE /api/v1/products/{id}` - Xóa sản phẩm
  - `GET /api/v1/products/search` - Tìm kiếm sản phẩm
- **Internal API** (`/api/internal/products`):
  - `PUT /api/internal/products/{productId}/decrease-stock` - Trừ kho
  - `PUT /api/internal/products/{productId}/increase-stock` - Tăng kho
- **Database**: PostgreSQL (productdb)

## Luồng xử lý chính

### 1. Đăng ký tài khoản
```
1. Client → POST /api/v1/auth/register
2. API Gateway → Auth Service
3. Auth Service → UserClient.create()
4. User Service (Internal API) → Tạo user trong DB
5. Response: UserResponse
```

### 2. Đăng nhập
```
1. Client → POST /api/v1/auth/login
2. API Gateway → Auth Service
3. Auth Service → UserClient.getByUserName()
4. User Service (Internal API) → Lấy thông tin user
5. Auth Service → Xác thực password + Tạo JWT Token
6. Response: JwtTokenResponse (accessToken, refreshToken)
```

### 3. Gọi API nghiệp vụ (ví dụ: Lấy thông tin user)
```
1. Client → GET /api/v1/users/{id} (với JWT trong header)
2. API Gateway → Client Server
3. Client Server:
   - Xác thực JWT token
   - Kiểm tra role (@PreAuthorize)
   - Forward: UserClient.getUser(id)
4. User Service → Truy vấn DB
5. Response: UserResponse
```

### 4. Tạo đơn hàng
```
1. Client → POST /api/v1/orders (với JWT)
2. API Gateway → Client Server
3. Client Server → OrderClient.createOrder()
4. Order Service:
   - Xác thực user (UserClient)
   - Tạo đơn hàng trong DB
5. Response: Order ID
```

### 5. Thêm sản phẩm vào đơn hàng
```
1. Client → POST /api/v1/order-details
2. OrderDetail Service:
   - ProductClient.decreaseStock() → Trừ kho sản phẩm
   - OrderClient.increaseTotalAmount() → Tăng tổng tiền đơn
   - Lưu chi tiết đơn hàng
3. Response: OrderDetail created
```

### 6. Xóa sản phẩm khỏi đơn hàng
```
1. Client → DELETE /api/v1/order-details/{id}
2. OrderDetail Service:
   - ProductClient.increaseStock() → Hoàn kho
   - OrderClient.decreaseTotalAmount() → Giảm tổng tiền
   - Xóa chi tiết đơn
3. Response: Success
```

## Cài đặt và Chạy

### Yêu cầu hệ thống
- Java 21+
- Maven 3.8+
- Docker và Docker Compose
- PostgreSQL

### Các bước cài đặt

#### 1. Clone repository
```bash
git clone <repository-url>
cd training-microservice
```

#### 2. Build các service
```bash
# Build tất cả service
mvn clean package -DskipTests

# Hoặc build từng service
cd auth-service && mvn clean package -DskipTests
cd ../user-service && mvn clean package -DskipTests
cd ../order-service && mvn clean package -DskipTests
cd ../orderdetail-service && mvn clean package -DskipTests
cd ../product-service && mvn clean package -DskipTests
cd ../client-server && mvn clean package -DskipTests
cd ../api-gateway && mvn clean package -DskipTests
cd ../discovery-server && mvn clean package -DskipTests
```

#### 3. Chạy với Docker Compose
```bash
docker-compose up -d
```

#### 4. Kiểm tra trạng thái
```bash
# Xem logs
docker-compose logs -f

# Kiểm tra containers đang chạy
docker ps
```

### Kiểm tra hoạt động
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8083
- Auth Service: http://localhost:8081
- Client Server: http://localhost:8087
- User Service: http://localhost:8082
- Order Service: http://localhost:8083
- Product Service: http://localhost:8085

### Dừng hệ thống
```bash
docker-compose down

# Xóa volumes (reset database)
docker-compose down -v
```

## APIs

### Auth Service (via API Gateway: http://localhost:8083)

#### Authentication
```bash
# Đăng ký
POST http://localhost:8083/api/v1/auth/register
Content-Type: application/json
{
  "username": "admin",
  "password": "admin123",
  "email": "admin@example.com",
  "role": "ADMIN"
}

# Đăng nhập
POST http://localhost:8083/api/v1/auth/login
Content-Type: application/json
{
  "username": "admin",
  "password": "admin123"
}

# Refresh Token
POST http://localhost:8083/api/v1/auth/refresh
Content-Type: application/json
{
  "refreshToken": "<refresh_token>"
}

# Verify Token
GET http://localhost:8083/api/v1/auth/verify
Authorization: Bearer <access_token>
```

### User Service (via Client Server)
```bash
# Lấy thông tin user (ADMIN or USER)
GET http://localhost:8083/api/v1/users/{id}
Authorization: Bearer <access_token>

# Lấy user với đơn hàng (ADMIN or USER)
GET http://localhost:8083/api/v1/users/{userId}/orders
Authorization: Bearer <access_token>

# Cập nhật user (ADMIN or USER)
PUT http://localhost:8083/api/v1/users/{id}
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "username": "newusername",
  "email": "newemail@example.com"
}

# Xóa user (ADMIN only)
DELETE http://localhost:8083/api/v1/users/{id}
Authorization: Bearer <access_token>
```

### Product Service (via Client Server)
```bash
# Tạo sản phẩm (ADMIN only)
POST http://localhost:8083/api/v1/products/insert
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "name": "Product Name",
  "description": "Description",
  "price": 100000,
  "stock": 50
}

# Lấy chi tiết sản phẩm
GET http://localhost:8083/api/v1/products/{id}
Authorization: Bearer <access_token>

# Cập nhật sản phẩm (ADMIN only)
PUT http://localhost:8083/api/v1/products/{id}
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "name": "Updated Name",
  "price": 120000,
  "stock": 100
}

# Xóa sản phẩm (ADMIN only)
DELETE http://localhost:8083/api/v1/products/{id}
Authorization: Bearer <access_token>

# Tìm kiếm sản phẩm
GET http://localhost:8083/api/v1/products/search?keyword=laptop&page=1&size=10
Authorization: Bearer <access_token>
```

### Order Service (via Client Server)
```bash
# Tạo đơn hàng (USER)
POST http://localhost:8083/api/v1/orders
Authorization: Bearer <access_token>
Content-Type: application/json
{
  "userId": 1
}

# Lấy chi tiết đơn hàng (ADMIN or USER)
GET http://localhost:8083/api/v1/orders/{id}
Authorization: Bearer <access_token>

# Lấy đơn hàng của user (ADMIN or USER)
GET http://localhost:8083/api/v1/orders/user/{userId}
Authorization: Bearer <access_token>

# Thống kê đơn hàng (ADMIN)
GET http://localhost:8083/api/v1/orders/statistics/by-user/{userId}
Authorization: Bearer <access_token>

# Xóa đơn hàng (ADMIN)
DELETE http://localhost:8083/api/v1/orders/{id}
Authorization: Bearer <access_token>
```

## Security

### JWT Authentication
- **Access Token**: Thời gian sống ngắn (15-30 phút)
- **Refresh Token**: Thời gian sống dài (7-30 ngày)
- **Token Header**: `Authorization: Bearer <token>`

### Role-based Authorization
- **ADMIN**: Toàn quyền quản lý (CRUD tất cả)
- **USER**: Quyền hạn giới hạn (xem và tạo đơn hàng của mình)

### Internal API Security
- **API Key Authentication**: Service-to-service communication
- **Header**: `X-Internal-API-Key: <secret-key>`
- **Bảo vệ**: Chỉ cho phép các service nội bộ gọi `/api/internal/**`

### Security Configuration
```yaml
# application.yml hoặc application.properties
internal:
  api:
    key: ${INTERNAL_API_KEY:your-secret-internal-key}
```

## Xử lý lỗi thường gặp

### 1. Lỗi 401 Unauthorized khi service gọi Internal API

**Triệu chứng:**
```
Remote service error: [401] during [GET] to 
[http://user-service/api/internal/users/by-username/xxx]
```

**Nguyên nhân**: 
- FeignClient không có API Key khi gọi Internal API
- Security Filter chặn request không có header `X-Internal-API-Key`

**Giải pháp:**

#### Bước 1: Tạo FeignConfig cho service gọi Internal API
```java
// Ví dụ: auth-service/config/FeignConfig.java
@Configuration
public class FeignConfig {
    @Value("${internal.api.key}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (requestTemplate.path().contains("/api/internal/")) {
                requestTemplate.header("X-Internal-API-Key", internalApiKey);
            }
        };
    }
}
```

#### Bước 2: Thêm FeignConfig vào FeignClient
```java
@FeignClient(name="user-service", 
             path="/api/internal/users", 
             configuration = FeignConfig.class)
public interface UserClient {
    // ...
}
```

#### Bước 3: Tạo InternalApiKeyFilter cho service nhận Internal API
```java
// Ví dụ: user-service/config/InternalApiKeyFilter.java
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {
    @Value("${internal.api.key}")
    private String internalApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        if (request.getRequestURI().startsWith("/api/internal/")) {
            String apiKey = request.getHeader("X-Internal-API-Key");
            
            if (apiKey == null || !apiKey.equals(internalApiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid API Key\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### Bước 4: Thêm Filter vào SecurityConfig
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/internal/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(internalApiKeyFilter, 
                        UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

#### Bước 5: Cấu hình API Key trong application.yml
```yaml
internal:
  api:
    key: ${INTERNAL_API_KEY:my-secret-key-change-in-production}
```

#### Bước 6: Cấu hình Docker Compose
```yaml
services:
  auth-service:
    environment:
      INTERNAL_API_KEY: "my-super-secret-key-2024"
  
  user-service:
    environment:
      INTERNAL_API_KEY: "my-super-secret-key-2024"
  # ... các service khác
```

### 2. Lỗi Connection Refused

**Giải pháp:**
- Kiểm tra Eureka Dashboard: http://localhost:8761
- Đảm bảo tất cả service đã đăng ký
- Kiểm tra network trong Docker Compose

### 3. Lỗi JWT Token Invalid

**Giải pháp:**
- Kiểm tra `jwt.secretKey` giống nhau giữa auth-service và client-server
- Kiểm tra token chưa hết hạn
- Kiểm tra format header: `Authorization: Bearer <token>`

### 4. Database Connection Error

**Giải pháp:**
```bash
# Kiểm tra PostgreSQL đang chạy
docker ps | grep postgres

# Xem logs database
docker-compose logs postgres

# Reset database
docker-compose down -v
docker-compose up -d
```

## Monitoring & Logging

### Eureka Dashboard
- URL: http://localhost:8761
- Xem trạng thái tất cả services
- Kiểm tra health status
- Load balancing info

### Application Logs
```bash
# Xem logs tất cả service
docker-compose logs -f

# Xem logs một service cụ thể
docker-compose logs -f auth-service
docker-compose logs -f user-service
docker-compose logs -f client-server

# Xem logs realtime
docker-compose logs -f --tail=100 auth-service
```

### Health Check Endpoints
- Auth Service: http://localhost:8081/actuator/health
- User Service: http://localhost:8082/actuator/health
- Order Service: http://localhost:8083/actuator/health
- Product Service: http://localhost:8085/actuator/health
- Client Server: http://localhost:8087/actuator/health

## Database Schema

### Databases
Mỗi service có database riêng (Database per Service pattern):

| Service | Database | Port |
|---------|----------|------|
| User Service | userdb | 5432 |
| Order Service | orderdb | 5432 |
| OrderDetail Service | orderdetaildb | 5432 |
| Product Service | productdb | 5432 |
| Auth Service | authdb | 5432 |

### Kết nối Database
```yaml
# docker-compose.yml
postgres:
  image: postgres:15
  environment:
    POSTGRES_USER: admin
    POSTGRES_PASSWORD: admin123
  ports:
    - "5432:5432"
```

## Service Communication Matrix

| From Service | To Service | Type | API Path | Auth Method |
|--------------|------------|------|----------|-------------|
| Auth Service | User Service | Internal | /api/internal/users | API Key |
| Client Server | User Service | Public | /api/v1/users | JWT Token |
| Client Server | Order Service | Public | /api/v1/orders | JWT Token |
| Client Server | Product Service | Public | /api/v1/products | JWT Token |
| Client Server | Auth Service | Public | /api/v1/auth/verify | JWT Token |
| Order Service | User Service | Internal | /api/internal/users | API Key |
| OrderDetail Service | Order Service | Internal | /api/internal/orders | API Key |
| OrderDetail Service | Product Service | Internal | /api/internal/products | API Key |
| User Service | Order Service | Internal | /api/internal/orders | API Key |

## Port Summary

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8083 | Entry point cho client |
| Discovery Server | 8761 | Eureka Server |
| Auth Service | 8081 | Authentication & Authorization |
| User Service | 8082 | User Management |
| Order Service | 8083 | Order Management |
| OrderDetail Service | 8084 | Order Detail Management |
| Product Service | 8085 | Product & Inventory |
| Client Server | 8087 | API Orchestrator |
| PostgreSQL | 5432 | Database |

## Troubleshooting Guide

### Service không đăng ký với Eureka

**Kiểm tra:**
```bash
# 1. Xem logs của service
docker-compose logs -f <service-name>

# 2. Kiểm tra Eureka Dashboard
# Truy cập: http://localhost:8761
# Xem phần "Instances currently registered with Eureka"

# 3. Kiểm tra network
docker network inspect training-microservice_default
```

**Giải pháp:**
- Đảm bảo `eureka.client.service-url.defaultZone` đúng
- Kiểm tra service có cùng Docker network
- Restart service: `docker-compose restart <service-name>`

### FeignClient Timeout

**Giải pháp:** Tăng timeout trong application.yml
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

### Port đã được sử dụng

**Giải pháp:**
```bash
# Kiểm tra port đang sử dụng (Windows)
netstat -ano | findstr :8083

# Kill process (Windows - chạy với quyền admin)
taskkill /PID <PID> /F

# Hoặc thay đổi port trong application.yml
server:
  port: 8084  # thay đổi port
```

### Rebuild sau khi thay đổi code

```bash
# 1. Stop containers
docker-compose down

# 2. Rebuild images
docker-compose build --no-cache

# 3. Start lại
docker-compose up -d

# 4. Xem logs
docker-compose logs -f
```

## Development Guide

### Thêm một service mới

1. **Tạo Spring Boot project** với dependencies:
   - Spring Web
   - Spring Cloud Netflix Eureka Client
   - Spring Cloud OpenFeign
   - PostgreSQL Driver
   - Lombok

2. **Cấu hình application.yml**
```yaml
spring:
  application:
    name: new-service
server:
  port: 8088

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

3. **Thêm @EnableDiscoveryClient** vào main class
```java
@SpringBootApplication
@EnableDiscoveryClient
public class NewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewServiceApplication.class, args);
    }
}
```

4. **Thêm vào docker-compose.yml**
```yaml
new-service:
  build: ./new-service
  ports:
    - "8088:8088"
  environment:
    SPRING_PROFILES_ACTIVE: docker
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://discovery-server:8761/eureka/
  depends_on:
    - discovery-server
    - postgres
```

5. **Cập nhật API Gateway routes** (nếu cần)

## Contributing

### Quy tắc commit
- `feat:` Thêm tính năng mới
- `fix:` Sửa bug
- `refactor:` Refactor code
- `docs:` Cập nhật documentation
- `test:` Thêm/sửa tests
- `chore:` Cập nhật build, dependencies

### Git Workflow
```bash
# 1. Tạo branch mới
git checkout -b feature/ten-tinh-nang

# 2. Commit changes
git add .
git commit -m "feat: thêm API tìm kiếm sản phẩm"

# 3. Push to remote
git push origin feature/ten-tinh-nang

# 4. Tạo Pull Request
```

## Roadmap

### Phase 1 - Current ✅
- [x] Microservices architecture
- [x] JWT Authentication
- [x] Service Discovery (Eureka)
- [x] API Gateway
- [x] Internal API security

### Phase 2 - Planned
- [ ] Redis Cache for performance
- [ ] Message Queue (RabbitMQ/Kafka)
- [ ] Distributed Tracing (Zipkin)
- [ ] Centralized Configuration (Spring Cloud Config)
- [ ] API Rate Limiting

### Phase 3 - Future
- [ ] Kubernetes deployment
- [ ] CI/CD Pipeline
- [ ] Monitoring (Prometheus + Grafana)
- [ ] ELK Stack for logging
- [ ] GraphQL API

## FAQ

### Q: Tại sao cần Client Server?
**A:** Client Server đóng vai trò API Orchestrator, xử lý:
- JWT validation
- Authorization kiểm tra
- Request routing
- Response aggregation

### Q: Khác biệt giữa Public API và Internal API?
**A:**
- **Public API** (`/api/v1/**`): Cho client gọi, yêu cầu JWT token
- **Internal API** (`/api/internal/**`): Cho service gọi nhau, yêu cầu API Key

### Q: Tại sao mỗi service có database riêng?
**A:** Database per Service pattern đảm bảo:
- Loose coupling giữa services
- Độc lập về technology stack
- Dễ dàng scale từng service
- Tránh single point of failure

### Q: Làm sao để scale một service?
**A:**
```bash
# Scale user-service lên 3 instances
docker-compose up -d --scale user-service=3

# Load balancing tự động qua Eureka + Ribbon
```

## License
MIT License

## Contact
- Author: [Your Name]
- Email: [your.email@example.com]
- GitHub: [github.com/yourusername]

---

**Cảm ơn bạn đã sử dụng hệ thống Microservice E-Commerce! 🚀**