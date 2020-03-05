package fr.carrefour.biskot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Stock {

    @JsonProperty("product_id")
    private Long productId ;
    @JsonProperty("quantity_available")
    private Integer quantityAvailable ;

}
