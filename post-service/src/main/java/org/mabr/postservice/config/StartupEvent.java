package org.mabr.postservice.config;

import org.mabr.postservice.service.IndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupEvent implements ApplicationListener<ContextRefreshedEvent> {

    private final IndexingService service;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        service.initiateIndexing();
    }
}
