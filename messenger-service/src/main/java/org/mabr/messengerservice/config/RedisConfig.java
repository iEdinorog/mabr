package org.mabr.messengerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        var config = new RedisStandaloneConfiguration();

        config.setHostName("localhost");
        config.setPort(6379);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(objectMapper)));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        var serializer = RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(objectMapper));

        return (builder) -> builder
                .withCacheConfiguration("chat",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
                                .serializeValuesWith(serializer))
                .withCacheConfiguration("messages",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
                                .serializeValuesWith(serializer))
                ;
    }
}
