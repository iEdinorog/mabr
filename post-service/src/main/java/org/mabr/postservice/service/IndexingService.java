package org.mabr.postservice.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingService {

    private final EntityManager em;
    private static final int THREAD_NUMBER = 4;

    @Transactional
    public void initiateIndexing()  {
        log.info("Initiating indexing");
        try {
            var searchSession = Search.session(em);
            searchSession
                    .massIndexer()
                    .threadsToLoadObjects(THREAD_NUMBER)
                    .startAndWait();
        }  catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("All entities indexed");
    }
}
