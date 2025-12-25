package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.response.NhtsaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@Slf4j
public class NhtsaService {
    private final RestClient restClient;

    public NhtsaService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://vpic.nhtsa.dot.gov/api/")
                .build();
    }

    public Optional<NhtsaResponse.NhtsaVinResult> decodeVin(String vin){
        try{
            NhtsaResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("vehicles/DecodeVinValues/{vin}")
                            .queryParam("format", "json")
                            .build(vin))
                    .retrieve()
                    .body(NhtsaResponse.class);

            if(response != null && response.getResults() != null && !response.getResults().isEmpty()){
                return Optional.of(response.getResults().get(0));
            }
        } catch(Exception e){
            log.error("Помилка при декодуванні VIN {}: {}", vin, e.getMessage());
        }

        return Optional.empty();
    }
}
