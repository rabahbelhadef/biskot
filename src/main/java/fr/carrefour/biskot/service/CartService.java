package fr.carrefour.biskot.service;

import fr.carrefour.biskot.cache.CartCache;
import fr.carrefour.biskot.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    ProductService productService ;

    @Autowired
    private CurrenceyConverter currenceyConverter ;

    @Autowired
    private CartCache cartCache ;



    public Cart intCarte(Cart cart){
        return cartCache.saveCart(cart) ;
    }

    public Cart getCartById(Long cartId) {
        return cartCache.getCart(cartId);
    }

    public Cart addToCart(AddProduct addProduct, Long cartId){
        Cart cart = getCartById(cartId) ;
        cart.getProducts().add(addProduct) ;
        processAndValidatePrice(cart) ;
        return cartCache.saveCart(cart) ;
    }

    private void processAndValidatePrice(final Cart cart) {
        Currency localCurrency = currenceyConverter.getLocalCurrency() ;
        float amount = (float) cart.getProducts().stream()
                .map(this::processPrice)
                .mapToDouble(i -> i).sum();
        cart.setTotalPrice(new Money(amount, localCurrency));
    }

    private Float processPrice(AddProduct addProduct) {
        Product product = productService.getProductById(addProduct.getProductId());
        Float unitlocalProductPrice = currenceyConverter.toLocalPrice(product.getPrice());;
        return addProduct.getQuantity() * unitlocalProductPrice;
    }
}
