package edu.lpnu.auction.repository;

import edu.lpnu.auction.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID>{
    boolean existsByVin(String vin);
}