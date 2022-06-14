package com.cagan.library.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "book_catalog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCatalog extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "title", nullable = false, unique = true, updatable = true)
    private String title;

    @Column(name = "author", length = 50)
    private String author;

//    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private String genre;

    @Column(name = "height")
    private Integer height;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "stripe_product_id")
    private String productId;

    @Column(name = "stripe_price_id")
    private String priceId;

    @JsonIgnore
    @Column
    @OneToMany(mappedBy = "bookCatalog", fetch = FetchType.LAZY)
    private List<Cart> carts;

    @Column(name = "product_category")
    @Convert(converter = ProductCategoryConverter.class)
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "bookCatalog", targetEntity = BookInSystem.class)
    private List<BookInSystem> booksInSystem;

    @PrePersist
    public void prePersist() {
        if (productCategory == null) {
            productCategory = ProductCategory.BOOK;
        }
    }

    @Override
    public String toString() {
        return "BookCatalog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", height=" + height +
                ", publisher='" + publisher + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", price=" + price +
                '}';
    }
}
