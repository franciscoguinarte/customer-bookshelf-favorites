package com.ancora.customerbookshelf.mapper;

import com.ancora.customerbookshelf.dto.CustomerDTO;
import com.ancora.customerbookshelf.model.Customer;

public class CustomerMapper {

    public static CustomerDTO ToDTO(Customer customer){
        return CustomerDTO.builder()
                .cpf(customer.getCpf())
                .email(customer.getEmail())
                .name(customer.getName())
                .build();
    }
    public static Customer toEntity(CustomerDTO customerDTO){
        return Customer.builder()
                .cpf(customerDTO.getCpf())
                .email(customerDTO.getEmail())
                .name(customerDTO.getName())
                .build();
    }

}
