package edu.lpnu.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarAuctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarAuctionApplication.class, args);
	}

}
