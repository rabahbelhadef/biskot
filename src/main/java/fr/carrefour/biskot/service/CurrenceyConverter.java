package fr.carrefour.biskot.service;

import fr.carrefour.biskot.dto.Currency;
import fr.carrefour.biskot.dto.Money;
import org.springframework.stereotype.Component;

@Component
public class CurrenceyConverter {

    private static Currency localCurrency = Currency.EURO ;

    public Float toLocalPrice(Money money){
        return money.getAmount() ;
    }

    public Currency getLocalCurrency() {
        return localCurrency;
    }
}
