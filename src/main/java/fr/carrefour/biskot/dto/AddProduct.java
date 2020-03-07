package fr.carrefour.biskot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddProduct {

    @JsonProperty("product_id")
    private Long productId ;
    private Integer quantity ;

}
