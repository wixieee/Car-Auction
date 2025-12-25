package edu.lpnu.auction.factory;

import edu.lpnu.auction.model.enums.car.*;

import java.util.UUID;

public class TestConstants {
    public static final UUID DEFAULT_UUID = UUID.fromString("9ae98c86-23a0-4656-aacc-a5c6e0b00808");
    public static final String DEFAULT_EMAIL = "email@email.com";
    public static final String DEFAULT_PASSWORD = "password";
    public static final String DEFAULT_HASHED_PASSWORD = "hashedPassword";
    public static final String DEFAULT_FIRSTNAME = "firstName";
    public static final String DEFAULT_LASTNAME = "lastName";
    public static final String DEFAULT_JWT = "jwtToken";
    public static final String WRONG_SECRET = "0bad8112fec0d8614569589356d03b8fa9215ddbc0538d4741c273328a9f2d7b";
    public static final String CORRECT_SECRET = "fb540b54a5b09d123a626e75f957eb34d05dff98e79eee00e7fff550b4c422a3";
    public static final int EXPIRATION_TIME = 3600000;
    public static final String DEFAULT_VIN = "1FTEW1CP5KFA12345";
    public static final String DEFAULT_MAKE = "Toyota";
    public static final String DEFAULT_MODEL = "Camry";
    public static final Integer DEFAULT_YEAR = 2022;
    public static final String DEFAULT_TRIM = "LE";
    public static final Integer DEFAULT_MILEAGE = 15000;
    public static final String DEFAULT_DESCRIPTION = "Excellent condition";
    public static final Double DEFAULT_ENGINE_VOLUME = 2.5;
    public static final Integer DEFAULT_HORSE_POWER = 203;
    public static final Integer DEFAULT_CYLINDERS = 4;
    public static final BodyType DEFAULT_BODY_TYPE = BodyType.SEDAN;
    public static final FuelType DEFAULT_FUEL_TYPE = FuelType.GASOLINE;
    public static final TransmissionType DEFAULT_TRANSMISSION = TransmissionType.AUTOMATIC;
    public static final DriveType DEFAULT_DRIVE_TYPE = DriveType.FWD;
    public static final Color DEFAULT_COLOR = Color.BLACK;
    public static final CarCondition DEFAULT_CONDITION = CarCondition.USED;
    public static final String NHTSA_YEAR_STR = "2022";
    public static final String NHTSA_DISPLACEMENT_STR = "2.5";
    public static final String NHTSA_HP_STR = "203";
    public static final String NHTSA_CYLINDERS_STR = "4";
    public static final String NHTSA_BODY_STR = "Sedan/Saloon";
    public static final String NHTSA_FUEL_STR = "Gasoline";
    public static final String NHTSA_TRANSMISSION_STR = "Automatic";
    public static final String NHTSA_DRIVE_STR = "FWD/Front-Wheel Drive";
}
