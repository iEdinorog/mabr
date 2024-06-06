package org.mabr.messengerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MessengerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessengerServiceApplication.class, args);
    }
}