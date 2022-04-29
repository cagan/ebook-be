package com.cagan.library.domain;

import javax.persistence.*;

@Table(name = "deneme")
@Entity
public class Deneme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;
}
