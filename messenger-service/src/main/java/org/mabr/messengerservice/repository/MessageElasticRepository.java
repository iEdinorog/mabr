package org.mabr.messengerservice.repository;

import org.mabr.messengerservice.document.MessageElastic;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageElasticRepository extends ElasticsearchRepository<MessageElastic, Integer> {

    List<MessageElastic> findByContent(String content, PageRequest pageable);
}
