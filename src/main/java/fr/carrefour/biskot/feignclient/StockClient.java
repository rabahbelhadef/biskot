package fr.carrefour.biskot.feignclient;

import fr.carrefour.biskot.dto.Stock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

/**
 * Simulate rest client for provide stocks.
 */
@Component
public class StockClient {

    @Value("classpath:data/stock.csv")
    private String stockPath ;

    private ConcurrentMap <Long,Stock> allStock ;


    @PostConstruct
    public void loadStock() throws IOException {
        final File stockFile = ResourceUtils.getFile(stockPath);
        allStock = Files.lines(stockFile.toPath())
                .skip(1)
                .map(line -> line.split(";"))
                .map(line -> Stock.builder()
                        .productId(Long.valueOf(line[0]))
                        .quantityAvailable(Integer.valueOf(line[1]))
                        .build())
                .collect(Collectors.toConcurrentMap(Stock::getProductId, identity()));
    }

    /**
     * Simulate rest resource /stock/{productId}
     */
    public Stock getStock(Long productId){
        return allStock.get(productId) ;
    }

}
