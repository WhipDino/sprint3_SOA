package br.com.gambling.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * DTO para criação e atualização de usuários
 */
public class UserRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Número de telefone inválido")
    private String phoneNumber;

    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDateTime dateOfBirth;

    // Construtores
    public UserRequestDto() {}

    public UserRequestDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}