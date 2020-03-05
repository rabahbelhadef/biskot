package fr.carrefour.biskot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    EURO("€"),
    DOLLAR("$"),
    POUND("$");

    private String value ;
}
