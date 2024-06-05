package io.codenamite.weatherapi.component;

import io.codenamite.weatherapi.service.WeatherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CityDataInitializer implements CommandLineRunner {
    private final WeatherService weatherService;

    public CityDataInitializer(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void run(String... args) {
        weatherService.loadInitialData();
    }
}
