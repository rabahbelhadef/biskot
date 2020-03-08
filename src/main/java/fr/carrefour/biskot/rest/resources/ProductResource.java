package fr.carrefour.biskot.rest.resources;

import fr.carrefour.biskot.dto.Product;
import fr.carrefour.biskot.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductResource {


    @Autowired
    private ProductService productService ;

    @RequestMapping(value = "/{page}", method = GET)
    public List<Product> getProductList(@PathVariable(value = "page") Integer page){
        log.debug("REST request to get products by page , page = {} ", page);
        return productService.productListByPage(page) ;
    }
}
