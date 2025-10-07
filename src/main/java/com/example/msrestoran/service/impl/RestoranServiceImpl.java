package com.example.msrestoran.service.impl;

import com.example.msrestoran.dao.entity.RestoranEntity;
import com.example.msrestoran.dao.repository.RestoranRepositroy;
import com.example.msrestoran.dto.request.RestoranRequest;
import com.example.msrestoran.dto.response.RestoranResponse;
import com.example.msrestoran.expection.RestoranNotFoundException;
import com.example.msrestoran.mapper.RestoranMapping;
import com.example.msrestoran.service.abstraction.RestoranService;
import com.example.msrestoran.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RestoranServiceImpl implements RestoranService {
    RestoranRepositroy repositroy;
    CacheUtil util;
    KafkaTemplate<String, RestoranResponse> kafkaTemplate;
    static String TOPIC = "restaurant-events";

    @Override
    public RestoranResponse save(RestoranRequest request) {
        RestoranEntity entity = RestoranMapping.RESTORAN.requestToEntity(request);
        repositroy.save(entity);
        RestoranResponse response = RestoranMapping.RESTORAN.entityToResponse(entity);
        log.info("kafka send");
        kafkaTemplate.send(TOPIC, response);
        log.info("kafka accept");
        util.set(getKey(entity.getId()), entity, 10, TimeUnit.MINUTES);
        System.out.println("🟢 DB-dən oxundu və Redis-ə yazıldı!");
        return response;
    }

    @Override
    public RestoranResponse findById(Long id) {
        RestoranEntity entitycache = util.get(getKey(id),RestoranEntity.class);
        if (entitycache != null) {
            System.out.println("🔴 Redis-dən oxundu!");
            return RestoranMapping.RESTORAN.entityToResponse(entitycache);
        }
        RestoranEntity entity = fetchRestoranIfExist(id);
        util.set(getKey(entity.getId()), entity, 10, TimeUnit.MINUTES);
        System.out.println("🟢 DB-dən oxundu və Redis-ə yazıldı!");

        return RestoranMapping.RESTORAN.entityToResponse(entity);
    }

    @Override
    public List<RestoranResponse> findAll() {
        log.info("RestaurantServiceImpl.findAll.start");
        List<Long> allIds = repositroy.findAllByStatusIn();
        List<String> keys = allIds.stream()
                .map(id -> "Restoran:" + id)
                .toList();
        Map<String, RestoranEntity> cachedUsers =
                util.multiGet(keys, RestoranEntity.class);
        List<Long> missingIds = new ArrayList<>();
        for (Long id : allIds) {
            String key = "Restoran:" + id;
            if (!cachedUsers.containsKey(key)) {
                missingIds.add(id);
            }
        }

        List<RestoranEntity> result = new ArrayList<>(cachedUsers.values());


        if (!missingIds.isEmpty()) {
            List<RestoranEntity> dbUsers = repositroy.findAllById(missingIds);

            Map<String, RestoranEntity> toCache = new HashMap<>();
            for (RestoranEntity u : dbUsers) {
                toCache.put("Restoran:" + u.getId(), u);
                result.add(u);
            }

            util.multiSet(toCache, 2, TimeUnit.MINUTES);
        }
        log.info("RestaurantServiceImpl.findAll.end");

        return result.stream()
                .map(RestoranMapping.RESTORAN::entityToResponse)
                .toList();
    }

    private RestoranEntity fetchRestoranIfExist(Long id) {
        return repositroy.findById(id).orElseThrow(() ->
                new RestoranNotFoundException("Restoran not found: " + id));
    }

    private String getKey(Long id) {
        return "Restoran:" + id;
    }


}
