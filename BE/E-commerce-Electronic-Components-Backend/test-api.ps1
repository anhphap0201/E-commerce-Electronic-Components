Write-Host "========== E-Commerce CRUD API Test ==========" -ForegroundColor Cyan
Write-Host ""

try {
    # 1. CREATE Category
    Write-Host "1. CREATE Category" -ForegroundColor Yellow
    $cat = @{name="Bộ vi xử lý"; description="CPU"} | ConvertTo-Json
    $catResp = Invoke-WebRequest -Uri "http://localhost:8080/api/categories" -Method POST -ContentType "application/json" -Body $cat -UseBasicParsing
    $category = $catResp.Content | ConvertFrom-Json
    $catId = $category.id
    Write-Host "✅ Danh mục ID: $catId - $($category.name)`n" -ForegroundColor Green

    # 2. GET All Categories
    Write-Host "2. GET All Categories" -ForegroundColor Yellow
    $cats = Invoke-WebRequest -Uri "http://localhost:8080/api/categories" -Method GET -UseBasicParsing
    $catList = $cats.Content | ConvertFrom-Json
    Write-Host "✅ Tổng danh mục: $($catList.Count)`n" -ForegroundColor Green

    # 3. CREATE Product
    Write-Host "3. CREATE Product" -ForegroundColor Yellow
    $prod = @{name="Intel Core i7"; sku="CPU001"; description="Processor"; price=15000000; category=@{id=$catId}} | ConvertTo-Json
    $prodResp = Invoke-WebRequest -Uri "http://localhost:8080/api/products" -Method POST -ContentType "application/json" -Body $prod -UseBasicParsing
    $product = $prodResp.Content | ConvertFrom-Json
    $prodId = $product.id
    Write-Host "✅ Sản phẩm ID: $prodId - $($product.name) (SKU: $($product.sku))`n" -ForegroundColor Green

    # 4. GET All Products
    Write-Host "4. GET All Products" -ForegroundColor Yellow
    $prods = Invoke-WebRequest -Uri "http://localhost:8080/api/products" -Method GET -UseBasicParsing
    $prodList = $prods.Content | ConvertFrom-Json
    Write-Host "✅ Tổng sản phẩm: $($prodList.Count)`n" -ForegroundColor Green

    # 5. CREATE Inventory
    Write-Host "5. CREATE Inventory" -ForegroundColor Yellow
    $inv = @{product=@{id=$prodId}; quantity=50; minQuantity=10; warehouse="Kho 1"} | ConvertTo-Json
    $invResp = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory" -Method POST -ContentType "application/json" -Body $inv -UseBasicParsing
    $inventory = $invResp.Content | ConvertFrom-Json
    $invId = $inventory.id
    Write-Host "✅ Kho ID: $invId - Số lượng: $($inventory.quantity)`n" -ForegroundColor Green

    # 6. GET All Inventory
    Write-Host "6. GET All Inventory" -ForegroundColor Yellow
    $invs = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory" -Method GET -UseBasicParsing
    $invList = $invs.Content | ConvertFrom-Json
    Write-Host "✅ Tổng kho: $($invList.Count)`n" -ForegroundColor Green

    # 7. UPDATE Product
    Write-Host "7. UPDATE Product" -ForegroundColor Yellow
    $prodUpd = @{name="Intel Core i9"; sku="CPU001"; description="Processor"; price=20000000; category=@{id=$catId}} | ConvertTo-Json
    $updResp = Invoke-WebRequest -Uri "http://localhost:8080/api/products/$prodId" -Method PUT -ContentType "application/json" -Body $prodUpd -UseBasicParsing
    $updProduct = $updResp.Content | ConvertFrom-Json
    Write-Host "✅ Sản phẩm cập nhật - Tên: $($updProduct.name), Giá: $($updProduct.price)`n" -ForegroundColor Green

    # 8. UPDATE Inventory Quantity
    Write-Host "8. UPDATE Inventory Quantity" -ForegroundColor Yellow
    $qtyResp = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/$invId/quantity?quantity=75" -Method PATCH -UseBasicParsing
    $updInv = $qtyResp.Content | ConvertFrom-Json
    Write-Host "✅ Số lượng cập nhật thành: $($updInv.quantity)`n" -ForegroundColor Green

    # 9. CHECK Low Stock
    Write-Host "9. CHECK Low Stock" -ForegroundColor Yellow
    $lowResp = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/$invId/low-stock" -Method GET -UseBasicParsing
    $lowMsg = $lowResp.Content
    Write-Host "✅ $lowMsg`n" -ForegroundColor Green

    # 10. GET Product by Category
    Write-Host "10. GET Products by Category" -ForegroundColor Yellow
    $catProds = Invoke-WebRequest -Uri "http://localhost:8080/api/products/category/$catId" -Method GET -UseBasicParsing
    $catProdList = $catProds.Content | ConvertFrom-Json
    Write-Host "✅ Sản phẩm trong danh mục: $($catProdList.Count)`n" -ForegroundColor Green

    Write-Host "========== ✅ ALL TESTS PASSED ==========" -ForegroundColor Green
}
catch {
    Write-Host "❌ ERROR: $_" -ForegroundColor Red
}
