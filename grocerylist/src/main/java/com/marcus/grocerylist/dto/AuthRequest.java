package com.marcus.grocerylist.dto;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Data
public class AuthRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Email
    private String email;

    public AuthRequest(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
