package org.mabr.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.mabr.notificationservice.dto.MessageSentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, MessageSentEvent> messageSentEventConsumerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "msg");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "messageSent:org.mabr.notificationservice.dto.MessageSentEvent");

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(MessageSentEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageSentEvent>
    messageSentEventConcurrentKafkaListenerContainerFactory() {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, MessageSentEvent>();
        factory.setConsumerFactory(messageSentEventConsumerFactory());

        return factory;
    }
}
