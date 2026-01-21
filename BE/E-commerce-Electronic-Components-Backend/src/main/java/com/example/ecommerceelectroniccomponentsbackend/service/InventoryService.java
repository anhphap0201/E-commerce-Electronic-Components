package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.entity.Inventory;
import com.example.ecommerceelectroniccomponentsbackend.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    // Create
    public Inventory createInventory(Inventory inventory) {
        inventory.setLastUpdated(System.currentTimeMillis());
        return inventoryRepository.save(inventory);
    }
    
    // Read all
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    
    // Read by ID
    public Optional<Inventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }
    
    // Get inventory by product ID
    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }
    
    // Get inventory by warehouse
    public List<Inventory> getInventoryByWarehouse(String warehouse) {
        return inventoryRepository.findByWarehouse(warehouse);
    }
    
    // Update
    public Optional<Inventory> updateInventory(Long id, Inventory updatedInventory) {
        return inventoryRepository.findById(id).map(inventory -> {
            inventory.setQuantity(updatedInventory.getQuantity());
            inventory.setMinQuantity(updatedInventory.getMinQuantity());
            inventory.setWarehouse(updatedInventory.getWarehouse());
            inventory.setLastUpdated(System.currentTimeMillis());
            return inventoryRepository.save(inventory);
        });
    }
    
    // Update quantity only
    public Optional<Inventory> updateQuantity(Long id, Integer quantity) {
        return inventoryRepository.findById(id).map(inventory -> {
            inventory.setQuantity(quantity);
            inventory.setLastUpdated(System.currentTimeMillis());
            return inventoryRepository.save(inventory);
        });
    }
    
    // Delete
    public boolean deleteInventory(Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Check if inventory is low
    public boolean isLowStock(Long id) {
        return inventoryRepository.findById(id)
                .map(inv -> inv.getQuantity() < inv.getMinQuantity())
                .orElse(false);
    }
}
