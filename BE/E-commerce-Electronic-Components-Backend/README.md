# E-commerce Electronic Components - Backend

> Backend API cho hệ thống thương mại điện tử bán linh kiện điện tử

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-6.0+-red.svg)](https://redis.io/)

## 📋 Mục Lục

- [Tổng Quan](#tổng-quan)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cài Đặt và Chạy](#cài-đặt-và-chạy)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [API Documentation](#api-documentation)
- [Tính Năng](#tính-năng)
- [Docker Setup](#docker-setup)
- [Testing](#testing)
- [Tài Liệu Tham Khảo](#tài-liệu-tham-khảo)

## 🎯 Tổng Quan

Backend API RESTful được xây dựng với Spring Boot, cung cấp các chức năng quản lý sản phẩm, đơn hàng, người dùng và xác thực cho hệ thống thương mại điện tử linh kiện điện tử.

### Tính Năng Chính

- 🔐 **Authentication & Authorization** với JWT
- 👤 **User Management** (User & Admin roles)
- 🛍️ **Product Management** với filter và tìm kiếm nâng cao
- 🛒 **Shopping Cart** 
- 📦 **Order Management**
- 💳 **Payment Integration**
- 📊 **Admin Dashboard**
- 🔄 **Refresh Token** với Redis
- 🚫 **Token Blacklisting**

## 🛠️ Công Nghệ Sử Dụng

### Core Technologies

- **Spring Boot 4.0.1** - Application framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database ORM
- **PostgreSQL** - Primary database
- **Redis** - Token storage & caching
- **JWT (JJWT 0.11.5)** - Token-based authentication

### Additional Libraries

- **Lombok** - Reduce boilerplate code
- **SpringDoc OpenAPI (Swagger)** - API documentation
- **Maven** - Dependency management
- **H2 Database** - Testing database

## 📋 Yêu Cầu Hệ Thống

- **Java JDK**: 17 hoặc cao hơn
- **Maven**: 3.6+ (hoặc sử dụng Maven wrapper)
- **PostgreSQL**: 12+
- **Redis**: 6.0+
- **RAM**: Tối thiểu 2GB
- **Disk**: Tối thiểu 500MB

## 🚀 Cài Đặt và Chạy

### Phương Pháp 1: Chạy Trực Tiếp (Khuyến Nghị cho Development)

#### 1. Clone Repository

```bash
git clone <repository-url>
cd E-commerce-Electronic-Components/BE/E-commerce-Electronic-Components-Backend
```

#### 2. Cài Đặt PostgreSQL

**Tạo Database:**
```sql
CREATE DATABASE databse_name;
```

**Import dữ liệu mẫu (Optional):**
```bash
psql -U postgres -d test_api -f database/sql.sql
```

#### 3. Cài Đặt Redis

**Windows:**
```bash
# Sử dụng Docker (khuyến nghị)
docker run -d -p 6379:6379 --name redis redis:latest

# Hoặc download từ: https://github.com/microsoftarchive/redis/releases
```

**Linux/Mac:**
```bash
# Ubuntu/Debian
sudo apt-get install redis-server

# MacOS
brew install redis
```

**Khởi động Redis:**
```bash
redis-server
```

**Kiểm tra Redis:**
```bash
redis-cli ping
# Output: PONG
```

#### 4. Cấu Hình Application

Cập nhật `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/databse_name
    username: postgres
    password: YOUR_PASSWORD  # Thay đổi theo password của bạn
  
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret-key: YOUR_SECRET_KEY  # Thay đổi trong production
  access-token-expiration: 3600000  # 1 hour
  refresh-token-expiration: 604800000  # 7 days
```

#### 5. Build và Chạy

**Sử dụng Maven Wrapper (Windows):**
```bash
.\mvnw clean install
.\mvnw spring-boot:run
```

**Sử dụng Maven Wrapper (Linux/Mac):**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

**Sử dụng Maven đã cài đặt:**
```bash
mvn clean install
mvn spring-boot:run
```

#### 6. Kiểm Tra

Truy cập: http://localhost:8080

API Documentation (Swagger): http://localhost:8080/swagger-ui/index.html

### Phương Pháp 2: Sử Dụng Docker Compose (Khuyến Nghị cho Production)

#### 1. Khởi Động Services

```bash
# Khởi động PostgreSQL và Redis
docker compose up -d

# Xem logs
docker compose logs -f
```

#### 2. Chạy Application

```bash
.\mvnw spring-boot:run
```

**Hoặc build và chạy với Docker:**
```bash
# Build jar file
.\mvnw clean package -DskipTests

# Run với Docker
docker build -t ecommerce-backend .
docker run -p 8080:8080 ecommerce-backend
```

#### 3. Dừng Services

```bash
# Dừng và giữ dữ liệu
docker compose stop

# Dừng và xóa containers
docker compose down

# Dừng và xóa cả volumes
docker compose down -v
```

## 📁 Cấu Trúc Dự Án

```
E-commerce-Electronic-Components-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/ecommerceelectroniccomponentsbackend/
│   │   │   ├── config/              # Spring Configuration (Security, Redis, etc.)
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # JPA Entities
│   │   │   ├── exception/           # Custom Exceptions & Handlers
│   │   │   ├── mapper/              # DTO ↔ Entity Mappers
│   │   │   ├── model/               # Request/Response Models
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   ├── service/             # Business Logic
│   │   │   ├── util/                # Utility Classes (JWT, etc.)
│   │   │   └── ECommerceElectronicComponentsBackendApplication.java
│   │   └── resources/
│   │       ├── application.yaml     # Main configuration
│   │       └── application-*.yaml   # Profile-specific configs
│   └── test/                        # Unit & Integration Tests
├── database/
│   └── sql.sql                      # Database schema & sample data
├── uploads/                         # File upload directory
├── target/                          # Build output
├── docker-compose.yaml              # Docker Compose configuration
├── pom.xml                          # Maven dependencies
├── API_DOCUMENTATION.md             # Chi tiết API endpoints
├── SETUP_GUIDE.md                   # Hướng dẫn cài đặt chi tiết
├── DOCKER_GUIDE.md                  # Hướng dẫn Docker
├── SIMPLIFIED_FILTER_FEATURE.md     # Tài liệu tính năng filter
└── README.md                        # File này
```

## 📖 API Documentation

### Swagger UI

Sau khi khởi động ứng dụng, truy cập Swagger UI tại:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Các API Endpoints Chính

#### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Đăng ký user mới | ❌ |
| POST | `/auth/login` | Đăng nhập | ❌ |
| POST | `/auth/logout` | Đăng xuất | ✅ |
| POST | `/auth/refresh-token` | Làm mới access token | ✅ |
| POST | `/auth/change-password` | Đổi mật khẩu | ✅ |

#### User Management

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/user/profile` | Xem profile | USER/ADMIN |
| PUT | `/api/user/profile` | Cập nhật profile | USER/ADMIN |

#### Admin

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/admin/dashboard` | Dashboard admin | ADMIN |
| GET | `/api/admin/users` | Danh sách users | ADMIN |

#### Products

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/products` | Danh sách sản phẩm (có filter) | Public |
| GET | `/api/products/{id}` | Chi tiết sản phẩm | Public |
| POST | `/api/products` | Tạo sản phẩm mới | ADMIN |
| PUT | `/api/products/{id}` | Cập nhật sản phẩm | ADMIN |
| DELETE | `/api/products/{id}` | Xóa sản phẩm | ADMIN |

#### Orders

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/orders` | Tạo đơn hàng | USER/ADMIN |
| GET | `/api/orders` | Danh sách đơn hàng | USER/ADMIN |
| GET | `/api/orders/{id}` | Chi tiết đơn hàng | USER/ADMIN |

#### Cart

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/cart` | Xem giỏ hàng | USER/ADMIN |
| POST | `/api/cart/items` | Thêm sản phẩm | USER/ADMIN |
| PUT | `/api/cart/items/{id}` | Cập nhật số lượng | USER/ADMIN |
| DELETE | `/api/cart/items/{id}` | Xóa sản phẩm | USER/ADMIN |

### Authentication Flow

1. **Đăng ký**: `POST /auth/register`
2. **Đăng nhập**: `POST /auth/login` → Nhận `accessToken` và `refreshToken`
3. **Sử dụng API**: Thêm header `Authorization: Bearer {accessToken}`
4. **Refresh Token**: Khi accessToken hết hạn → `POST /auth/refresh-token`
5. **Đăng xuất**: `POST /auth/logout` → Token bị blacklist

### Example Request

```bash
# Đăng nhập
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "123456"
  }'

# Sử dụng API với token
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer {your-access-token}"
```

**Xem chi tiết**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## ✨ Tính Năng

### 1. Authentication & Authorization
- JWT-based authentication
- Role-based access control (USER, ADMIN)
- Token refresh mechanism
- Token blacklisting với Redis
- Password encryption với BCrypt

### 2. User Management
- User registration & login
- Profile management
- Password change
- Address management

### 3. Product Management
- CRUD operations cho products
- Advanced filtering & search
- Category management
- Image upload
- Stock management

### 4. Shopping Cart
- Add/update/remove items
- Persistent cart
- Price calculation

### 5. Order Management
- Order creation & tracking
- Order status management
- Order history
- Payment integration

### 6. Admin Features
- Dashboard statistics
- User management
- Product management
- Order management

## 🐳 Docker Setup

### Quick Start

```bash
# Khởi động PostgreSQL và Redis
docker compose up -d

# Kiểm tra trạng thái
docker compose ps

# Xem logs
docker compose logs -f
```

### Services

**PostgreSQL:**
- Port: 5432
- Database: test_api
- Username: postgres
- Password: 123456

**Redis:**
- Port: 6379

**Xem chi tiết**: [DOCKER_GUIDE.md](./DOCKER_GUIDE.md)

## 🧪 Testing

### Chạy Tests

```bash
# Chạy tất cả tests
.\mvnw test

# Chạy tests với coverage
.\mvnw test jacoco:report

# Chạy specific test class
.\mvnw test -Dtest=UserServiceTest
```

### Test Output

Kết quả test được lưu trong: `target/surefire-reports/`

Coverage report: `target/site/jacoco/index.html`

## 🔒 Security

### Best Practices

- ✅ JWT tokens với expiration
- ✅ Password hashing với BCrypt
- ✅ Token blacklisting
- ✅ CORS configuration
- ✅ Input validation
- ✅ SQL injection prevention (JPA)
- ✅ XSS protection

## 📊 Database Schema

### Main Tables

- `users` - User accounts
- `roles` - User roles
- `products` - Product catalog
- `categories` - Product categories
- `orders` - Customer orders
- `order_items` - Order details
- `cart` - Shopping cart
- `cart_items` - Cart details
- `addresses` - User addresses
- `payments` - Payment records

**Import schema**: `database/sql.sql`

## 🐛 Troubleshooting

### Common Issues

**1. Port 8080 already in use**
```bash
# Tìm process sử dụng port 8080
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <PID> /F
```

**2. PostgreSQL connection refused**
- Kiểm tra PostgreSQL đang chạy
- Kiểm tra username/password trong `application.yaml`
- Kiểm tra database `test_api` đã được tạo

**3. Redis connection failed**
```bash
# Kiểm tra Redis đang chạy
redis-cli ping

# Khởi động Redis
redis-server
```

**4. Build failed**
```bash
# Clean và rebuild
.\mvnw clean install -U
```

## 📚 Tài Liệu Tham Khảo

### Tài Liệu Chi Tiết

- [SETUP_GUIDE.md](./SETUP_GUIDE.md) - Hướng dẫn cài đặt chi tiết
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Chi tiết API endpoints
- [DOCKER_GUIDE.md](./DOCKER_GUIDE.md) - Hướng dẫn Docker
- [SIMPLIFIED_FILTER_FEATURE.md](./SIMPLIFIED_FILTER_FEATURE.md) - Tính năng filter

### External Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)
- [JWT.io](https://jwt.io/)

