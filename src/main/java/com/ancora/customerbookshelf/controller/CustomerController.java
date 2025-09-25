package com.ancora.customerbookshelf.controller;

import com.ancora.customerbookshelf.dto.CustomerDTO;
import com.ancora.customerbookshelf.dto.UpdateCustomerDTO;
import com.ancora.customerbookshelf.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerDTO request) {
        log.info("Received request to create new customer with email: {}", request.getEmail());
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        log.info("Received request to get customer by id: {}", id);
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerDTO request) {
        log.info("Received request to update customer with id: {}", id);
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Received request to delete customer with id: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<CustomerDTO>> searchAll(
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Received request to search all customers with pageable: {}", pageable);
        Page<CustomerDTO> page = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(page);
    }
}
