package com.ancora.customerbookshelf.service;

import com.ancora.customerbookshelf.dto.CustomerDTO;
import com.ancora.customerbookshelf.exception.ConflictException;
import com.ancora.customerbookshelf.exception.NoContentException;
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

        Customer customer = CustomerMapper.toEntity(customerDTO);
        customerRepository.save(customer);
        return customerDTO;
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