/***********Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 03-05-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/

package com.saber.taxiconfig.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {

    // Constant defining the Redis channel for accepted events.
    public static final String ACCEPTED_EVENT_CHANNEL = "accepted_event_channel";

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){

        return new LettuceConnectionFactory("db", 6379);
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory){
        return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext.string());
    }
}
