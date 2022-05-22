package com.cagan.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends AbstractAuditingEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "quantity")
    @NotNull
    private Integer quantity;

    @NotNull
    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @OneToOne
    @JoinColumn(name = "book_catalog_id", referencedColumnName = "id")
    private BookCatalog bookCatalog;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id) && Objects.equals(quantity, orderItem.quantity) && Objects.equals(price, orderItem.price) && Objects.equals(order, orderItem.order) && Objects.equals(bookCatalog, orderItem.bookCatalog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, price, order, bookCatalog);
    }
}
