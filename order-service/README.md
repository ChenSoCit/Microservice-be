# Order Service - Tài liệu Tổng quan

## 📋 Mục lục
1. [Tổng quan](#tổng-quan)
2. [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
3. [Các thành phần đã triển khai](#các-thành-phần-đã-triển-khai)
4. [Flow tạo Order](#flow-tạo-order)
5. [API Endpoints](#api-endpoints)
6. [Cấu trúc Database](#cấu-trúc-database)
7. [Cài đặt và chạy](#cài-đặt-và-chạy)
8. [Testing](#testing)

## 🎯 Tổng quan

Order Service là một microservice trong hệ thống quản lý đơn hàng, chịu trách nhiệm:
- Tạo và quản lý đơn hàng
- Tương tác với User Service để xác thực người dùng
- Tương tác với Product Service để kiểm tra và cập nhật tồn kho
- Lưu trữ thông tin chi tiết đơn hàng và các sản phẩm trong đơn

## 🏗️ Kiến trúc hệ thống

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       v
┌─────────────────┐
│    Gateway      │
└──────┬──────────┘
       │
       v
┌─────────────────┐
│  Auth Service   │ (Xác thực)
└─────────────────┘
       │
       v
┌─────────────────────────────────────────┐
│           Order Service                  │
│  ┌─────────────────────────────────┐   │
│  │ OrderController                  │   │
│  └──────────┬──────────────────────┘   │
│             v                            │
│  ┌─────────────────────────────────┐   │
│  │ OrderServiceImpl                 │   │
│  │  - createOrder()                 │   │
│  │  - getOrderById()                │   │
│  │  - getOrderByUserId()            │   │
│  └──┬───────────────────┬──────────┘   │
│     v                   v                │
│  ┌────────┐      ┌─────────────┐       │
│  │ Mapper │      │   Clients   │       │
│  └────────┘      └─────────────┘       │
└───────────────────┬──────┬──────────────┘
                    │      │
        ┌───────────┘      └──────────┐
        v                              v
┌───────────────┐            ┌─────────────────┐
│ User Service  │            │ Product Service │
└───────────────┘            └─────────────────┘
        │                              │
        v                              v
   (User Info)                  (Product Info,
                                Check Stock,
                                Update Stock)
```

## 📦 Các thành phần đã triển khai

### 1. **Controllers**
- `OrderController.java` - REST API endpoints cho order

### 2. **Services**
- `OrderService.java` - Interface định nghĩa các method
- `OrderServiceImpl.java` - Triển khai logic nghiệp vụ tạo order

### 3. **Models**
- `Order.java` - Entity đơn hàng
- `OrderDetail.java` - Entity chi tiết đơn hàng

### 4. **DTOs**

#### Request DTOs:
- `OrderRequest.java` - Request tạo order
  ```java
  {
    userId: Integer
    fullName: String
    email: String
    phone: String
    address: String
    note: String
    items: List<OrderItemRequest>
  }
  ```

- `OrderItemRequest.java` - Item trong order
  ```java
  {
    productId: Integer
    quantity: Integer
    color: String
  }
  ```

#### Response DTOs:
- `OrderInfoResponse.java` - Thông tin đầy đủ order sau khi tạo
- `OrderItemResponse.java` - Thông tin item trong response
- `OrderResponse.java` - Thông tin order cơ bản
- `OrderDetailResponse.java` - Chi tiết order
- `CntOrderResponse.java` - Thống kê order
- `ApiResponse.java` - Wrapper response chung
- `UserResponse.java` - Thông tin user từ User Service
- `ProductResponse.java` - Thông tin product từ Product Service

### 5. **Mappers (MyBatis)**
- `OrderMapper.java` & `OrderMapper.xml` - Database operations cho Order
- `OrderDetailMapper.java` & `OrderDetailMapper.xml` - Database operations cho OrderDetail

### 6. **Feign Clients**
- `UserClient.java` - Gọi User Service
  - `getUserById(Integer id)` - Lấy thông tin user
  
- `ProductClient.java` - Gọi Product Service
  - `getProductById(Integer id)` - Lấy thông tin sản phẩm
  - `checkStock(Integer id, Integer quantity)` - Kiểm tra tồn kho
  - `updateStock(Integer id, Integer quantity)` - Cập nhật tồn kho

### 7. **Configuration**
- `WebSecurityConfig.java` - Cấu hình security
- `application.properties` - Cấu hình application

## 🔄 Flow tạo Order

### Luồng xử lý chi tiết:

```
1. Client gửi request → Gateway
2. Gateway → Auth Service (xác thực token)
3. Auth Service → trả về thông tin user
4. Gateway forward request → Order Service

5. Order Service nhận request:
   
   Step 1: Xác thực User
   ├─ Gọi UserClient.getUserById(userId)
   ├─ Nếu user không tồn tại → throw exception
   └─ Lưu thông tin user
   
   Step 2: Validate Items
   ├─ Kiểm tra items không rỗng
   └─ Nếu rỗng → throw exception
   
   Step 3: Kiểm tra Products và Stock
   ├─ For each item:
   │  ├─ Gọi ProductClient.getProductById(productId)
   │  ├─ Nếu product không tồn tại → throw exception
   │  ├─ Gọi ProductClient.checkStock(productId, quantity)
   │  ├─ Nếu không đủ hàng → throw exception
   │  ├─ Tính totalMoney += price × quantity
   │  └─ Thêm vào danh sách orderItems
   │
   Step 4: Tạo Order
   ├─ Tạo Order entity
   │  ├─ userId, fullName, email, phone, address
   │  ├─ status = "PENDING"
   │  ├─ totalMoney
   │  ├─ orderDate = now()
   │  └─ note
   ├─ orderMapper.createOrder(order)
   └─ Lấy orderId vừa tạo
   
   Step 5: Tạo Order Details
   ├─ For each item:
   │  ├─ Tạo OrderDetail entity
   │  │  ├─ orderId, productId
   │  │  ├─ price, quantity, totalMoney
   │  │  ├─ color, timestamps
   │  └─ orderDetailMapper.createOrderDetail(orderDetail)
   │
   Step 6: Cập nhật Stock
   ├─ For each item:
   │  ├─ Gọi ProductClient.updateStock(productId, -quantity)
   │  └─ Nếu thất bại → throw exception (rollback)
   │
   Step 7: Trả về Response
   └─ Return OrderInfoResponse
      ├─ Thông tin Order
      ├─ Thông tin User
      └─ Danh sách OrderItems (với thông tin product)

6. Gateway → trả response về Client
```

### Transaction Management
- Toàn bộ quá trình được bao bọc trong `@Transactional`
- Nếu bất kỳ bước nào thất bại → Rollback toàn bộ
- Đảm bảo tính nhất quán dữ liệu

## 🔌 API Endpoints

### 1. Tạo Order
```http
POST /api/v1/orders
Content-Type: application/json

Request Body:
{
  "userId": 1,
  "fullName": "Nguyen Van A",
  "email": "a@example.com",
  "phone": "0123456789",
  "address": "123 ABC Street",
  "note": "Giao ngoai gio hanh chinh",
  "items": [
    {
      "productId": 10,
      "quantity": 2,
      "color": "Red"
    }
  ]
}

Response: 201 Created
{
  "code": 201,
  "message": "Order created successfully",
  "data": { OrderInfoResponse }
}
```

### 2. Lấy Order theo ID
```http
GET /api/v1/orders/{id}

Response: 200 OK
{
  "code": 200,
  "message": "Get order detail by id",
  "data": { OrderDetailResponse }
}
```

### 3. Lấy Orders của User
```http
GET /api/v1/orders/user/{userId}

Response: 200 OK
{
  "code": 200,
  "message": "get order by user id",
  "data": [ OrderResponse... ]
}
```

### 4. Thống kê Orders
```http
GET /api/v1/orders/statistics/by-user/{userId}

Response: 200 OK
{
  "code": 200,
  "message": "thong ke so luong don hang",
  "data": { CntOrderResponse }
}
```

## 🗄️ Cấu trúc Database

### Table: `orders`
| Column | Type | Description |
|--------|------|-------------|
| id | SERIAL PK | ID đơn hàng |
| user_id | INTEGER | ID user |
| full_name | VARCHAR(255) | Tên người nhận |
| email | VARCHAR(255) | Email người nhận |
| phone | VARCHAR(20) | SĐT người nhận |
| address | TEXT | Địa chỉ giao hàng |
| order_date | TIMESTAMP | Ngày tạo đơn |
| status | VARCHAR(50) | Trạng thái (PENDING, CONFIRMED, etc.) |
| total_money | DECIMAL(15,2) | Tổng tiền |
| note | TEXT | Ghi chú |

### Table: `order_details`
| Column | Type | Description |
|--------|------|-------------|
| id | SERIAL PK | ID chi tiết |
| order_id | INTEGER FK | ID đơn hàng |
| product_id | INTEGER | ID sản phẩm |
| price | DECIMAL(15,2) | Giá tại thời điểm mua |
| number_of_products | INTEGER | Số lượng |
| total_money | DECIMAL(15,2) | Tổng tiền item |
| color | VARCHAR(50) | Màu sắc |
| created_at | TIMESTAMP | Thời gian tạo |
| updated_at | TIMESTAMP | Thời gian cập nhật |

## 🚀 Cài đặt và chạy

### Prerequisites
- JDK 17
- Maven 3.6+
- PostgreSQL 12+
- Spring Boot 3.5.5

### 1. Cấu hình Database
```bash
# Tạo database
createdb orderdb

# Chạy script tạo schema
psql -U postgres -d orderdb -f database/schema.sql
```

### 2. Cấu hình application.properties
```properties
server.port=8081
spring.application.name=order-service

# Database
spring.datasource.url=jdbc:postgresql://localhost:5433/orderdb
spring.datasource.username=postgres
spring.datasource.password=123456

# MyBatis
mybatis.type-aliases-package=com.example.order_service.models
mybatis.mapper-locations=classpath*:mapper/**/*.xml

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### 3. Build và Run
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Hoặc
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

## 🧪 Testing

### Unit Test
```bash
mvn test
```

### Integration Test với Postman
- Import file `API_EXAMPLES.md` để có sẵn các test cases
- Chạy các endpoint theo thứ tự:
  1. Tạo order mới
  2. Lấy order theo ID
  3. Lấy danh sách orders của user
  4. Thống kê orders

### Test Flow hoàn chỉnh
1. Đảm bảo các service khác đang chạy:
   - Eureka Server (port 8761)
   - Gateway Service (port 8080)
   - Auth Service
   - User Service
   - Product Service

2. Tạo order:
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d @test-data/create-order-request.json
```

## 📝 Lưu ý quan trọng

### 1. Transaction và Rollback
- Tất cả operations trong `createOrder()` đều nằm trong `@Transactional`
- Nếu bất kỳ bước nào fail → toàn bộ sẽ rollback
- Đảm bảo:
  - Order không được tạo
  - OrderDetail không được tạo
  - Stock không bị trừ

### 2. Error Handling
- User not found → 400 Bad Request
- Product not found → 400 Bad Request
- Out of stock → 400 Bad Request
- Database error → 500 Internal Server Error

### 3. Stock Management
- Stock được kiểm tra trước khi tạo order
- Stock được trừ sau khi order được tạo thành công
- Nếu update stock fail → rollback toàn bộ

### 4. Payment Flow (Chưa triển khai)
- Order được tạo với status `PENDING`
- Payment Service sẽ xử lý thanh toán
- Sau khi thanh toán thành công → update status sang `CONFIRMED`

## 📚 Tài liệu tham khảo

- [ORDER_FLOW.md](ORDER_FLOW.md) - Chi tiết flow tạo order
- [API_EXAMPLES.md](API_EXAMPLES.md) - Ví dụ API requests/responses
- [database/schema.sql](database/schema.sql) - Database schema

## 🔮 Tính năng sẽ phát triển

1. ✅ Tạo order với nhiều sản phẩm
2. ✅ Kiểm tra tồn kho
3. ✅ Cập nhật tồn kho
4. ⏳ Thanh toán (Payment Service)
5. ⏳ Gửi email xác nhận
6. ⏳ Tracking đơn hàng
7. ⏳ Hủy đơn hàng và hoàn trả stock
8. ⏳ Apply voucher/discount

## 👥 Liên hệ

- Developer: Your Name
- Email: your.email@example.com
- GitHub: https://github.com/yourusername/order-service
