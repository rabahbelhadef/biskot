package fr.carrefour.biskot.service;

import fr.carrefour.biskot.BusinessException;
import fr.carrefour.biskot.cache.CartCache;
import fr.carrefour.biskot.dto.*;
import fr.carrefour.biskot.feignclient.StockClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private ProductService productService ;

    @Autowired
    private CartCache cartCache ;

    @Autowired
    private StockClient stockClient ;

    private Integer maxCartAount  = 100;

    final int maxProductNumber = 5;


    public Cart intCarte(Cart cart){
        return cartCache.saveCart(cart) ;
    }

    public Cart getCartById(Long cartId) {
        return cartCache.getCart(cartId);
    }

    public Cart addToCart(AddProduct addProduct, Long cartId){
        Cart cart = getCartById(cartId) ;
        validateCartAndAppendProduct(cart, addProduct) ;
        return cartCache.saveCart(cart) ;
    }

    private void validateCartAndAppendProduct(final Cart cart, AddProduct addProduct) {
        validateTotalPrice(cart);
        validateProductNumber(cart);
        validateAddProductweight(cart, addProduct);
    }


    private void validateAddProductweight(Cart cart, AddProduct addProduct) {

        Product product = productService.getProductById(addProduct.getProductId());

        validateProductWeight(addProduct, product);

        appendProducAddReprocessPrice(cart, addProduct, product);

        validateTotalPrice(cart);

        validateQuantityAvailable(addProduct, product);

    }

    private void validateProductNumber(Cart cart) {
        if (cart.getProducts().size()  >= maxProductNumber) {
            throw new BusinessException("Le panier ne peut pas contenir plus de %s produits", maxProductNumber);
        }
    }

    private void validateTotalPrice(Cart initialCart) {
        if (initialCart.getTotalPrice().getAmount() >= maxCartAount) {
            throw new BusinessException("Le montant total du panier ne doit pas dépasser %d euros", maxCartAount);
        }
    }

    private void appendProducAddReprocessPrice(Cart cart, AddProduct addProduct, Product product) {
        float newPrice = cart.getTotalPrice().getAmount() +  addProduct.getQuantity() * product.getPrice().getAmount();
        cart.setTotalPrice(new Money(newPrice, Currency.EURO));
        cart.getProducts().add(addProduct) ;
    }

    private void validateProductWeight(AddProduct addProduct, Product product) {
        if (addProduct.getQuantity() * product.getWeightInKg() > 3) {
            throw new BusinessException("Le poids pour chaque produit dans le panier ne doit pas excéder 3 kg");
        }
    }

    private void validateQuantityAvailable(AddProduct addProduct, Product product) {
        final Integer quantityAvailable = stockClient.getStock(product.getId()).getQuantityAvailable();
        if (addProduct.getQuantity() > quantityAvailable) {
            throw new BusinessException("La quantité de chaque produit ajoutée dans le panier ne doit pas dépasser la quantité disponible en stock");
        }
    }

}
