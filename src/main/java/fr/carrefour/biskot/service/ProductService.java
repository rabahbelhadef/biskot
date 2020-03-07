package fr.carrefour.biskot.service;

import fr.carrefour.biskot.dto.Product;
import fr.carrefour.biskot.feignclient.ProductReferentiel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Value("${product.page.size}")
    private Integer productPageSize ;

    @Autowired
    private ProductReferentiel productReferentiel ;

    public Product getProductById(Long productId){
        return productReferentiel.getProductById(productId);
    }


    public List<Product> productListByPage(Integer page){
        return productReferentiel.getProductsByPage(productPageSize, (page -1 ) * productPageSize) ;
    }
}
