package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.entity.TestAPI;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class Controller {

    private List<TestAPI> detailsList = new ArrayList<>();
    {
        detailsList.add(new TestAPI(1, "Resistor"));
        detailsList.add(new TestAPI(2, "Capacitor"));
    }
    // GET API → Fetch all details
    @GetMapping("/details")
    public List<TestAPI> getAllDetails() {
        return detailsList;
    }

    // POST API → Add new detail
    @PostMapping("/details")
    public String addDetails(@RequestBody TestAPI details) {
        detailsList.add(details);
        return "Data Inserted Successfully";
    }

    // PUT API → Update detail by ID
    @PutMapping("/details/{id}")
    public String updateDetails(@PathVariable int id, @RequestBody TestAPI updatedDetails) {
        for (TestAPI details : detailsList) {
            if (details.getId() == id) {
                details.setName(updatedDetails.getName());
                return "Data Updated Successfully";
            }
        }
        return "Detail not found!";
    }

    // DELETE API → Remove detail by ID
    @DeleteMapping("/details/{id}")
    public String deleteDetails(@PathVariable int id) {
        detailsList.removeIf(details -> details.getId() == id);
        return "Data Deleted Successfully";
    }
}
