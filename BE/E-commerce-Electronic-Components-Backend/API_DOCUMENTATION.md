# E-commerce Electronic Components - Authentication & Authorization API

## Tổng quan

Hệ thống Authentication và Authorization với JWT token, hỗ trợ 2 role: **USER** và **ADMIN**

## 🚀 Truy Cập Swagger UI

Sau khi khởi động ứng dụng, truy cập Swagger UI tại:

```
http://localhost:8080/swagger-ui.html
```

Hoặc:

```
http://localhost:8080/swagger-ui/index.html
```

**OpenAPI JSON:** http://localhost:8080/v3/api-docs

## 📖 Hướng Dẫn Sử Dụng Swagger

### 1. Đăng Ký User Mới

1. Tìm endpoint **POST /auth/register** trong section **Authentication**
2. Click **"Try it out"**
3. Nhập thông tin:
```json
{
  "email": "user@test.com",
  "password": "123456",
  "role": "ROLE_USER"
}
```
4. Click **"Execute"**
5. Xem response với status code **201 Created**

### 2. Đăng Nhập

1. Tìm endpoint **POST /auth/login**
2. Click **"Try it out"**
3. Nhập credentials:
```json
{
  "email": "user@test.com",
  "password": "123456"
}
```
4. Click **"Execute"**
5. **QUAN TRỌNG:** Copy `accessToken` từ response

### 3. Xác Thực (Authorize)

1. Click nút **"Authorize"** 🔓 ở góc trên bên phải Swagger UI
2. Paste access token vào ô **"Value"** (không cần thêm "Bearer ")
3. Click **"Authorize"**
4. Click **"Close"**
5. Biểu tượng sẽ đổi thành 🔒 (đã xác thực)

### 4. Test Protected Endpoints

**User Profile (USER/ADMIN):**
1. Tìm endpoint **GET /api/user/profile**
2. Click **"Try it out"**
3. Click **"Execute"**
4. Xem thông tin user trong response

**Admin Dashboard (chỉ ADMIN):**
1. Đăng nhập với admin account
2. Authorize với admin token
3. Tìm endpoint **GET /api/admin/dashboard**
4. Click **"Try it out"** → **"Execute"**

### 5. Đổi Mật Khẩu

1. Đảm bảo đã Authorize với token hợp lệ
2. Tìm endpoint **POST /auth/change-password**
3. Click **"Try it out"**
4. Nhập:
```json
{
  "currentPassword": "123456",
  "newPassword": "newpass123",
  "confirmPassword": "newpass123"
}
```
5. Click **"Execute"**

### 6. Đăng Xuất

1. Tìm endpoint **POST /auth/logout**
2. Click **"Try it out"**
3. Click **"Execute"**
4. Token sẽ bị blacklist
5. Click nút **"Authorize"** và **"Logout"** để xóa token

## Chức năng

1. ✅ **Register** - Đăng ký tài khoản mới
2. ✅ **Login** - Đăng nhập và nhận JWT token
3. ✅ **Logout** - Đăng xuất và blacklist token
4. ✅ **Change Password** - Đổi mật khẩu (yêu cầu authentication)
5. ✅ **Role-based Authorization** - Phân quyền USER và ADMIN

## API Endpoints

> 💡 **Tip:** Tất cả endpoints dưới đây có thể test trực tiếp trên Swagger UI tại `http://localhost:8080/swagger-ui.html`

### 1. Register (Đăng ký)

**POST** `/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "role": "ROLE_USER"  
}
```

**Response:** `201 Created`
```json
{
  "email": "user@example.com",
  "role": "ROLE_USER",
  "message": "User registered successfully"
}
```

**Swagger:** Tìm trong section "Authentication" → POST /auth/register

---

### 2. Login (Đăng nhập)

**POST** `/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**⚠️ Lưu ý:** 
- Copy `accessToken` để sử dụng cho các request tiếp theo
- Refresh token sẽ được lưu trong HTTP-only cookie với tên `refresh_token`

**Swagger:** Tìm trong section "Authentication" → POST /auth/login

---

### 3. Logout (Đăng xuất)

**POST** `/auth/logout`

**Headers:**
- `Authorization: Bearer {accessToken}`

**Cookies:**
- `refresh_token` (tự động gửi từ browser)

**Response:** `204 No Content`

**Swagger:** 
1. Click **"Authorize"** và nhập access token
2. Tìm POST /auth/logout → Try it out → Execute

---

### 4. Change Password (Đổi mật khẩu)

**POST** `/auth/change-password`

**Headers:**
- `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

**Response:** `200 OK`
```json
{
  "message": "Password changed successfully",
  "email": "user@example.com"
}
```

**Swagger:** Yêu cầu Authorize trước → Tìm POST /auth/change-password

---

### 5. Test Endpoints (Demo phân quyền)

#### 5.1 Public Endpoint (Không cần authentication)

**GET** `/api/public`

**Response:** `200 OK`
```json
{
  "message": "This is a public endpoint - accessible to everyone"
}
```

**Swagger:** Tìm trong section "Test Endpoints" → GET /api/public

#### 5.2 User Profile (Cần role USER hoặc ADMIN)

**GET** `/api/user/profile`

**Headers:**
- `Authorization: Bearer {accessToken}`

**Response:** `200 OK`
```json
{
  "message": "Welcome to user profile",
  "email": "user@example.com",
  "role": "[ROLE_USER]"
}
```

**Swagger:** Yêu cầu Authorize → GET /api/user/profile

#### 5.3 Admin Dashboard (Chỉ cho ADMIN)

**GET** `/api/admin/dashboard`

**Headers:**
- `Authorization: Bearer {accessToken}`

**Response:** `200 OK`
```json
{
  "message": "Welcome to admin dashboard",
  "email": "admin@example.com",
  "role": "[ROLE_ADMIN]"
}
```

**Response nếu không có quyền:** `403 Forbidden`
```json
{
  "error": "Access denied. You don't have permission to access this resource"
}
```

**Swagger:** Yêu cầu Authorize với ADMIN token → GET /api/admin/dashboard

## Roles (Vai trò)

### ROLE_USER
- Có thể truy cập các endpoint dành cho user
- Có thể đổi mật khẩu
- **Endpoints:** `/api/user/**`, `/auth/change-password`

### ROLE_ADMIN
- Có toàn quyền truy cập
- Bao gồm tất cả quyền của USER
- **Endpoints:** `/api/admin/**`, `/api/user/**`, `/auth/change-password`

## Security Configuration

### JWT Token
- **Access Token**: Hết hạn sau 30 phút
- **Refresh Token**: Hết hạn sau 14 ngày
- **Algorithm**: HS512
- **Claims**: subject (email), role, scope, jwtId, issueTime, expirationTime

### Password Encoding
- **Algorithm**: BCrypt
- **Minimum Length**: 6 characters

### Token Storage
- **Access Token**: Gửi trong response body, client lưu trong memory/localStorage
- **Refresh Token**: Lưu trong HTTP-only cookie (bảo mật hơn)

## Error Handling

### 400 Bad Request
```json
{
  "error": "Email already exists"
}
```

### 401 Unauthorized
```json
{
  "error": "Invalid email or password"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied. You don't have permission to access this resource"
}
```

### Validation Errors
```json
{
  "email": "Email must be valid",
  "password": "Password must be at least 6 characters"
}
```

## 💡 Swagger UI Tips & Tricks

### Authorize với JWT Token

1. **Lấy Token:**
   - Login qua endpoint POST /auth/login
   - Copy `accessToken` từ response

2. **Authorize:**
   - Click nút **Authorize** 🔓 (góc trên phải)
   - Paste token vào field "Value"
   - Không cần thêm "Bearer " prefix
   - Click **Authorize** → **Close**

3. **Logout khỏi Swagger:**
   - Click **Authorize** 🔒
   - Click **Logout**

### Test Flow Hoàn Chỉnh trên Swagger

#### Scenario 1: User Registration & Login

1. **POST /auth/register** → Đăng ký user mới
   ```json
   {
     "email": "testuser@test.com",
     "password": "123456",
     "role": "ROLE_USER"
   }
   ```

2. **POST /auth/login** → Đăng nhập
   ```json
   {
     "email": "testuser@test.com",
     "password": "123456"
   }
   ```
   → Copy `accessToken`

3. **Authorize** → Paste token

4. **GET /api/user/profile** → Test user endpoint

5. **GET /api/admin/dashboard** → Should fail (403 Forbidden)

6. **POST /auth/change-password** → Đổi mật khẩu
   ```json
   {
     "currentPassword": "123456",
     "newPassword": "newpass123",
     "confirmPassword": "newpass123"
   }
   ```

7. **POST /auth/logout** → Đăng xuất

#### Scenario 2: Admin Flow

1. **POST /auth/register** → Đăng ký admin
   ```json
   {
     "email": "admin@test.com",
     "password": "123456",
     "role": "ROLE_ADMIN"
   }
   ```

2. **POST /auth/login** → Đăng nhập admin

3. **Authorize** → Paste admin token

4. **GET /api/user/profile** → Success ✅

5. **GET /api/admin/dashboard** → Success ✅

### Swagger Features

- **Schema Models:** Click vào model name để xem cấu trúc data
- **Example Values:** Click "Example Value" để auto-fill request body
- **Response Samples:** Xem example responses cho mỗi status code
- **Curl Commands:** Copy curl command từ response để test bằng terminal

## 🎯 Quick Test với cURL (Alternative)

Nếu muốn test nhanh bằng command line:

### Register
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"123456","role":"ROLE_USER"}'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"123456"}'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Change Password
```bash
curl -X POST http://localhost:8080/auth/change-password \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"currentPassword":"123456","newPassword":"newpass123","confirmPassword":"newpass123"}'
```


## Best Practices

1. **Bảo mật Password**
   - Luôn hash password trước khi lưu vào database
   - Sử dụng BCrypt với salt tự động

2. **JWT Token**
   - Access token có thời gian sống ngắn (30 phút)
   - Refresh token được lưu trong HTTP-only cookie
   - Token bị blacklist khi logout

3. **Role-based Access Control**
   - Sử dụng `@PreAuthorize` annotation
   - Kiểm tra quyền ở cả controller và config level

4. **Error Handling**
   - Trả về message rõ ràng cho client
   - Log chi tiết error ở server side
   - Không expose thông tin nhạy cảm trong error message

## Troubleshooting

### Token expired
- Request token mới bằng cách login lại
- Trong production nên implement refresh token endpoint

### Access denied
- Kiểm tra role của user
- Đảm bảo token chứa đúng role claim

### Authentication failed
- Kiểm tra email và password
- Đảm bảo user đã được tạo trong database
