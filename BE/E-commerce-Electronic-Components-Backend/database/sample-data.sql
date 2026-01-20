-- =====================================================
-- E-commerce Electronic Components - Sample Data
-- Database: PostgreSQL
-- =====================================================

-- Clean existing data (in reverse order of dependencies)
TRUNCATE TABLE inventory RESTART IDENTITY CASCADE;
TRUNCATE TABLE products RESTART IDENTITY CASCADE;
TRUNCATE TABLE categories RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- =====================================================
-- Insert Sample Users
-- Password: "admin123" and "user123" (BCrypt encoded)
-- You should generate these using BCrypt with strength 10
-- =====================================================
INSERT INTO users (email, password, role, created_at, updated_at) VALUES
-- Admin user (password: admin123)
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
('admin@ecommerce.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Regular user (password: user123)
-- BCrypt hash: $2a$10$DOwnh2KYn6L9LPSt1mGULe5VYU6L4W1W1QnJZ/6nT9LxRr6mYY.Ym
('user@ecommerce.com', '$2a$10$DOwnh2KYn6L9LPSt1mGULe5VYU6L4W1W1QnJZ/6nT9LxRr6mYY.Ym', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- Insert Sample Categories
-- =====================================================
INSERT INTO categories (name, description, created_at, updated_at) VALUES
('Resistors', 'Fixed and variable resistors for electronic circuits', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Capacitors', 'Ceramic, electrolytic, and tantalum capacitors', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Transistors', 'BJT, MOSFET, and JFET transistors', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Integrated Circuits', 'Microcontrollers, op-amps, and logic ICs', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Diodes', 'Rectifier, LED, Zener, and Schottky diodes', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Connectors', 'Headers, terminal blocks, and cable connectors', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Sensors', 'Temperature, pressure, and motion sensors', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Power Supplies', 'AC/DC converters and voltage regulators', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT);

-- =====================================================
-- Insert Sample Products
-- =====================================================
INSERT INTO products (name, sku, description, price, category_id, created_at, updated_at) VALUES
-- Resistors
('Carbon Film Resistor 1K Ohm', 'RES-CF-1K', '1/4W carbon film resistor, 5% tolerance', 0.05, 1, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Metal Film Resistor 10K Ohm', 'RES-MF-10K', '1/4W metal film resistor, 1% tolerance', 0.08, 1, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Potentiometer 100K Ohm', 'RES-POT-100K', 'Linear taper potentiometer', 0.50, 1, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Capacitors
('Ceramic Capacitor 100nF', 'CAP-CER-100N', '50V ceramic capacitor, X7R', 0.10, 2, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Electrolytic Capacitor 1000uF', 'CAP-ELE-1000U', '25V electrolytic capacitor', 0.25, 2, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Tantalum Capacitor 47uF', 'CAP-TAN-47U', '16V tantalum capacitor', 0.40, 2, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Transistors
('NPN Transistor 2N2222', 'TRN-NPN-2N2222', 'General purpose NPN transistor', 0.15, 3, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('PNP Transistor 2N3906', 'TRN-PNP-2N3906', 'General purpose PNP transistor', 0.15, 3, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('N-Channel MOSFET IRF520', 'TRN-MOS-IRF520', '100V, 9.7A N-channel MOSFET', 0.85, 3, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Integrated Circuits
('Arduino Nano', 'IC-MCU-NANO', 'ATmega328P microcontroller board', 4.50, 4, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('LM358 Op-Amp', 'IC-OP-LM358', 'Dual operational amplifier', 0.30, 4, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('74HC595 Shift Register', 'IC-LOG-74HC595', '8-bit shift register with output latches', 0.35, 4, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Diodes
('1N4007 Rectifier Diode', 'DIO-REC-1N4007', '1A 1000V rectifier diode', 0.08, 5, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Red LED 5mm', 'DIO-LED-RED5MM', '5mm red LED, 20mA', 0.12, 5, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Zener Diode 5.1V', 'DIO-ZEN-5V1', '5.1V 1W Zener diode', 0.18, 5, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Connectors
('2.54mm Header Pins 40P', 'CON-HDR-40P', '40-pin single row header, 2.54mm pitch', 0.60, 6, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Screw Terminal Block 2P', 'CON-TER-2P', '2-position screw terminal block, 5mm pitch', 0.35, 6, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('USB-C Connector', 'CON-USB-TYPEC', 'USB Type-C receptacle connector', 1.20, 6, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Sensors
('DHT22 Temperature Sensor', 'SEN-TEMP-DHT22', 'Digital temperature and humidity sensor', 3.50, 7, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('HC-SR04 Ultrasonic Sensor', 'SEN-DIST-HCSR04', 'Ultrasonic distance sensor, 2-400cm', 2.50, 7, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('PIR Motion Sensor', 'SEN-MOT-PIR', 'Passive infrared motion detector', 1.80, 7, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Power Supplies
('LM7805 Voltage Regulator', 'PWR-REG-7805', '5V 1A linear voltage regulator', 0.45, 8, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('Buck Converter Module', 'PWR-BUCK-LM2596', 'Adjustable step-down converter, 3A', 1.50, 8, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
('USB Power Bank Module', 'PWR-USB-5V2A', '5V 2A USB charging module', 2.30, 8, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT);

-- =====================================================
-- Insert Sample Inventory
-- =====================================================
INSERT INTO inventory (product_id, quantity, min_quantity, warehouse, last_updated) VALUES
-- Resistors
(1, 5000, 1000, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(2, 3500, 1000, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(3, 500, 100, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Capacitors
(4, 4000, 1000, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(5, 2500, 500, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(6, 1200, 300, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Transistors
(7, 3000, 500, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(8, 2800, 500, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(9, 800, 200, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Integrated Circuits
(10, 250, 50, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(11, 1500, 300, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(12, 1000, 200, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Diodes
(13, 6000, 1000, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(14, 4500, 1000, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(15, 1800, 500, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Connectors
(16, 800, 200, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(17, 1500, 300, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(18, 600, 150, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Sensors
(19, 300, 50, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(20, 450, 100, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(21, 380, 80, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),

-- Power Supplies
(22, 2000, 400, 'Main Warehouse A', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(23, 550, 100, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT),
(24, 420, 80, 'Main Warehouse B', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::BIGINT);

-- =====================================================
-- Verify Data Insertion
-- =====================================================
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'Categories', COUNT(*) FROM categories
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Inventory', COUNT(*) FROM inventory;

-- =====================================================
-- Sample Queries for Testing
-- =====================================================

-- Get all products with their categories
-- SELECT p.id, p.name, p.sku, p.price, c.name as category_name
-- FROM products p
-- JOIN categories c ON p.category_id = c.id
-- ORDER BY c.name, p.name;

-- Get low stock items
-- SELECT p.name, p.sku, i.quantity, i.min_quantity, i.warehouse
-- FROM inventory i
-- JOIN products p ON i.product_id = p.id
-- WHERE i.quantity < i.min_quantity
-- ORDER BY i.warehouse, p.name;

-- Get products by category with inventory
-- SELECT p.id, p.name, p.sku, p.price, i.quantity, i.warehouse
-- FROM products p
-- JOIN categories c ON p.category_id = c.id
-- LEFT JOIN inventory i ON p.id = i.product_id
-- WHERE c.name = 'Integrated Circuits'
-- ORDER BY p.name;
