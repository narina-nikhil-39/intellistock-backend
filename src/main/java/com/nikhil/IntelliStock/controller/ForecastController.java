package com.nikhil.IntelliStock.controller;

import com.nikhil.IntelliStock.dto.DemandForecastDTO;
import com.nikhil.IntelliStock.service.ForecastService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping
    public ResponseEntity<List<DemandForecastDTO>> getAllForecasts() {
        return ResponseEntity.ok(forecastService.getAllForecasts());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<DemandForecastDTO> getProductForecast(@PathVariable Long productId) {
        return ResponseEntity.ok(forecastService.calculateForecast(productId));
    }
}
