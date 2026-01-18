package com.example.ecommerceelectroniccomponentsbackend.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("refresh_tokens")
@Data
@Builder
@Getter
@Setter
public class RefreshToken {

    @Id
    private String jwtId;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long ttl;
}

