package com.example.msrestoran.dao.repository;

import com.example.msrestoran.dao.entity.RestoranEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestoranRepositroy extends JpaRepository<RestoranEntity,Long> {
}
