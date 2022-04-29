package com.cagan.library.book;

import com.cagan.library.filter.StringFilter;
import liquibase.pro.packaged.V;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.function.Function;

@Transactional(readOnly = true)
public class QueryService<ENTITY> {

    protected Specification<ENTITY> buildStringSpecification(StringFilter filter, SingularAttribute<? super ENTITY, String> field) {
        return this.buildSpecification(filter, (root) -> root.get(field));
    }

    protected Specification<ENTITY> buildSpecification(StringFilter filter, Function<Root<ENTITY>, Expression<String>> metaclassFunction) {
        if (filter.getEquals() != null) {
            return this.equalsSpecification(metaclassFunction, (String) filter.getEquals());
        } else if (filter.getIn() != null) {
            return this.valueIn(metaclassFunction, filter.getIn());
        } else if (filter.getNotIn() != null) {
            return this.valueNotIn(metaclassFunction, filter.getNotIn());
        } else if (filter.getContains() != null) {
            return this.likeUpperSpecification(metaclassFunction, filter.getContains());
        } else if (filter.getDoesNotContains() != null) {
            return this.doesNotContainsSpecification(metaclassFunction, filter.getDoesNotContains());
        }else if (filter.getNotEquals() != null) {
            return this.notEqualsSpecification(metaclassFunction, filter.getNotEquals());
        } else {
            return filter.getSpecified() != null ? this.byFieldSpecified(metaclassFunction, filter.getSpecified()) : null;
        }
    }

    private Specification<ENTITY> byFieldSpecified(Function<Root<ENTITY>, Expression<String>> metaclassFunction, Boolean specified) {
        return specified ?
                (root, query, builder) -> builder.isNotNull(metaclassFunction.apply(root)) :
                (root, query, builder) -> builder.isNull(metaclassFunction.apply(root));
    }

    protected <V> Specification<ENTITY> equalsSpecification(Function<Root<ENTITY>, Expression<V>> metaclassFunction, V value) {
        return (root, query, builder) -> builder.equal(metaclassFunction.apply(root), value);
    }

    protected <V> Specification<ENTITY> notEqualsSpecification(Function<Root<ENTITY>, Expression<V>> metaclassFunction, V value) {
        return (root, query, builder) -> builder.notEqual(metaclassFunction.apply(root), value);
    }

    protected <V> Specification<ENTITY> valueIn(Function<Root<ENTITY>, Expression<V>> metaclassFunction, Collection<V> values) {
        return (root, query, builder) -> {
            CriteriaBuilder.In<V> in = builder.in(metaclassFunction.apply(root));

            for (V value : values) {
                in = in.value(value);
            }

            return in;
        };
    }

    protected <V> Specification<ENTITY> valueNotIn(Function<Root<ENTITY>, Expression<V>> metaclassFunction, Collection<V> values) {
        return (root, query, builder) -> {
            CriteriaBuilder.In<V> in = builder.in(metaclassFunction.apply(root));

            for (V value : values) {
                in = in.value(value);
            }

            return builder.not(in);
        };
    }

    protected Specification<ENTITY> likeUpperSpecification(Function<Root<ENTITY>, Expression<String>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.like(builder.upper(metaclassFunction.apply(root)), this.wrapLikeQuery(value));
    }

    protected Specification<ENTITY> doesNotContainsSpecification(Function<Root<ENTITY>, Expression<String>> metaclassFunction, String value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notLike(metaclassFunction.apply(root), this.wrapLikeQuery(value));
    }

    protected String wrapLikeQuery(String txt) {
        return "%" + txt.toUpperCase() + '%';
    }

    protected Specification<Book> distinct(final boolean distinct) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(distinct);
            return null;
        };
    }
}