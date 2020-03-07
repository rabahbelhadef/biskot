package fr.carrefour.biskot.cache;


import fr.carrefour.biskot.dto.Cart;
import fr.carrefour.biskot.dto.Currency;
import fr.carrefour.biskot.dto.Money;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static fr.carrefour.biskot.dto.Currency.EURO;

/**
 * Simulate distributed cache,
 * this class should be replaced by an Redis ou hazelcast.
 */
@Component
public class CartCache {

    private final AtomicLong cartIdGenerator = new AtomicLong(0);

    private Map<Long, Cart> allCarts = new HashMap<>() ;

    public Cart initCart(Cart cart){
        cart.setId(cartIdGenerator.incrementAndGet());
        cart.setTotalPrice(new Money(0f, EURO));
        allCarts.put(cart.getId(), cart);
        return cart;
    }

    public Cart saveCart(Cart cart){
        if (cart.getId() == null){
            cart.setId(cartIdGenerator.incrementAndGet());
        }
        allCarts.compute(cart.getId(), (cartId, oldCart) -> cart) ;
        return cart ;
    }

    public Cart getCart(Long cartId){
        return allCarts.get(cartId) ;
    }


}
