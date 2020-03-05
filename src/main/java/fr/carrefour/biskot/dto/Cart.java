package fr.carrefour.biskot.dto;

import lombok.Data;

import java.util.List;

@Data
public class Cart {

    private Long id ;
    private List<AddProduct> products ;
    private Money totalPrice ;

}
