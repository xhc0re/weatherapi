package io.codenamite.weatherapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long openWeatherId;
    @Column
    private String name;
    @Column
    private Double temperature;
    @Column
    private LocalDateTime lastUpdated;

    public City(Long id, String name, Double temperature, LocalDateTime lastUpdated) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.lastUpdated = lastUpdated;
    }

    public City() {};

    public Long getOpenWeatherId() {
        return openWeatherId;
    }

    public void setOpenWeatherId(Long openWeatherId) {
        this.openWeatherId = openWeatherId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getTemperature() {
        return temperature;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}

