-- =====================================================
-- E-commerce Electronic Components - Database Schema
-- Database: PostgreSQL
-- =====================================================

-- Drop tables if exist (in reverse order of dependencies)
DROP TABLE IF EXISTS inventory CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================================
-- Table: users
-- Description: Stores user authentication and authorization data
-- =====================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster email lookups
CREATE INDEX idx_users_email ON users(email);

-- =====================================================
-- Table: categories
-- Description: Product categories for organizing products
-- =====================================================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(500),
    created_at BIGINT,
    updated_at BIGINT
);

-- Index for faster name lookups
CREATE INDEX idx_categories_name ON categories(name);

-- =====================================================
-- Table: products
-- Description: Electronic components/products for sale
-- =====================================================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(1000),
    price DOUBLE PRECISION NOT NULL CHECK (price >= 0),
    category_id BIGINT NOT NULL,
    created_at BIGINT,
    updated_at BIGINT,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);

-- Indexes for faster lookups
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_name ON products(name);

-- =====================================================
-- Table: inventory
-- Description: Inventory/stock management for products
-- =====================================================
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT UNIQUE NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    min_quantity INTEGER NOT NULL CHECK (min_quantity >= 0),
    warehouse VARCHAR(255) NOT NULL,
    last_updated BIGINT,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);

-- Index for faster product lookups
CREATE INDEX idx_inventory_product_id ON inventory(product_id);
CREATE INDEX idx_inventory_warehouse ON inventory(warehouse);

-- =====================================================
-- Trigger: Update updated_at timestamp for users
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Comments for documentation
-- =====================================================
COMMENT ON TABLE users IS 'User authentication and authorization table';
COMMENT ON TABLE categories IS 'Product categories for organizing electronic components';
COMMENT ON TABLE products IS 'Electronic components/products catalog';
COMMENT ON TABLE inventory IS 'Inventory and stock management for products';

COMMENT ON COLUMN users.role IS 'User role: ROLE_USER or ROLE_ADMIN';
COMMENT ON COLUMN products.sku IS 'Stock Keeping Unit - unique product identifier';
COMMENT ON COLUMN inventory.min_quantity IS 'Minimum quantity threshold for reorder alerts';
