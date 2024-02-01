package com.ecommerce.customer.LIBRARY.Model.Utils;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "token")
public class Token {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String token;

    private LocalDateTime expiration;

    private boolean used;

    private String tokenFor;

}
