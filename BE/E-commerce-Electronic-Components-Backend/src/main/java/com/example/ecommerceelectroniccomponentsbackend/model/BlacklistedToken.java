package com.example.ecommerceelectroniccomponentsbackend.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("jwt_blacklist")
@Data
@Builder
public class BlacklistedToken {

    @Id
    private String jwtId;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long ttl;
}

