package com.example.msrestoran.controller;

import com.example.msrestoran.dto.request.RestoranRequest;
import com.example.msrestoran.dto.response.RestoranResponse;
import com.example.msrestoran.service.abstraction.RestoranService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/restorans")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RestoranController {
    RestoranService service;

    @PostMapping
    public RestoranResponse save(@RequestBody RestoranRequest request) {
        return service.save(request);
    }

    @GetMapping("/{id}")
    public RestoranResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<RestoranResponse> findAll() {
        return service.findAll();
    }
}
