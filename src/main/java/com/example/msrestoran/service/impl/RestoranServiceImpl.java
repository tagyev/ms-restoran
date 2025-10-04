package com.example.msrestoran.service.impl;

import com.example.msrestoran.dao.entity.RestoranEntity;
import com.example.msrestoran.dao.repository.RestoranRepositroy;
import com.example.msrestoran.dto.request.RestoranRequest;
import com.example.msrestoran.dto.response.RestoranResponse;
import com.example.msrestoran.mapper.RestoranMapping;
import com.example.msrestoran.service.abstraction.RestoranService;
import com.example.msrestoran.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RestoranServiceImpl implements RestoranService {
    RestoranRepositroy repositroy;
    CacheUtil util;

    @Override
    public RestoranResponse save(RestoranRequest request) {
        RestoranEntity entity = RestoranMapping.requestToEntity(request);
        repositroy.save(entity);
        util.saveToCache(getKey(entity.getId()), entity, 10L, ChronoUnit.MINUTES);
        System.out.println("🟢 DB-dən oxundu və Redis-ə yazıldı!");
        return RestoranMapping.entityToResponse(entity);
    }

    @Override
    public RestoranResponse findById(Long id) {
        RestoranEntity entitycache = util.getBucket(getKey(id));
        if (entitycache != null) {
            System.out.println("🔴 Redis-dən oxundu!");
            return RestoranMapping.entityToResponse(entitycache);
        }
        RestoranEntity entity = fetchRestoranIfExist(id);
        util.saveToCache(getKey(entity.getId()), entity, 10L, ChronoUnit.MINUTES);
        System.out.println("🟢 DB-dən oxundu və Redis-ə yazıldı!");

        return RestoranMapping.entityToResponse(entity);
    }

    @Override
    public Set<RestoranResponse> findAll() {

        Set<RestoranEntity> cachedRestaurants = util.getBucket("RESTAURANTS:ALL");
        if (cachedRestaurants != null && !cachedRestaurants.isEmpty()) {
            System.out.println("🔴 Redis-dən oxundu (bütün restoranlar)!");
            return cachedRestaurants.stream()
                    .map(entity -> RestoranResponse.builder()
                            .id(entity.getId())
                            .name(entity.getName())
                            .address(entity.getAddress())
                            .build())
                    .collect(Collectors.toSet());
        }


        Set<RestoranEntity> entities = new HashSet<>(repositroy.findAll());


        util.saveToCache("RESTAURANTS:ALL", entities, 10L, ChronoUnit.MINUTES);
        System.out.println("🟢 DB-dən oxundu və Redis-ə yazıldı (bütün restoranlar)!");


        return entities.stream()
                .map(entity -> RestoranResponse.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .address(entity.getAddress())
                        .build())
                .collect(Collectors.toSet());
    }

    private RestoranEntity fetchRestoranIfExist(Long id) {
        return repositroy.findById(id).orElseThrow(() ->
                new RuntimeException("User not found: " + id));
    }

    private String getKey(Long id) {
        return "Restoran:" + id;
    }


}
