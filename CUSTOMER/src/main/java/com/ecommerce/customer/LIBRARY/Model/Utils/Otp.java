package com.ecommerce.customer.LIBRARY.Model.Utils;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "otp")
public class Otp {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @NotNull
    @Column(unique = true)
    private String username;

    private LocalDateTime expiration;

    private Boolean used;
}
