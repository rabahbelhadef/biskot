package fr.carrefour.biskot.rest.resources;

import fr.carrefour.biskot.dto.AddProduct;
import fr.carrefour.biskot.dto.Cart;
import fr.carrefour.biskot.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Slf4j
public class CartResource {

    @Autowired
    private CartService cartService ;

    @RequestMapping("/{cartId}")
    public Cart getCart(@PathVariable("cartId") Long cartId){
        log.debug("REST request to get cart by id : ={} ", cartId);
        return cartService.getCartById(cartId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createCart(@RequestBody Cart cart){
        log.debug("REST request to create new cart {} ", cart);
        cartService.intCarte(cart) ;
    }

    @RequestMapping(value = "/{cartId}", method = RequestMethod.POST)
    public Cart addProductToCart(@RequestBody AddProduct addProduct, @PathVariable("cartId") Long cartId){
        log.debug("REST request to add product to cart, cartID = {}, productToAdd = ", cartId, addProduct);
        return cartService.addToCart(addProduct, cartId) ;
    }

}
