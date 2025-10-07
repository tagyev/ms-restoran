package com.example.msrestoran.service.abstraction;

import com.example.msrestoran.dto.request.RestoranRequest;
import com.example.msrestoran.dto.response.RestoranResponse;

import java.util.List;
import java.util.Set;

public interface RestoranService {
    RestoranResponse save (RestoranRequest request);
    RestoranResponse findById(Long id);
    List<RestoranResponse> findAll();

}
