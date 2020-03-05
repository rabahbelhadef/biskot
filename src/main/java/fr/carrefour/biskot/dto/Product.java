package fr.carrefour.biskot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {

    private Long id ;
    private String label ;
    private Money price ;
    @JsonProperty("weight_in_kg")
    private Float weightInKg ;

}
