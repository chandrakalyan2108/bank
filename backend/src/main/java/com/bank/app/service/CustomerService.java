package com.bank.app.service;

import com.bank.app.dto.CustomerRequest;
import com.bank.app.entity.Customer;
import com.bank.app.exception.ResourceNotFoundException;
import com.bank.app.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(CustomerRequest request, Long createdBy) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .aadhaar(request.getAadhaar())
                .createdBy(createdBy)
                .build();
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }
}
