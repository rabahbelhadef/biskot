package fr.carrefour.biskot.feignclient;

import fr.carrefour.biskot.dto.Currency;
import fr.carrefour.biskot.dto.Money;
import fr.carrefour.biskot.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * Simulate rest client for provide list of products.
 */
@Component
public class ProductReferentiel {

    @Value("classpath:data/products.csv")
    private String productsPath ;

    private Map<Long,Product> allProducts ;


    @PostConstruct
    public void loadStock() throws IOException {
        final File prodcutsFile = ResourceUtils.getFile(productsPath);
        allProducts = Files.lines(prodcutsFile.toPath())
                .skip(1)
                .map(line -> line.split(";"))
                .map(line -> Product.builder()
                        .id(Long.valueOf(line[0]))
                        .label(line[1])
                        .price(new Money(Float.valueOf(line[2]), Currency.valueOf(line[3])))
                        .weightInKg(Float.valueOf(line[4]))
                        .build())
                .collect(toMap(Product::getId, identity()));
    }

    /**
     * Simulate rest resource /stock/{productId}
     */
    public Product getProductById(Long productId){
        return allProducts.get(productId) ;
    }

    public List<Product> getProductsByPage(Integer limit, Integer offset) {
        return allProducts.values().stream().skip(offset).limit(limit).collect(toList());
    }
}
