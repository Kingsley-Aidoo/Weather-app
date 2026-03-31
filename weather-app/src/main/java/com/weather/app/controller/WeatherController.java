package com.weather.app.controller;

import com.weather.app.model.WeatherData;
import com.weather.app.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/")
    public String index() {
        return "weather-glass"; // matches weather-glass.html in templates/
    }

    @GetMapping("/api/weather/city")
    @ResponseBody
    public ResponseEntity<?> getWeatherByCity(@RequestParam String city) {
        try {
            WeatherData data = weatherService.getWeatherByCity(city);
            enrichResponse(data);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(errorMap("City not found or API error: " + e.getMessage()));
        }
    }

    @GetMapping("/api/weather/coords")
    @ResponseBody
    public ResponseEntity<?> getWeatherByCoords(
            @RequestParam double lat,
            @RequestParam double lon) {
        try {
            WeatherData data = weatherService.getWeatherByCoords(lat, lon);
            enrichResponse(data);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(errorMap("Location error: " + e.getMessage()));
        }
    }

    private void enrichResponse(WeatherData data) {
        // Add wind direction string
        data.setAnalysis(data.getAnalysis()); // already set in service
    }

    private Map<String, String> errorMap(String message) {
        Map<String, String> err = new HashMap<>();
        err.put("error", message);
        return err;
    }
}
