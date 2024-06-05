package io.codenamite.weatherapi.controller;

import io.codenamite.weatherapi.model.City;
import io.codenamite.weatherapi.repository.CityRepository;
import io.codenamite.weatherapi.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cities")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final CityRepository cityRepository;
    private final WeatherService weatherService;

    public WeatherController(CityRepository cityRepository, WeatherService weatherService) {
        this.cityRepository = cityRepository;
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<Page<City>> getCities(Pageable pageable) {
        logger.info("Fetching cities with paging");
        Page<City> cities = cityRepository.findAll(pageable);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}/celsius")
    public ResponseEntity<Double> getTemperatureByIdInCelsius(@PathVariable Long id) {
        logger.info("Fetching temperature in Celsius for city with id: {}", id);
        Double temperature = weatherService.getTemperatureByIdInCelsius(id);
        return ResponseEntity.ok(temperature);
    }

    @GetMapping("/{id}/fahrenheit")
    public ResponseEntity<Double> getTemperatureByIdInFahrenheit(@PathVariable Long id) {
        logger.info("Fetching temperature in Fahrenheit for city with id: {}", id);
        Double temperature = weatherService.getTemperatureByIdInFahrenheit(id);
        return ResponseEntity.ok(temperature);
    }

    @GetMapping("/name/{name}/celsius")
    public ResponseEntity<Double> getTemperatureByNameInCelsius(@PathVariable String name) {
        logger.info("Fetching temperature in Celsius for city with name: {}", name);
        Double temperature = weatherService.getTemperatureByNameInCelsius(name);
        return ResponseEntity.ok(temperature);
    }

    @GetMapping("/name/{name}/fahrenheit")
    public ResponseEntity<Double> getTemperatureByNameInFahrenheit(@PathVariable String name) {
        logger.info("Fetching temperature in Fahrenheit for city with name: {}", name);
        Double temperature = weatherService.getTemperatureByNameInFahrenheit(name);
        return ResponseEntity.ok(temperature);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllCities() {
        logger.info("Deleting all cities");
        cityRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateWeatherData() {
        logger.info("Updating weather data for all cities");
        weatherService.updateCityTemperatures();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fetch")
    public ResponseEntity<Void> fetchCitiesFromFile() {
        logger.info("Regenerating Cities from file");
        weatherService.loadInitialData();
        return ResponseEntity.ok().build();
    }
}
