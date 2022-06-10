package com.cagan.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Cart extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    public Cart() {}

    @ManyToOne
    @JoinColumn(name = "book_catalog_id", referencedColumnName = "id")
    private BookCatalog bookCatalog;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "quantity")
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Cart cart = (Cart) o;
        return id != null && Objects.equals(id, cart.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", bookCatalog=" + bookCatalog +
                ", user=" + user +
                ", quantity=" + quantity +
                '}';
    }
}
