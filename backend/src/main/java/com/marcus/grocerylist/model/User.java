package com.marcus.grocerylist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User name cannot be empty")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroceryList> lists = new ArrayList<>();

    public User(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(){};
}
