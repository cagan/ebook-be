package com.cagan.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "book")
@Getter
@Setter
public class Book extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "object_locator", unique = true)
    private String objectLocator;

    @OneToMany(mappedBy = "book", targetEntity = BookInSystem.class)
    private List<BookInSystem> bookInSystems;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", objectLocator='" + objectLocator + '\'' +
                ", bookInSystems=" + bookInSystems +
                '}';
    }
}
