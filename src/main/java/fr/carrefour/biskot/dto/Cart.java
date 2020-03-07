package fr.carrefour.biskot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private Long id ;

    private List<AddProduct> products ;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Money totalPrice ;

    public List<AddProduct> getProducts() {
        if (products == null){
            products = new ArrayList<>();
        }
        return products ;
    }
}
