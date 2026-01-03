package edu.lpnu.auction.utils;

import edu.lpnu.auction.dto.request.LotFilterRequest;
import edu.lpnu.auction.model.Car;
import edu.lpnu.auction.model.Lot;
import edu.lpnu.auction.model.enums.LotStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LotSpecification {

    private static final List<LotStatus> PUBLIC_STATUSES = List.of(
            LotStatus.ACTIVE,
            LotStatus.APPROVED,
            LotStatus.SOLD
    );

    public static Specification<Lot> getSpec(LotFilterRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getStatus() != null) {

                if (PUBLIC_STATUSES.contains(request.getStatus())) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
                } else {
                    return criteriaBuilder.disjunction();
                }

            } else {
                predicates.add(root.get("status").in(PUBLIC_STATUSES));
            }

            Join<Lot, Car> car = root.join("car");

            if (request.getMake() != null && !request.getMake().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(car.get("make")),
                        "%" + request.getMake().toLowerCase() + "%"
                ));
            }
            if (request.getModel() != null && !request.getModel().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(car.get("model")),
                        "%" + request.getModel().toLowerCase() + "%"
                ));
            }

            if (request.getPriceMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("currentPrice"), request.getPriceMin()));
            }
            if (request.getPriceMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("currentPrice"), request.getPriceMax()));
            }

            if (request.getYearFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(car.get("year"), request.getYearFrom()));
            }
            if (request.getYearTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(car.get("year"), request.getYearTo()));
            }

            if (request.getMileageMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(car.get("mileage"), request.getMileageMin()));
            }
            if (request.getMileageMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(car.get("mileage"), request.getMileageMax()));
            }

            if (request.getEngineVolumeMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(car.get("engineVolume"), request.getEngineVolumeMin()));
            }
            if (request.getEngineVolumeMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(car.get("engineVolume"), request.getEngineVolumeMax()));
            }

            if (request.getHorsePowerMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(car.get("horsePower"), request.getHorsePowerMin()));
            }
            if (request.getHorsePowerMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(car.get("horsePower"), request.getHorsePowerMax()));
            }

            if (request.getBodyType() != null) {
                predicates.add(criteriaBuilder.equal(car.get("bodyType"), request.getBodyType()));
            }
            if (request.getFuelType() != null) {
                predicates.add(criteriaBuilder.equal(car.get("fuelType"), request.getFuelType()));
            }
            if (request.getTransmission() != null) {
                predicates.add(criteriaBuilder.equal(car.get("transmission"), request.getTransmission()));
            }
            if (request.getDriveType() != null) {
                predicates.add(criteriaBuilder.equal(car.get("driveType"), request.getDriveType()));
            }
            if (request.getColor() != null) {
                predicates.add(criteriaBuilder.equal(car.get("color"), request.getColor()));
            }
            if (request.getCondition() != null) {
                predicates.add(criteriaBuilder.equal(car.get("condition"), request.getCondition()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}