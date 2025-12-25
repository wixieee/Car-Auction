package edu.lpnu.auction.utils.validation;

import edu.lpnu.auction.dto.CarRequest;
import edu.lpnu.auction.model.enums.car.FuelType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CarConfigurationValidator implements ConstraintValidator<ValidCarConfiguration, CarRequest> {

    @Override
    public boolean isValid(CarRequest request, ConstraintValidatorContext context) {
        if (request.getFuelType() == null) return true;

        boolean isValid = true;
        context.disableDefaultConstraintViolation();
        if (isIce(request.getFuelType())) {
            if (request.getEngineVolume() == null) {
                addError(context, "engineVolume", "Об'єм двигуна обов'язковий для бензину/дизелю/гібриду");
                isValid = false;
            }
            if (request.getCylinderCount() == null) {
                addError(context, "cylinderCount", "Кількість циліндрів обов'язкова");
                isValid = false;
            }
        }

        if (request.getFuelType() == FuelType.ELECTRIC) {
            if (request.getBatteryCapacity() == null) {
                addError(context, "batteryCapacity", "Ємність батареї обов'язкова для електрокара");
                isValid = false;
            }
            if (request.getChargingPort() == null) {
                addError(context, "chargingPort", "Тип порту зарядки обов'язковий для електрокара");
                isValid = false;
            }
            if (request.getElectricRange() == null) {
                addError(context, "electricRange", "Вкажіть приблизний запас ходу");
                isValid = false;
            }
            if (request.getBatteryHealth() == null) {
                addError(context, "batteryHealth", "Вкажіть справність батареї у відсотках");
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean isIce(FuelType type) {
        return type == FuelType.GASOLINE || type == FuelType.DIESEL ||
                type == FuelType.HYBRID || type == FuelType.GAS ||
                type == FuelType.FLEX_FUEL;
    }

    private void addError(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}