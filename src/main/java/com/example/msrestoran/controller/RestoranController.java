package com.example.msrestoran.controller;

import com.example.msrestoran.dto.request.RestoranRequest;
import com.example.msrestoran.dto.response.RestoranResponse;
import com.example.msrestoran.service.abstraction.RestoranService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/restorans")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RestoranController {
    RestoranService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public RestoranResponse save(@RequestBody RestoranRequest request) {
        return service.save(request);
    }

    @GetMapping("/{id}")
    public RestoranResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public Set<RestoranResponse> findAll() {
        return service.findAll();
    }
}
