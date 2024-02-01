package com.ecommerce.customer.LIBRARY.Model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admins", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "image"}))
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Size(min = 3, max = 10, message = "First name contains 3-10 characters")
    private String firstName;

    @Size(min = 3, max = 10, message = "Last name contains 3-10 characters")
    private String lastName;

    @NotNull(message = "username should not be null")
    @Size(min = 5, max = 30, message = "Email should consist at-least 5-50 characters")
    private String username;

    @NotNull(message = "Password should not be null")
    private String password;

    private String image;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "admins_roles", joinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private List<Role> roles;
}
