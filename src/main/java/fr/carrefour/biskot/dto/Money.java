package fr.carrefour.biskot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Money {

    private Float amount ;
    private Currency currency;
}
