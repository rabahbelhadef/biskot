package fr.carrefour.biskot.service;

import fr.carrefour.biskot.cache.CartCache;
import fr.carrefour.biskot.dto.AddProduct;
import fr.carrefour.biskot.dto.Cart;
import fr.carrefour.biskot.dto.Money;
import fr.carrefour.biskot.dto.Product;
import fr.carrefour.biskot.exception.BusinessException;
import fr.carrefour.biskot.exception.DataNotFoundException;
import fr.carrefour.biskot.feignclient.StockClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static fr.carrefour.biskot.dto.Currency.EURO;
import static java.util.Optional.ofNullable;

@Service
public class CartService {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartCache cartCache;

    @Autowired
    private StockClient stockClient;

    private Integer maxCartAount = 100;

    final int maxProductNumber = 5;


    public Cart intCarte(Cart cart) {
        return cartCache.initCart(cart);
    }

    public Cart getCartById(Long cartId) {
        return ofNullable(cartCache.getCart(cartId))
                .orElseThrow(() -> new DataNotFoundException("Panier non trouvé"));
    }

    public Cart addToCart(AddProduct addProduct, Long cartId) {
        Assert.notNull(addProduct.getProductId(), "Produit à rajouter dans le panier est obligatoire");
        Assert.notNull(cartId, "Identifiant du panier est obligatoire");

        Cart cart = getCartById(cartId);
        Cart cartToSave = validateCartAndAppendProduct(cart, addProduct);

        return cartCache.saveCart(cartToSave);
    }

    private Cart validateCartAndAppendProduct(final Cart cart, AddProduct addProduct) {

        validateTotalPrice(cart);
        validateProductNumber(cart, addProduct.getProductId());
        return validateAddProductweight(cart, addProduct);
    }


    private Cart validateAddProductweight(Cart cart, AddProduct addProduct) {

        Product product = productService.getProductById(addProduct.getProductId());

        AddProduct updatetedAddProduct = getUpdateAddProduct(cart, addProduct);
        validateProductWeight(updatetedAddProduct, product);

        Cart cartToSave = appendProducAddReprocessPrice(cart, updatetedAddProduct, product.getPrice().getAmount(), addProduct.getQuantity());

        validateTotalPrice(cartToSave);

        validateQuantityAvailable(addProduct, product);

        return cartToSave;


    }

    private void validateProductNumber(Cart cart, Long productId) {
        boolean newProductToAdd = cart.getProducts().stream().map(AddProduct::getProductId).noneMatch(productId::equals);
        if (newProductToAdd && cart.getProducts().size() >= maxProductNumber) {
            throw new BusinessException("Le panier ne peut pas contenir plus de %s produits", maxProductNumber);
        }
    }

    private void validateTotalPrice(Cart initialCart) {
        if (initialCart.getTotalPrice().getAmount() >= maxCartAount) {
            throw new BusinessException("Le montant total du panier ne doit pas dépasser %d euros", maxCartAount);
        }
    }

    private Cart appendProducAddReprocessPrice(Cart cart, AddProduct updatedAddProduct, Float productPrice, Integer quantityToAdd) {

        float newPrice = cart.getTotalPrice().getAmount() + quantityToAdd * productPrice;
        cart.setTotalPrice(new Money(newPrice, EURO));
        final List<AddProduct> newProducts = new ArrayList<>();
        for (int i = 0; i < cart.getProducts().size(); i++) {
            AddProduct p = cart.getProducts().get(i);
            if (p.getProductId().equals(updatedAddProduct.getProductId())) {
                newProducts.add(updatedAddProduct);
            } else {
                newProducts.add(p);
            }
        }

        cart.getProducts().add(updatedAddProduct);
        return Cart.builder()
                .id(cart.getId())
                .products(newProducts)
                .totalPrice(new Money(newPrice, EURO))
                .build();
    }

    private void validateProductWeight(AddProduct updateAddProduct, Product product) {
        if (updateAddProduct.getQuantity() * product.getWeightInKg() > 3) {
            throw new BusinessException("Le poids pour chaque produit dans le panier ne doit pas excéder 3 kg");
        }
    }

    private AddProduct getUpdateAddProduct(Cart cart, AddProduct addProduct) {
        return cart.getProducts().stream().filter(p -> p.getProductId()
                .equals(addProduct.getProductId()))
                .map(p -> new AddProduct(p.getProductId(), p.getQuantity() + addProduct.getQuantity()))
                .findFirst().orElse(addProduct);
    }

    private void validateQuantityAvailable(AddProduct addProduct, Product product) {
        final Integer quantityAvailable = stockClient.getStock(product.getId()).getQuantityAvailable();
        if (addProduct.getQuantity() > quantityAvailable) {
            throw new BusinessException("La quantité de chaque produit ajoutée dans le panier ne doit pas dépasser la quantité disponible en stock");
        }
    }

}
