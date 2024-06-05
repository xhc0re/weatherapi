package io.codenamite.weatherapi.component;


import io.codenamite.weatherapi.service.WeatherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final WeatherService weatherService;

    public ScheduledTasks(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleWeatherUpdate() {
        weatherService.updateCityTemperatures();
    }
}
