package fr.carrefour.biskot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Stock {

    @JsonProperty("product_id")
    private Long productId ;
    @JsonProperty("quantity_available")
    private Integer quantityAvailable ;

}
