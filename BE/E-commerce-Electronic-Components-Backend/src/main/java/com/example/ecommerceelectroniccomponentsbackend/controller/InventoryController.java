package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.entity.Inventory;
import com.example.ecommerceelectroniccomponentsbackend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    // CREATE - POST /api/inventory
    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody Inventory inventory) {
        try {
            Inventory created = inventoryService.createInventory(inventory);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi tạo kho hàng: " + e.getMessage());
        }
    }
    
    // READ ALL - GET /api/inventory
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventories = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventories);
    }
    
    // READ BY ID - GET /api/inventory/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryService.getInventoryById(id);
        if (inventory.isPresent()) {
            return ResponseEntity.ok(inventory.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Kho hàng với ID " + id + " không tồn tại");
    }
    
    // GET INVENTORY BY PRODUCT ID - GET /api/inventory/product/{productId}
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getInventoryByProductId(@PathVariable Long productId) {
        Optional<Inventory> inventory = inventoryService.getInventoryByProductId(productId);
        if (inventory.isPresent()) {
            return ResponseEntity.ok(inventory.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Kho hàng cho sản phẩm ID " + productId + " không tồn tại");
    }
    
    // GET INVENTORY BY WAREHOUSE - GET /api/inventory/warehouse/{warehouse}
    @GetMapping("/warehouse/{warehouse}")
    public ResponseEntity<?> getInventoryByWarehouse(@PathVariable String warehouse) {
        List<Inventory> inventories = inventoryService.getInventoryByWarehouse(warehouse);
        if (!inventories.isEmpty()) {
            return ResponseEntity.ok(inventories);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Không có kho hàng nào với tên '" + warehouse + "'");
    }
    
    // UPDATE - PUT /api/inventory/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable Long id, @RequestBody Inventory updatedInventory) {
        try {
            Optional<Inventory> updated = inventoryService.updateInventory(id, updatedInventory);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Kho hàng với ID " + id + " không tồn tại");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi cập nhật kho hàng: " + e.getMessage());
        }
    }
    
    // UPDATE QUANTITY ONLY - PATCH /api/inventory/{id}/quantity
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            Optional<Inventory> updated = inventoryService.updateQuantity(id, quantity);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Kho hàng với ID " + id + " không tồn tại");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi cập nhật số lượng: " + e.getMessage());
        }
    }
    
    // CHECK LOW STOCK - GET /api/inventory/{id}/low-stock
    @GetMapping("/{id}/low-stock")
    public ResponseEntity<?> isLowStock(@PathVariable Long id) {
        boolean isLow = inventoryService.isLowStock(id);
        if (isLow) {
            return ResponseEntity.ok("Cảnh báo: Số lượng hàng hóa thấp hơn mức tối thiểu");
        }
        return ResponseEntity.ok("Số lượng hàng hóa đủ");
    }
    
    // DELETE - DELETE /api/inventory/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long id) {
        try {
            if (inventoryService.deleteInventory(id)) {
                return ResponseEntity.ok("Kho hàng đã được xóa thành công");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Kho hàng với ID " + id + " không tồn tại");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi xóa kho hàng: " + e.getMessage());
        }
    }
}
