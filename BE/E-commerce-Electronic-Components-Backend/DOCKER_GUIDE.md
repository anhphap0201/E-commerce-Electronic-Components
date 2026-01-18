# Docker Setup Guide

## Quick Start với Docker Compose

### Khởi động Services

```bash
# Khởi động PostgreSQL và Redis
docker compose up -d

# Kiểm tra trạng thái
docker compose ps

# Xem logs
docker compose logs -f
```

### Dừng Services

```bash
# Dừng và giữ dữ liệu
docker compose stop

# Dừng và xóa containers
docker compose down

# Dừng và xóa cả volumes (mất hết dữ liệu)
docker compose down -v
```

## Chi Tiết Services

### PostgreSQL

**Image:** postgres:15-alpine
**Port:** 5432
**Database:** test_api
**Username:** postgres
**Password:** 123456

**Kết nối từ host:**
```bash
psql -h localhost -p 5432 -U postgres -d test_api
```

**Kết nối từ container:**
```bash
docker exec -it ecommerce-postgres psql -U postgres -d test_api
```

**Xem logs:**
```bash
docker compose logs postgres
```

**Import sample data:**
```bash
# Data được tự động import từ sample-data.sql khi container khởi động lần đầu
# Nếu cần import lại:
docker exec -i ecommerce-postgres psql -U postgres -d test_api < sample-data.sql
```

### Redis

**Image:** redis:8.0-alpine
**Port:** 6379

**Kết nối từ host:**
```bash
redis-cli -h localhost -p 6379
```

**Kết nối từ container:**
```bash
docker exec -it ecommerce-redis redis-cli
```

**Test kết nối:**
```bash
docker exec -it ecommerce-redis redis-cli ping
# Kết quả: PONG
```

**Xem data:**
```bash
docker exec -it ecommerce-redis redis-cli
> KEYS *
> GET refresh_token:{jwt_id}
> GET blacklisted_token:{jwt_id}
```

**Xóa toàn bộ data:**
```bash
docker exec -it ecommerce-redis redis-cli FLUSHALL
```

## Troubleshooting

### Port đã được sử dụng

**Lỗi:** `Port 5432/6379 is already in use`

**Giải pháp:**
1. Dừng service đang chạy trên port đó:
```bash
# Windows
netstat -ano | findstr :5432
taskkill /PID <PID> /F

# Linux/Mac
sudo lsof -i :5432
sudo kill -9 <PID>
```

2. Hoặc thay đổi port trong docker-compose.yaml:
```yaml
services:
  postgres:
    ports:
      - "5433:5432"  # Thay đổi port host
```

### Container không khởi động

**Kiểm tra logs:**
```bash
docker compose logs postgres
docker compose logs redis
```

**Xóa và tạo lại:**
```bash
docker compose down -v
docker compose up -d
```

### Mất dữ liệu sau khi restart

**Nguyên nhân:** Volume đã bị xóa

**Giải pháp:** Sử dụng `docker compose stop` thay vì `docker compose down -v`

### Reset Database

```bash
# Xóa volume
docker compose down -v

# Khởi động lại (sẽ tạo DB mới và import sample-data.sql)
docker compose up -d
```

## Backup & Restore

### Backup PostgreSQL

```bash
# Backup database
docker exec ecommerce-postgres pg_dump -U postgres test_api > backup.sql

# Backup với timestamp
docker exec ecommerce-postgres pg_dump -U postgres test_api > backup_$(date +%Y%m%d_%H%M%S).sql
```

### Restore PostgreSQL

```bash
# Restore từ backup
docker exec -i ecommerce-postgres psql -U postgres -d test_api < backup.sql
```

### Backup Redis

```bash
# Trigger save
docker exec ecommerce-redis redis-cli SAVE

# Copy dump file
docker cp ecommerce-redis:/data/dump.rdb ./redis-backup.rdb
```

### Restore Redis

```bash
# Stop Redis
docker compose stop redis

# Copy backup file
docker cp redis-backup.rdb ecommerce-redis:/data/dump.rdb

# Start Redis
docker compose start redis
```

## Production Considerations

### Security

1. **Thay đổi passwords:**
```yaml
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

2. **Không expose ports ra ngoài:**
```yaml
# Comment out ports nếu chỉ app trong Docker network cần access
# ports:
#   - "5432:5432"
```

3. **Sử dụng secrets:**
```yaml
secrets:
  postgres_password:
    file: ./secrets/postgres_password.txt
```

### Performance

1. **Tăng memory cho PostgreSQL:**
```yaml
services:
  postgres:
    deploy:
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M
```

2. **Cấu hình Redis maxmemory:**
```yaml
services:
  redis:
    command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
```

### Monitoring

**Thêm health checks vào docker-compose.yaml** (đã có sẵn)

**Kiểm tra health:**
```bash
docker compose ps
```

**Xem resource usage:**
```bash
docker stats ecommerce-postgres ecommerce-redis
```

## Advanced Commands

### Execute SQL directly

```bash
docker exec ecommerce-postgres psql -U postgres -d test_api -c "SELECT * FROM users;"
```

### Create new database

```bash
docker exec ecommerce-postgres createdb -U postgres new_database
```

### List all databases

```bash
docker exec ecommerce-postgres psql -U postgres -c "\l"
```

### Redis monitor

```bash
docker exec -it ecommerce-redis redis-cli MONITOR
```

### Redis info

```bash
docker exec ecommerce-redis redis-cli INFO
```

## Integration với Spring Boot

Application sẽ tự động kết nối với các services khi chạy trên host machine:

```yaml
# application.yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/test_api
  data:
    redis:
      host: localhost
```

Nếu chạy Spring Boot trong Docker, cần thay đổi host:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/test_api
  data:
    redis:
      host: redis
```

## Useful Docker Commands

```bash
# Xem tất cả containers
docker ps -a

# Xem volumes
docker volume ls

# Xem networks
docker network ls

# Xóa unused volumes
docker volume prune

# Xóa unused images
docker image prune

# Xem logs realtime
docker compose logs -f --tail=100

# Restart một service
docker compose restart postgres

# Rebuild và restart
docker compose up -d --build
```
