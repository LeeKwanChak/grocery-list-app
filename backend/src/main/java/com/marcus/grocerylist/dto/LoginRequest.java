package com.marcus.grocerylist.dto;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    public LoginRequest(String email, String password){
        this.email= email;
        this.password = password;
    }
}