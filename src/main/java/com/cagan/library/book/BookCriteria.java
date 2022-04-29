package com.cagan.library.book;

import com.cagan.library.filter.StringFilter;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class BookCriteria implements Serializable {
    private static final long serialVersionID = 1L;
    private Long id;
    private StringFilter title;
    private String publisher;
    private String genre;
    private Integer height;
    private Boolean distinct;

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
                ", publisher='" + publisher + '\'' +
                ", genre='" + genre + '\'' +
                ", height=" + height +
                ", distinct=" + distinct +
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
        final BookCriteria that = (BookCriteria) o;
        return (
                Objects.equals(id, that.id) &&
                        Objects.equals(title, that.title) &&
                        Objects.equals(height, that.height) &&
                        Objects.equals(distinct, that.distinct) &&
                        Objects.equals(genre, that.genre) &&
                        Objects.equals(publisher, that.publisher));
    }
}
