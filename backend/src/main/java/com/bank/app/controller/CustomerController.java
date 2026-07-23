package com.bank.app.controller;

import com.bank.app.dto.ApiResponse;
import com.bank.app.dto.CustomerRequest;
import com.bank.app.entity.Customer;
import com.bank.app.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // Cashier creates a customer
    @PostMapping
    public ApiResponse<Customer> createCustomer(@Valid @RequestBody CustomerRequest request,
                                                 Authentication authentication) {
        Customer customer = customerService.createCustomer(request, null);
        return ApiResponse.ok("Customer created successfully", customer);
    }

    @GetMapping
    public ApiResponse<List<Customer>> getAllCustomers() {
        return ApiResponse.ok("Customers fetched", customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> getCustomer(@PathVariable Long id) {
        return ApiResponse.ok("Customer fetched", customerService.getCustomer(id));
    }
}
