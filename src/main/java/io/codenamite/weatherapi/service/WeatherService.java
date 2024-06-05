package io.codenamite.weatherapi.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.codenamite.weatherapi.model.City;
import io.codenamite.weatherapi.model.WeatherResponse;
import io.codenamite.weatherapi.repository.CityRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final CityRepository cityRepository;
    private final String apiKey;
    private final RestTemplate restTemplate;
    private final int threadPoolSize;

    public WeatherService(CityRepository cityRepository, @Value("${weather.api.key}") String apiKey, @Value("${weather.executor.thread-pool-size}") int threadPoolSize) {
        this.cityRepository = cityRepository;
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        this.threadPoolSize = threadPoolSize;
    }

    @PostConstruct
    public void loadInitialData() {
        ExecutorService loadExecutorService = Executors.newFixedThreadPool(threadPoolSize);
        cityRepository.deleteAll();
        try {
            Resource resource = new ClassPathResource("city_list.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()));
            List<String[]> records = reader.readAll();
            logger.info("Loaded {} records from CSV", records.size());

            for (String[] record : records) {
                loadExecutorService.submit(() -> {
                    String cityName = record[1];
                    Long openWeatherId = Long.parseLong(record[0]);
                    City city = new City();
                    city.setName(cityName);
                    city.setOpenWeatherId(openWeatherId);
                    cityRepository.save(city);
                    logger.info("Saved city: {}", cityName);
                });
            }

        } catch (IOException | CsvException e) {
            logger.error("Error reading CSV file", e);
        }

        loadExecutorService.shutdown();
        try {
            boolean terminated = loadExecutorService.awaitTermination(1, TimeUnit.HOURS); // Dostosuj czas oczekiwania w zależności od potrzeb
            if (!terminated) {
                logger.warn("Load Executor service did not terminate within the specified time.");
            }
        } catch (InterruptedException e) {
            logger.error("Load Executor service interrupted", e);
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }

        // Now we can update temperatures after the cities have been loaded
        updateCityTemperatures();
    }

    public void updateCityTemperatures() {
        List<City> cities = cityRepository.findAll();
        ExecutorService updateExecutorService = Executors.newFixedThreadPool(threadPoolSize);

        for (City city : cities) {
            String url = "https://api.openweathermap.org/data/2.5/weather?id=" + city.getOpenWeatherId() + "&units=metric&appid=" + apiKey;
            updateExecutorService.submit(() -> {
                try {
                    Optional<WeatherResponse> response = Optional.ofNullable(restTemplate.getForObject(url, WeatherResponse.class));
                    response.ifPresent(weatherResponse -> {
                        Double temperatureInCelsius = weatherResponse.getMain().getTemp();
                        city.setTemperature(temperatureInCelsius);
                        city.setLastUpdated(LocalDateTime.now());
                        cityRepository.save(city);
                        logger.info("Updated temperature for city: {} to {}", city.getName(), temperatureInCelsius);
                    });
                } catch (Exception e) {
                    logger.error("Error fetching temperature for city: {}", city.getName(), e);
                }
            });
        }

        updateExecutorService.shutdown();
        try {
            boolean terminated = updateExecutorService.awaitTermination(1, TimeUnit.HOURS); // Dostosuj czas oczekiwania w zależności od potrzeb
            if (!terminated) {
                logger.warn("Update Executor service did not terminate within the specified time.");
            }
        } catch (InterruptedException e) {
            logger.error("Update Executor service interrupted", e);
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
    }

    public Double getTemperatureByIdInCelsius(Long id) {
        City city = cityRepository.findByOpenWeatherId(id).orElseThrow(() -> {
            logger.warn("City not found with id: {}", id);
            return new RuntimeException("City not found");
        });
        return city.getTemperature();
    }

    public Double getTemperatureByIdInFahrenheit(Long id) {
        City city = cityRepository.findByOpenWeatherId(id).orElseThrow(() -> {
            logger.warn("City not found with id: {}", id);
            return new RuntimeException("City not found");
        });
        return celsiusToFahrenheit(city.getTemperature());
    }

    public Double getTemperatureByNameInCelsius(String name) {
        City city = cityRepository.findByName(name).orElseThrow(() -> {
            logger.warn("City not found with name: {}", name);
            return new RuntimeException("City not found");
        });
        return city.getTemperature();
    }

    public Double getTemperatureByNameInFahrenheit(String name) {
        City city = cityRepository.findByName(name).orElseThrow(() -> {
            logger.warn("City not found with id: {}", name);
            return new RuntimeException("City not found");
        });
        return celsiusToFahrenheit(city.getTemperature());
    }

    public double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }
}