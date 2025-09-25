package com.ancora.customerbookshelf.service;

import com.ancora.customerbookshelf.dto.CustomerDTO;
import com.ancora.customerbookshelf.dto.UpdateCustomerDTO;
import com.ancora.customerbookshelf.exception.ConflictException;
import com.ancora.customerbookshelf.exception.NoContentException;
import com.ancora.customerbookshelf.exception.ResourceNotFoundException;
import com.ancora.customerbookshelf.mapper.CustomerMapper;
import com.ancora.customerbookshelf.model.Customer;
import com.ancora.customerbookshelf.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    @Transactional
    public CustomerDTO createCustomer(@Valid CustomerDTO customerDTO) {
        log.debug("Attempting to create customer with email: {} and CPF: {}", customerDTO.getEmail(), customerDTO.getCpf());

        String emailNormalized = customerDTO.getEmail().trim().toLowerCase();
        if (customerRepository.findByEmail(emailNormalized).isPresent()) {
            throw new ConflictException("Email already registered");
        }
        customerRepository.findByCpf(customerDTO.getCpf()).ifPresent(c -> {
            throw new ConflictException("CPF already registered");
        });

        Customer customer = CustomerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Successfully created customer with ID: {}", savedCustomer.getId());
        return CustomerMapper.ToDTO(savedCustomer);
    }

    public CustomerDTO getCustomerById(Long id) {
        log.debug("Attempting to find customer with ID: {}", id);
        return customerRepository.findById(id)
                .map(CustomerMapper::ToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, @Valid UpdateCustomerDTO customerDTO) {
        log.debug("Attempting to update customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        String emailNormalized = customerDTO.getEmail().trim().toLowerCase();
        customerRepository.findByEmail(emailNormalized).ifPresent(existingCustomer -> {
            if (!existingCustomer.getId().equals(id)) {
                throw new ConflictException("Email already registered");
            }
        });

        customer.setName(customerDTO.getName());
        customer.setEmail(emailNormalized);

        Customer updatedCustomer = customerRepository.save(customer);

        log.info("Successfully updated customer with ID: {}", updatedCustomer.getId());
        return CustomerMapper.ToDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.debug("Attempting to delete customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Successfully deleted customer with ID: {}", id);
    }

    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        log.debug("Attempting to find all customers for page request: {}", pageable);
        Page<Customer> page = customerRepository.findAll(pageable);
        if (page.isEmpty()) {
            throw new NoContentException("Don't have any customers");
        }
        return page.map(CustomerMapper::ToDTO);
    }


}
