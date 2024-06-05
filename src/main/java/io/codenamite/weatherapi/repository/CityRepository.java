package io.codenamite.weatherapi.repository;

import io.codenamite.weatherapi.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByName(String name);
    Optional<City> findByOpenWeatherId(Long openWeatherId);
}
