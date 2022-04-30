package com.cagan.library.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookQueryService extends QueryService<Book> {
    private final BookRepository bookRepository;
    private final BookViewMapper bookViewMapper;

    @Autowired
    public BookQueryService(BookRepository bookRepository, BookViewMapper bookViewMapper) {
        this.bookRepository = bookRepository;
        this.bookViewMapper = bookViewMapper;
    }

    public Page<BookView> findByCriteria(BookCriteria criteria, Pageable pageable) {
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, pageable).map(bookViewMapper::toDto);
    }

    private Specification<Book> createSpecification(final BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);

        if (criteria != null) {
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Book_.id));
            }
            if (criteria.getHeight() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHeight(), Book_.height));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Book_.title));
            }
            if (criteria.getPublisher() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPublisher(), Book_.publisher));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Book_.createdDate));
            }
        }

        return specification;
    }
}
