package com.example.msrestoran.dao.repository;

import com.example.msrestoran.dao.entity.RestoranEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestoranRepositroy extends JpaRepository<RestoranEntity,Long> {
//    @Query(value = "SELECT r.id FROM restorans r WHERE r.status IN ('ACTIVE','DELETED')", nativeQuery = true)
    List<Long> findAllByStatusIn();
}
