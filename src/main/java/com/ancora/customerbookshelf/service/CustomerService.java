package com.ancora.customerbookshelf.service;

import com.ancora.customerbookshelf.dto.CustomerDTO;
import com.ancora.customerbookshelf.exception.ConflictException;
import com.ancora.customerbookshelf.exception.NoContentException;
import com.ancora.customerbookshelf.exception.ResourceNotFoundException;
import com.ancora.customerbookshelf.mapper.CustomerMapper;
import com.ancora.customerbookshelf.model.Customer;
import com.ancora.customerbookshelf.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    @Transactional
    public CustomerDTO createCustomer(@Valid CustomerDTO customerDTO) {
        String emailNormalized = customerDTO.getEmail().trim().toLowerCase();
        if (customerRepository.findByEmail(emailNormalized).isPresent()) {
            throw new ConflictException("Email already registered");
        }
        customerRepository.findByCpf(customerDTO.getCpf()).ifPresent(c -> {
            throw new ConflictException("CPF already registered");
        });

        Customer customer = CustomerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapper.ToDTO(savedCustomer);
    }

    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(CustomerMapper::ToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, @Valid CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        String emailNormalized = customerDTO.getEmail().trim().toLowerCase();
        customerRepository.findByEmail(emailNormalized).ifPresent(existingCustomer -> {
            if (!existingCustomer.getId().equals(id)) {
                throw new ConflictException("Email already registered");
            }
        });

        customerRepository.findByCpf(customerDTO.getCpf()).ifPresent(existingCustomer -> {
            if (!existingCustomer.getId().equals(id)) {
                throw new ConflictException("CPF already registered");
            }
        });

        Customer updatedCustomerData = Customer.builder()
                .id(customer.getId())
                .name(customerDTO.getName())
                .email(emailNormalized)
                .cpf(customerDTO.getCpf())
                .createdAt(customer.getCreatedAt())
                .build();

        Customer updatedCustomer = customerRepository.save(updatedCustomerData);
        return CustomerMapper.ToDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        Page<Customer> page = customerRepository.findAll(pageable);
        if (page.isEmpty()) {
            throw new NoContentException("Don't have any customers");
        }
        return page.map(CustomerMapper::ToDTO);
    }

    @Transactional
    public List<CustomerDTO> getCustomerById() {
        List<Customer> customers = customerRepository.findAll();
        if(customers.isEmpty()){
            throw new NoContentException("Don't have any customers");
        }
        return customers.stream().map(CustomerMapper::ToDTO).toList();
    }

}