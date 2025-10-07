package com.example.msrestoran.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class CacheUtil {
    RedissonClient redissonClient;
    ObjectMapper mapper;


    @SneakyThrows
    public <T> void set(String key, T data, int timeToLive, TimeUnit timeUnit) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        String serialized = mapper.writeValueAsString(data);
        bucket.set(serialized, java.time.Duration.ofMillis(timeUnit.toMillis(timeToLive)));
    }

    @SneakyThrows
    public <T> T get(String key, Class<T> clazz) {

        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (!bucket.isExists()) return null;
        return mapper.readValue((String) bucket.get(), clazz);
    }

    public void delete(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            bucket.delete();
        }
    }

    @SneakyThrows
    public <T> Map<String, T> multiGet(List<String> keys, Class<T> clazz) {
        RBatch batch = redissonClient.createBatch();
        Map<String, RFuture<Object>> futures = new HashMap<>();

        for (String key : keys) {
            futures.put(key, batch.getBucket(key).getAsync());
        }

        batch.execute();

        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, RFuture<Object>> entry : futures.entrySet()) {
            Object val = entry.getValue().getNow();
            if (val != null) {
                result.put(entry.getKey(), mapper.readValue((String) val, clazz));
            }
        }
        return result;
    }


    @SneakyThrows
    public <T> void multiSet(Map<String, T> dataMap, int ttl, TimeUnit unit) {
        RBatch batch = redissonClient.createBatch();
        for (Map.Entry<String, T> entry : dataMap.entrySet()) {
            String serialized = mapper.writeValueAsString(entry.getValue());
            batch.getBucket(entry.getKey())
                    .setAsync(serialized, Duration.ofMillis(unit.toMillis(ttl)));
        }
        batch.execute();
    }
}