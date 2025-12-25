package edu.lpnu.auction.model.enums.car;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public enum BodyType {
    SEDAN("Седан", "Sedan/Saloon", "Limousine"),

    SUV("Позашляховик / Кросовер",
            "Sport Utility Vehicle (SUV)/Multi-Purpose Vehicle (MPV)",
            "Crossover Utility Vehicle (CUV)"),

    PICKUP("Пікап", "Pickup", "Sport Utility Truck (SUT)"),

    HATCHBACK("Хетчбек", "Hatchback/Liftback/Notchback"),

    COUPE("Купе / Кабріолет", "Coupe", "Convertible/Cabriolet", "Roadster"),

    WAGON("Універсал", "Wagon"),

    MINIVAN("Мінівен", "Minivan"),

    VAN("Фургон / Бус", "Van", "Cargo Van", "Step Van / Walk-in Van"),

    BUS("Автобус",
            "Bus", "Bus - School Bus", "Incomplete - Bus Chassis",
            "Incomplete - School Bus Chassis", "Incomplete - Shuttle Bus Chassis",
            "Incomplete - Transit Bus Chassis", "Incomplete - Commercial Bus Chassis",
            "Incomplete - Motor Coach Chassis"),

    TRUCK("Вантажівка",
            "Truck", "Truck-Tractor", "Street Sweeper", "Fire Apparatus", "Refuse Truck",
            "Incomplete - Chassis Cab (Number of Cab Unknown)",
            "Incomplete - Chassis Cab (Single Cab)",
            "Incomplete - Chassis Cab (Double Cab)"),

    MOTORCYCLE("Мотоцикл / ATV",
            "Motorcycle - Sport", "Motorcycle - Street", "Motorcycle - Cruiser",
            "Motorcycle - Scooter", "Motorcycle - Touring / Sport Touring",
            "Motorcycle - Standard", "Motorcycle - Custom",
            "Motorcycle - Dual Sport / Adventure / Supermoto / On/Off-road",
            "Motorcycle - Small / Minibike", "Motorcycle - Moped",
            "Motorcycle - Side Car", "Motorcycle - Competition",
            "Motorcycle - Cross Country", "Motorcycle - Unknown Body Class",
            "Motorcycle - Three Wheeled...", "Motorcycle - Enclosed...",
            "Off-road Vehicle - All Terrain Vehicle (ATV) (Motorcycle-style)",
            "Off-road Vehicle - Dirt Bike / Off-Road"),

    OTHER("Інше / Спецтехніка",
            "Trailer", "Incomplete - Trailer Chassis",
            "Motorhome", "Incomplete - Motor Home Chassis",
            "Ambulance",
            "Off-road Vehicle - Golf Cart", "Off-road Vehicle - Go Kart",
            "Off-road Vehicle - Snowmobile",
            "Off-road Vehicle - Farm Equipment", "Off-road Vehicle - Construction Equipment",
            "Low Speed Vehicle (LSV) / Neighborhood Electric Vehicle (NEV)",
            "Streetcar / Trolley", "Incomplete", "Incomplete - Glider"),

    UNKNOWN("Невідомо", "Unknown");

    private final String ukrainianLabel;
    private final List<String> nhtsaValues;

    BodyType(String ukrainianLabel, String... nhtsaValues) {
        this.ukrainianLabel = ukrainianLabel;
        this.nhtsaValues = Arrays.asList(nhtsaValues);
    }

    public static BodyType fromString(String text) {
        if (text == null || text.isBlank()) return UNKNOWN;
        return Arrays.stream(BodyType.values())
                .filter(t -> t.nhtsaValues.stream().anyMatch(text::equalsIgnoreCase))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
