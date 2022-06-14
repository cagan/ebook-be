package com.cagan.library.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "book_in_system")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private BookCatalog bookCatalog;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Book book;

    @Column(name = "is_available")
    private Boolean isAvailable = false;
}
