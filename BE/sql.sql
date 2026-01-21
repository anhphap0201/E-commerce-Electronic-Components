DROP TABLE IF EXISTS user_notifications CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS articles CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS product_variants CASCADE;
DROP TABLE IF EXISTS product_categories CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255), -- bcrypt hash
    full_name VARCHAR(255),
    phone VARCHAR(20),
    gender VARCHAR(10),
    birth_date DATE,
    role VARCHAR(50) DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    image_url TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    short_description TEXT,
    description TEXT,
    avg_rating DOUBLE PRECISION DEFAULT 0,
    sold_quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_categories (
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, category_id)
);

CREATE TABLE product_variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    variant_name VARCHAR(255),
    price NUMERIC(12,2) NOT NULL,
    discount_price NUMERIC(12,2),
    in_stock INT DEFAULT 0 CHECK (in_stock >= 0),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT REFERENCES carts(id) ON DELETE CASCADE,
    product_variant_id BIGINT REFERENCES product_variants(id) ON DELETE CASCADE,
    quantity INT NOT NULL CHECK (quantity > 0)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    total_cost NUMERIC(12,2) NOT NULL,
    shipping_cost NUMERIC(12,2),
    payment_method VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
    product_variant_id BIGINT REFERENCES product_variants(id) ON DELETE SET NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL
);

CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

CREATE TABLE articles (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    title VARCHAR(255),
    slug VARCHAR(255) UNIQUE,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_notifications (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    notification_id BIGINT REFERENCES notifications(id) ON DELETE CASCADE,
    is_read BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, notification_id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_reviews_product ON reviews(product_id);

-- =====================================================
-- DỮ LIỆU MẪU (SAMPLE DATA)
-- =====================================================

-- Người dùng mẫu (password: admin123 và user123)
INSERT INTO users (email, password, full_name, phone, gender, role, is_active) VALUES
('he@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', '0901234567', 'Male', 'ROLE_ADMIN', TRUE),
('user@gmail.com', '$2a$10$DOwnh2KYn6L9LPSt1mGULe5VYU6L4W1W1QnJZ/6nT9LxRr6mYY.Ym', 'Nguyễn Văn A', '0987654321', 'Male', 'ROLE_USER', TRUE),
('maria@gmail.com', '$2a$10$DOwnh2KYn6L9LPSt1mGULe5VYU6L4W1W1QnJZ/6nT9LxRr6mYY.Ym', 'Trần Thị B', '0912345678', 'Female', 'ROLE_USER', TRUE);

-- Danh mục sản phẩm
INSERT INTO categories (name, slug, image_url, description) VALUES
('Arduino & Modules', 'arduino-modules', '/images/ArduinoUnoR3.jpg', 'Bo mạch Arduino và các module mở rộng'),
('Raspberry Pi', 'raspberry-pi', '/images/raspberry-pi-5-6.jpg', 'Máy tính nhúng Raspberry Pi và phụ kiện'),
('Linh kiện điện tử', 'linh-kien-dien-tu', '/images/resistores.jpg', 'Điện trở, tụ điện, transistor, IC và các linh kiện cơ bản'),
('Cảm biến', 'cam-bien', '/images/cam-bien-vat-can-hong-ngoai-fm52-5.jpg', 'Các loại cảm biến nhiệt độ, độ ẩm, khoảng cách, ánh sáng'),
('Module nguồn', 'module-nguon', '/images/pin-18650__T7hrNFf7Uz.webp', 'Module nguồn, sạc pin, Buck/Boost converter'),
('Module truyền thông', 'module-truyen-thong', '/images/nodemcu-esp32-01.webp', 'WiFi, Bluetooth, LoRa, RF, GSM modules'),
('Màn hình hiển thị', 'man-hinh-hien-thi', '/images/man-hinh-lcd-oled-0-96-inch-giao-tiep-i2c-white-9w56-1.jpg', 'LCD, OLED, LED Matrix, TFT displays'),
('Motor & Driver', 'motor-driver', '/images/dong-co-servo-sg90-180-do-rk7a-1.jpg', 'Động cơ và mạch điều khiển động cơ');

-- Sản phẩm Arduino & Modules
INSERT INTO products (name, slug, short_description, description, avg_rating, sold_quantity) VALUES
('Arduino Uno R3', 'arduino-uno-r3', 'Bo mạch Arduino Uno phiên bản R3 chính hãng', 
'Arduino Uno R3 là bo mạch phát triển phổ biến nhất với chip ATmega328P, 14 digital I/O pins, 6 analog inputs, USB connection. Phù hợp cho người mới bắt đầu học lập trình nhúng.', 4.8, 1250),

('Arduino Nano V3', 'arduino-nano-v3', 'Bo mạch Arduino Nano nhỏ gọn với chip CH340', 
'Arduino Nano V3 sử dụng chip ATmega328P, kích thước nhỏ gọn phù hợp cho các dự án có không gian hạn chế. Tương thích hoàn toàn với Arduino IDE.', 4.7, 980),

('Arduino Mega 2560', 'arduino-mega-2560', 'Bo mạch Arduino Mega với 54 digital I/O', 
'Arduino Mega 2560 dựa trên chip ATmega2560, có 54 digital I/O pins, 16 analog inputs, 4 UART. Lý tưởng cho các dự án phức tạp cần nhiều chân I/O.', 4.9, 450),

('ESP32 DevKit V1', 'esp32-devkit-v1', 'Module WiFi + Bluetooth dual-core 240MHz', 
'ESP32 là vi điều khiển mạnh mẽ với WiFi và Bluetooth tích hợp, dual-core 240MHz, nhiều GPIO, ADC, DAC, SPI, I2C. Hoàn hảo cho IoT projects.', 4.9, 2100),

('ESP8266 NodeMCU', 'esp8266-nodemcu', 'Module WiFi ESP8266 với cổng USB', 
'NodeMCU ESP8266 là board phát triển WiFi giá rẻ, dễ lập trình với Arduino IDE hoặc Lua. Phù hợp cho các dự án IoT đơn giản.', 4.6, 1580);

-- Sản phẩm Raspberry Pi
INSERT INTO products (name, slug, short_description, description, avg_rating, sold_quantity) VALUES
('Raspberry Pi 4 Model B 4GB', 'raspberry-pi-4-4gb', 'Máy tính nhúng Raspberry Pi 4 RAM 4GB', 
'Raspberry Pi 4 Model B với CPU Quad-core ARM Cortex-A72 1.5GHz, 4GB RAM, Dual 4K display, Gigabit Ethernet, USB 3.0, WiFi 5, Bluetooth 5.0.', 4.9, 850),

('Raspberry Pi 3 Model B+', 'raspberry-pi-3-b-plus', 'Raspberry Pi 3 B+ với WiFi dual-band', 
'Raspberry Pi 3 Model B+ với CPU Quad-core 1.4GHz, 1GB RAM, WiFi dual-band, Bluetooth 4.2, Gigabit Ethernet. Phù hợp cho các dự án vừa và nhỏ.', 4.8, 620),

('Raspberry Pi Zero W', 'raspberry-pi-zero-w', 'Raspberry Pi Zero siêu nhỏ gọn có WiFi', 
'Raspberry Pi Zero W kích thước cực nhỏ với WiFi và Bluetooth tích hợp. CPU 1GHz single-core, 512MB RAM. Lý tưởng cho các dự án yêu cầu kích thước nhỏ.', 4.7, 340);

-- Sản phẩm Cảm biến
INSERT INTO products (name, slug, short_description, description, avg_rating, sold_quantity) VALUES
('DHT22 - Cảm biến nhiệt độ và độ ẩm', 'dht22-sensor', 'Cảm biến nhiệt độ và độ ẩm độ chính xác cao', 
'DHT22 đo nhiệt độ từ -40 đến 80°C (±0.5°C) và độ ẩm 0-100% RH (±2-5%). Giao tiếp digital đơn giản, phù hợp cho weather station, smart home.', 4.7, 1850),

('HC-SR04 - Cảm biến siêu âm', 'hc-sr04-ultrasonic', 'Module đo khoảng cách siêu âm 2-400cm', 
'HC-SR04 sử dụng sóng siêu âm để đo khoảng cách từ 2cm đến 400cm với độ chính xác 3mm. Hoạt động ở tần số 40KHz, điện áp 5V.', 4.6, 2230),

('BMP280 - Cảm biến áp suất và nhiệt độ', 'bmp280-pressure', 'Cảm biến khí áp và nhiệt độ I2C/SPI', 
'BMP280 đo áp suất khí quyển 300-1100 hPa và nhiệt độ -40 đến +85°C. Giao tiếp I2C hoặc SPI, tiêu thụ điện năng cực thấp.', 4.8, 670),

('MPU6050 - Cảm biến gia tốc và con quay hồi chuyển', 'mpu6050-imu', 'Module IMU 6DOF gyroscope + accelerometer', 
'MPU6050 tích hợp 3-axis gyroscope và 3-axis accelerometer, giao tiếp I2C. Sử dụng trong drone, robot tự cân bằng, VR headset.', 4.8, 1120),

('MQ-2 - Cảm biến khí gas', 'mq2-gas-sensor', 'Cảm biến phát hiện khí LPG, propane, methane', 
'MQ-2 phát hiện các loại khí dễ cháy như LPG, propane, methane, hydrogen, alcohol. Đầu ra analog và digital, thời gian đáp ứng nhanh.', 4.5, 890);

-- Sản phẩm Module nguồn
INSERT INTO products (name, slug, short_description, description, avg_rating, sold_quantity) VALUES
('Module Buck LM2596 DC-DC', 'lm2596-buck-converter', 'Module hạ áp 3A điều chỉnh được', 
'LM2596 step-down converter với dòng 3A, điện áp đầu vào 4-40V, đầu ra điều chỉnh 1.25-37V. Hiệu suất lên đến 92%, có LED hiển thị.', 4.7, 1560),

('Module Boost MT3608', 'mt3608-boost-converter', 'Module tăng áp 2A DC-DC', 
'MT3608 step-up converter tăng điện áp từ 2-24V lên 5-28V với dòng 2A. Kích thước nhỏ gọn, hiệu suất cao, có biến trở điều chỉnh.', 4.6, 1230),

('TP4056 - Module sạc pin Lithium', 'tp4056-charger', 'Module sạc pin Li-ion/Li-Po với bảo vệ', 
'TP4056 sạc pin lithium 1S với dòng sạc 1A, có mạch bảo vệ quá sạc, quá phóng, ngắn mạch. Cổng micro USB, LED hiển thị trạng thái.', 4.8, 2450),

('AMS1117-3.3V - Module nguồn 3.3V', 'ams1117-3v3-regulator', 'Module ổn áp tuyến tính 3.3V 1A', 
'AMS1117-3.3V cung cấp nguồn ổn định 3.3V với dòng tối đa 1A, điện áp đầu vào 4.5-12V. Phù hợp cho các mạch cần nguồn 3.3V ổn định.', 4.5, 890);

-- Sản phẩm Module truyền thông
INSERT INTO products (name, slug, short_description, description, avg_rating, sold_quantity) VALUES
('NRF24L01+ - Module RF 2.4GHz', 'nrf24l01-rf-module', 'Module RF truyền nhận không dây 2.4GHz', 
'NRF24L01+ hoạt động ở tần số 2.4GHz, tốc độ truyền 250Kbps-2Mbps, khoảng cách 100m. Tiêu thụ điện năng thấp, giao tiếp SPI.', 4.6, 1340),

('HC-05 - Module Bluetooth', 'hc05-bluetooth', 'Module Bluetooth SPP phổ biến nhất', 
'HC-05 là module Bluetooth 2.0 có thể cấu hình Master/Slave, tốc độ truyền 9600-115200 baud. Khoảng cách 10m, giao tiếp UART đơn giản.', 4.7, 1780),

('SIM800L - Module GSM/GPRS', 'sim800l-gsm', 'Module GSM gọi điện và SMS', 
'SIM800L hỗ trợ GSM/GPRS quad-band, gọi điện thoại, gửi/nhận SMS, GPRS data. Giao tiếp UART, hỗ trợ thẻ SIM mini.', 4.4, 420),

('LoRa SX1278 433MHz', 'lora-sx1278-433mhz', 'Module LoRa tầm xa 3-5km', 
'LoRa SX1278 hoạt động ở tần số 433MHz, khoảng cách truyền 3-5km (tầm nhìn), tốc độ 0.3-19.2Kbps. Tiêu thụ điện năng cực thấp, phù hợp cho IoT.', 4.8, 560);

-- Sản phẩm Màn hình
INSERT INTO products (name, slug, short_description, description, avg_rating, sold_quantity) VALUES
('LCD 16x2 I2C xanh lá', 'lcd-16x2-i2c-green', 'Màn hình LCD 16 ký tự x 2 dòng I2C', 
'LCD 1602 với module I2C giúp tiết kiệm chân kết nối, chỉ cần 2 chân SDA/SCL. Nền xanh lá chữ trắng, độ tương phản điều chỉnh được.', 4.7, 1890),

('OLED 0.96" I2C 128x64', 'oled-096-i2c-128x64', 'Màn hình OLED đơn sắc độ phân giải cao', 
'OLED 0.96 inch với độ phân giải 128x64 pixels, màu trắng hoặc xanh dương. Góc nhìn rộng, độ tương phản cao, giao tiếp I2C.', 4.9, 2340),

('TFT LCD 2.4" SPI cảm ứng', 'tft-24-spi-touch', 'Màn hình TFT màu 240x320 có touch', 
'TFT 2.4 inch màu 262K với độ phân giải 240x320, tích hợp cảm ứng điện trở. Giao tiếp SPI, điều khiển ILI9341, phù hợp cho giao diện đồ họa.', 4.6, 780);

-- Sản phẩm Motor & Driver
INSERT INTO products (name, slug, short_description, description, avg_rating, sold_quantity) VALUES
('L298N - Driver động cơ DC', 'l298n-motor-driver', 'Module điều khiển 2 động cơ DC hoặc 1 stepper', 
'L298N có thể điều khiển 2 động cơ DC hoặc 1 động cơ stepper, dòng tối đa 2A/motor, điện áp 5-35V. Có mạch ổn áp 5V tích hợp.', 4.7, 1450),

('Động cơ Servo SG90', 'servo-sg90', 'Servo motor 9g góc quay 180 độ', 
'SG90 là servo nhỏ gọn trọng lượng 9g, góc quay 180°, momen xoắn 1.8kg.cm, điều khiển PWM. Phù hợp cho robot, cánh tay máy nhỏ.', 4.6, 2120),

('Động cơ giảm tốc 12V', 'dc-geared-motor-12v', 'Động cơ DC giảm tốc 100RPM có encoder', 
'Động cơ DC 12V với hộp số giảm tốc tích hợp, tốc độ 100RPM, momen xoắn cao. Có encoder đo tốc độ, phù hợp cho robot di động.', 4.8, 670),

('Động cơ Stepper 28BYJ-48', 'stepper-28byj48', 'Động cơ bước 5V với driver ULN2003', 
'28BYJ-48 là động cơ bước 5V giảm tốc 1/64, góc bước 5.625°. Đi kèm driver ULN2003, điều khiển đơn giản, tiêu thụ điện năng thấp.', 4.5, 890);

-- Liên kết sản phẩm với danh mục
INSERT INTO product_categories (product_id, category_id) VALUES
-- Arduino & Modules
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1),
-- Raspberry Pi
(6, 2), (7, 2), (8, 2),
-- Cảm biến
(9, 4), (10, 4), (11, 4), (12, 4), (13, 4),
-- Module nguồn
(14, 5), (15, 5), (16, 5), (17, 5),
-- Module truyền thông
(18, 6), (19, 6), (20, 6), (21, 6),
-- Màn hình
(22, 7), (23, 7), (24, 7),
-- Motor & Driver
(25, 8), (26, 8), (27, 8), (28, 8);

-- Biến thể sản phẩm (Product Variants)
INSERT INTO product_variants (product_id, variant_name, price, discount_price, in_stock) VALUES
-- Arduino Uno R3
(1, 'Arduino Uno R3 - Chính hãng', 180000, 165000, 45),
(1, 'Arduino Uno R3 - Compatible', 120000, 110000, 120),
-- Arduino Nano
(2, 'Arduino Nano V3 CH340', 65000, 59000, 200),
(2, 'Arduino Nano V3 FTDI', 85000, NULL, 80),
-- Arduino Mega
(3, 'Arduino Mega 2560 R3', 280000, 259000, 35),
-- ESP32
(4, 'ESP32 DevKit V1 - 30 pins', 95000, 89000, 180),
(4, 'ESP32 DevKit V1 - 38 pins', 105000, 98000, 150),
-- ESP8266
(5, 'NodeMCU ESP8266 V3', 75000, 69000, 250),
-- Raspberry Pi 4 4GB
(6, 'Raspberry Pi 4B 4GB - Kit đầy đủ', 1850000, 1750000, 25),
(6, 'Raspberry Pi 4B 4GB - Board only', 1450000, 1380000, 40),
-- Raspberry Pi 3 B+
(7, 'Raspberry Pi 3B+ - Kit cơ bản', 1250000, 1180000, 30),
(7, 'Raspberry Pi 3B+ - Board only', 980000, NULL, 45),
-- Raspberry Pi Zero W
(8, 'Raspberry Pi Zero W', 320000, 299000, 60),
-- DHT22
(9, 'DHT22 - Module có board', 85000, 79000, 150),
(9, 'DHT22 - Sensor only', 65000, NULL, 200),
-- HC-SR04
(10, 'HC-SR04 Ultrasonic', 35000, 32000, 300),
-- BMP280
(11, 'BMP280 Module I2C', 55000, 49000, 100),
-- MPU6050
(12, 'MPU6050 GY-521', 65000, 59000, 180),
-- MQ-2
(13, 'MQ-2 Gas Sensor Module', 45000, 39000, 120),
-- LM2596 Buck
(14, 'LM2596 Buck Converter', 35000, 32000, 200),
-- MT3608 Boost
(15, 'MT3608 Boost Module', 25000, 22000, 180),
-- TP4056
(16, 'TP4056 với bảo vệ', 18000, 15000, 400),
(16, 'TP4056 không bảo vệ', 12000, NULL, 350),
-- AMS1117
(17, 'AMS1117-3.3V Module', 15000, 12000, 250),
-- NRF24L01+
(18, 'NRF24L01+ cơ bản', 35000, 32000, 150),
(18, 'NRF24L01+ có anten', 55000, 49000, 100),
-- HC-05
(19, 'HC-05 Master/Slave', 95000, 89000, 200),
-- SIM800L
(20, 'SIM800L GSM Module', 180000, 165000, 60),
-- LoRa SX1278
(21, 'LoRa 433MHz SX1278', 135000, 125000, 80),
-- LCD 16x2
(22, 'LCD 1602 I2C xanh lá', 65000, 59000, 180),
(22, 'LCD 1602 I2C xanh dương', 65000, 59000, 150),
-- OLED
(23, 'OLED 0.96" màu trắng', 85000, 79000, 250),
(23, 'OLED 0.96" màu xanh', 85000, 79000, 200),
-- TFT 2.4"
(24, 'TFT 2.4" cảm ứng', 185000, 169000, 70),
-- L298N
(25, 'L298N Motor Driver', 55000, 49000, 180),
-- Servo SG90
(26, 'Servo SG90 9g', 35000, 32000, 300),
-- DC Motor
(27, 'DC Motor 12V 100RPM', 125000, 115000, 90),
-- Stepper
(28, 'Stepper 28BYJ-48 + ULN2003', 65000, 59000, 120);

-- Đánh giá sản phẩm
INSERT INTO reviews (user_id, product_id, rating, comment) VALUES
(2, 1, 5, 'Arduino Uno chất lượng tốt, ship nhanh, đóng gói cẩn thận. Rất hài lòng!'),
(3, 1, 5, 'Sản phẩm chính hãng, hoạt động ổn định. Shop tư vấn nhiệt tình.'),
(2, 4, 5, 'ESP32 rất mạnh, WiFi ổn định, lập trình dễ dàng. Giá cả phải chăng.'),
(3, 9, 4, 'DHT22 đo chính xác, giao tiếp đơn giản. Đóng gói tốt.'),
(2, 10, 5, 'HC-SR04 hoạt động tốt, đo khoảng cách chính xác. Giá rẻ.'),
(3, 23, 5, 'OLED sắc nét, độ tương phản cao, rất đẹp. Recommend!'),
(2, 19, 4, 'HC-05 kết nối ổn định, dễ cấu hình. Tốt với giá tiền này.');