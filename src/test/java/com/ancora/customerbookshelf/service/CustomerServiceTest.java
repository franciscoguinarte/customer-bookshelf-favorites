package com.ancora.customerbookshelf.service;

import com.ancora.customerbookshelf.dto.CustomerDTO;
import com.ancora.customerbookshelf.exception.ConflictException;
import com.ancora.customerbookshelf.exception.ResourceNotFoundException;
import com.ancora.customerbookshelf.model.Customer;
import com.ancora.customerbookshelf.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .cpf("12345678900")
                .createdAt(LocalDateTime.now())
                .build();

        customerDTO = CustomerDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .cpf("12345678900")
                .build();
    }

    @Test
    void testCreateCustomer_Success() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getCreatedAt(), result.getCreatedAt()); // Verifica se a data de criação foi mapeada
    }

    @Test
    void testCreateCustomer_EmailConflict() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            customerService.createCustomer(customerDTO);
        });

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void testCreateCustomer_CpfConflict() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(customer));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            customerService.createCustomer(customerDTO);
        });

        assertEquals("CPF already registered", exception.getMessage());
    }

    @Test
    void testGetCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(1L);
        assertNotNull(result);
        assertEquals(customer.getName(), result.getName());
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerById(99L);
        });
    }
}
