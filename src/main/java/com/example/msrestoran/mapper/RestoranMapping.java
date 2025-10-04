package com.example.msrestoran.mapper;

import com.example.msrestoran.dao.entity.RestoranEntity;
import com.example.msrestoran.dto.request.RestoranRequest;
import com.example.msrestoran.dto.response.RestoranResponse;
import com.example.msrestoran.util.CacheUtil;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.msrestoran.enums.RestoranStatus.ACTIVE;

public class RestoranMapping {
    public static RestoranEntity requestToEntity(RestoranRequest request) {
        return RestoranEntity.builder()
                .name(request.getName())
                .address(request.getAddress())
                .status(ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static RestoranResponse entityToResponse(RestoranEntity entity) {
        return RestoranResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .build();
    }
}
