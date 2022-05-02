package com.cagan.library.bookcatalog;

import com.cagan.library.filter.InstantFilter;
import com.cagan.library.filter.IntegerFilter;
import com.cagan.library.filter.LongFilter;
import com.cagan.library.filter.StringFilter;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class BookCatalogCriteria implements Serializable {
    private static final long serialVersionID = 1L;
    private LongFilter id;
    private StringFilter title;
    private StringFilter publisher;
    private StringFilter genre;
    private IntegerFilter height;
    private Boolean distinct;
    private InstantFilter createdDate;

    public StringFilter title() {
        if (title == null) {
            return new StringFilter();
        }
        return title;
    }

    @Override
    public String toString() {
        return "BookCriteria{" +
                "id=" + id +
                ", title=" + title +
                ", publisher=" + publisher +
                ", genre=" + genre +
                ", height=" + height +
                ", distinct=" + distinct +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, height, id, genre, publisher, distinct);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCatalogCriteria that = (BookCatalogCriteria) o;
        return (
                Objects.equals(id, that.id) &&
                        Objects.equals(title, that.title) &&
                        Objects.equals(height, that.height) &&
                        Objects.equals(distinct, that.distinct) &&
                        Objects.equals(genre, that.genre) &&
                        Objects.equals(publisher, that.publisher));
    }
}
