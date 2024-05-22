package org.mabr.postservice.service.search;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchService {

    private final EntityManager entityManager;

    @Transactional
    public <E> List<E> searchBy(Class<E> entityClass, String text,
                                List<String> searchableFields, String sortField,
                                int page, int size) {

        var searchSession = Search.session(entityManager);
        var fields = searchableFields.toArray(new String[0]);
        var offset = page * size;

        var result =
                searchSession
                        .search(entityClass)
                        .where(f -> {
                            if (StringUtils.hasText(text)) {
                                return f.match().fields(fields).matching(text).fuzzy(2);
                            } else
                                return f.matchAll();
                        })
                        .sort(f -> f.field(sortField.concat("_sort")).desc())
                        .fetch(offset, size);

        //var total = result.total();

        return result.hits();
    }
}
