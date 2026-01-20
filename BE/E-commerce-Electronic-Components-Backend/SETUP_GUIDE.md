# Hướng Dẫn Chạy Ứng Dụng

## Yêu Cầu Hệ Thống

- **Java**: JDK 17 hoặc cao hơn
- **Maven**: 3.6+ (hoặc sử dụng Maven wrapper đã có sẵn)
- **PostgreSQL**: 12+ 
- **Redis**: 6.0+ (cho blacklist token và refresh token)

## Bước 1: Cài Đặt Database

### PostgreSQL

1. **Cài đặt PostgreSQL** (nếu chưa có)
   - Download từ: https://www.postgresql.org/download/

2. **Tạo Database**
   ```sql
   CREATE DATABASE test_api;
   ```

3. **Cập nhật thông tin kết nối** trong `src/main/resources/application.yaml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/test_api
       username: postgres
       password: 123456  # Thay đổi password của bạn
   ```

4. **(Optional) Insert dữ liệu mẫu**
   - Kết nối vào PostgreSQL
   - Chạy script trong file `sql.sql`
   ```bash
   psql -U postgres -d test_api -f sql.sql
   ```

### Redis

1. **Cài đặt Redis**
   
   **Windows:**
   - Download từ: https://github.com/microsoftarchive/redis/releases
   - Hoặc sử dụng Docker:
     ```bash
     docker run -d -p 6379:6379 --name redis redis:latest
     ```

   **Linux/Mac:**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install redis-server
   
   # MacOS
   brew install redis
   ```

2. **Khởi động Redis**
   ```bash
   # Windows (nếu cài từ installer)
   redis-server
   
   # Linux/Mac
   sudo service redis-server start
   # hoặc
   redis-server
   ```

3. **Kiểm tra Redis đang chạy**
   ```bash
   redis-cli ping
   # Kết quả: PONG
   ```

## Bước 2: Cấu Hình Ứng Dụng

Kiểm tra file `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: E-commerce-Electronic-Components-Backend

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/test_api
    username: postgres
    password: 123456  # ⚠️ Thay đổi theo password của bạn
  
  data:
    redis:
      host: localhost
      port: 6379
  
  jpa:
    hibernate:
      ddl-auto: update  # Tự động tạo/update bảng
    show-sql: true      # (Optional) Hiển thị SQL queries

jwt:
  secret-key: 6af834a092761d5d394cf5a1feac20b4e025ce29b09fe355a3b40971b0860095
  # ⚠️ Trong production, nên dùng environment variable
```

## Bước 3: Build Ứng Dụng

### Sử dụng Maven Wrapper (Khuyến nghị)

**Windows:**
```powershell
./mvnw clean install
```

**Linux/Mac:**
```bash
./mvnw clean install
```

### Sử dụng Maven đã cài đặt

```bash
mvn clean install
```

## Bước 4: Chạy Ứng Dụng

### Phương pháp 1: Sử dụng Maven

**Windows:**
```powershell
./mvnw spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Phương pháp 2: Chạy JAR file

```bash
java -jar target/E-commerce-Electronic-Components-Backend-0.0.1-SNAPSHOT.jar
```

### Phương pháp 3: Chạy từ IDE (IntelliJ IDEA/Eclipse)

1. Mở project trong IDE
2. Tìm file `ECommerceElectronicComponentsBackendApplication.java`
3. Right-click → Run

## Bước 5: Kiểm Tra Ứng Dụng

### 1. Kiểm tra ứng dụng đã chạy

Mở browser và truy cập:
```
http://localhost:8080
```

### 2. Xem Swagger API Documentation

```
http://localhost:8080/swagger-ui.html
```

### 3. Test API với Postman

1. Import file `Postman_Collection.json` vào Postman
2. Test các endpoint theo thứ tự:
   - Register User
   - Login
   - Test endpoints với token

### 4. Test nhanh bằng curl

**Public Endpoint:**
```bash
curl http://localhost:8080/api/public
```

**Register:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"123456\",\"role\":\"ROLE_USER\"}"
```

**Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"123456\"}"
```

## Xử Lý Lỗi Thường Gặp

### 1. Lỗi kết nối PostgreSQL

**Lỗi:** `PSQLException: Connection refused`

**Giải pháp:**
- Kiểm tra PostgreSQL đang chạy
- Kiểm tra port 5432 không bị chiếm
- Kiểm tra username/password trong `application.yaml`

```bash
# Kiểm tra PostgreSQL
sudo service postgresql status

# Restart PostgreSQL
sudo service postgresql restart
```

### 2. Lỗi kết nối Redis

**Lỗi:** `RedisConnectionException: Unable to connect to Redis`

**Giải pháp:**
- Kiểm tra Redis đang chạy
- Kiểm tra port 6379 không bị chiếm

```bash
# Kiểm tra Redis
redis-cli ping

# Restart Redis
sudo service redis-server restart
```

### 3. Lỗi Port đã được sử dụng

**Lỗi:** `Port 8080 is already in use`

**Giải pháp:**
- Thay đổi port trong `application.yaml`:
```yaml
server:
  port: 8081  # Hoặc port khác
```

### 4. Lỗi Circular Dependency

**Lỗi:** `The dependencies of some of the beans in the application context form a cycle`

**Giải pháp:** Đã được fix trong code hiện tại, nhưng nếu vẫn gặp:
```yaml
spring:
  main:
    allow-circular-references: true
```

## Testing

### Chạy Unit Tests

```bash
./mvnw test
```

### Test Coverage

```bash
./mvnw clean test jacoco:report
```

## Production Deployment

### 1. Tạo Production Build

```bash
./mvnw clean package -DskipTests
```

### 2. Chạy với Production Profile

Tạo file `application-prod.yaml`:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
  
  jpa:
    hibernate:
      ddl-auto: validate  # ⚠️ Không auto update trong production
    show-sql: false

jwt:
  secret-key: ${JWT_SECRET_KEY}

server:
  port: ${PORT:8080}
```

Chạy với production profile:
```bash
java -jar target/E-commerce-Electronic-Components-Backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 3. Environment Variables

```bash
export DATABASE_URL=jdbc:postgresql://your-prod-db:5432/prod_db
export DATABASE_USERNAME=prod_user
export DATABASE_PASSWORD=prod_password
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export JWT_SECRET_KEY=your-super-secret-key-here
```

## Monitoring & Logging

### Xem Logs

```bash
# Realtime logs
tail -f logs/spring.log

# Grep specific logs
grep "ERROR" logs/spring.log
```

### Health Check

```
http://localhost:8080/actuator/health
```

## Tài Liệu Tham Khảo

- [API Documentation](API_DOCUMENTATION.md) - Chi tiết các API endpoints
- [Postman Collection](Postman_Collection.json) - Import vào Postman để test
- [Sample Data](sample-data.sql) - Dữ liệu mẫu cho database

## Contact & Support

Nếu gặp vấn đề, hãy kiểm tra:
1. Logs của ứng dụng
2. PostgreSQL logs
3. Redis logs
4. Console output

Happy Coding! 🚀
