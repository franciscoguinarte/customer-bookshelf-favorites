package com.ancora.customerbookshelf.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
@Builder
public class CustomerDTO {

    private String name;
    @Email
    private String email;
    @CPF
    private String cpf;

}
