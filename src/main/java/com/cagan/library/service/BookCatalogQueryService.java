package com.cagan.library.service;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.BookCatalog_;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.service.criteria.BookCatalogCriteria;
import com.cagan.library.service.dto.view.BookCatalogView;
import com.cagan.library.service.mapper.BookCatalogViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@Transactional(readOnly = true)
@Transactional
public class BookCatalogQueryService extends QueryService<BookCatalog> {
    private final BookCatalogRepository bookCatalogRepository;
    private final BookCatalogViewMapper bookViewMapper;

    @Autowired
    public BookCatalogQueryService(BookCatalogRepository bookRepository, BookCatalogViewMapper bookViewMapper) {
        this.bookCatalogRepository = bookRepository;
        this.bookViewMapper = bookViewMapper;
    }

    public Page<BookCatalogView> findByCriteria(BookCatalogCriteria criteria, Pageable pageable) {
        final Specification<BookCatalog> specification = createSpecification(criteria);
        return bookCatalogRepository.findAll(specification, pageable).map(bookViewMapper::toDto);
    }

    public List<BookCatalogView> findByCriteria(BookCatalogCriteria criteria) {
        final Specification<BookCatalog> specification = createSpecification(criteria);
        return bookViewMapper.toDto(bookCatalogRepository.findAll(specification));
    }

    private Specification<BookCatalog> createSpecification(final BookCatalogCriteria criteria) {
        Specification<BookCatalog> specification = Specification.where(null);

        if (criteria != null) {
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BookCatalog_.id));
            }
            if (criteria.getHeight() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHeight(), BookCatalog_.height));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), BookCatalog_.title));
            }
            if (criteria.getPublisher() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPublisher(), BookCatalog_.publisher));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), BookCatalog_.createdDate));
            }
        }

        return specification;
    }

//    @Transactional(readOnly = true)
    public long countByCriteria(BookCatalogCriteria criteria) {
        final Specification<BookCatalog> specification = createSpecification(criteria);
        return bookCatalogRepository.count(specification);
    }
}
