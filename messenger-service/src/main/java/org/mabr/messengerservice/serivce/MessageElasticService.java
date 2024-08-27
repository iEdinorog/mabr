package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import org.mabr.messengerservice.document.MessageElastic;
import org.mabr.messengerservice.repository.MessageElasticRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageElasticService {

    private final MessageElasticRepository elasticRepository;

    public MessageElastic save(MessageElastic message) {
        return elasticRepository.save(message);
    }

    public List<MessageElastic> findByContent(String content, int page, int size) {
        return elasticRepository.findByContent(content,
                PageRequest.of(page, size, Sort.by("sentAt").descending()));
    }

}
