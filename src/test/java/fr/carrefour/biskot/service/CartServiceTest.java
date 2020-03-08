package fr.carrefour.biskot.service;

import fr.carrefour.biskot.cache.CartCache;
import fr.carrefour.biskot.dto.*;
import fr.carrefour.biskot.exception.BusinessException;
import fr.carrefour.biskot.exception.DataNotFoundException;
import fr.carrefour.biskot.feignclient.StockClient;
import fr.carrefour.biskot.test.utils.TestResult;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static fr.carrefour.biskot.dto.Currency.EURO;
import static fr.carrefour.biskot.test.utils.TryUtils.tryToExecute;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class CartServiceTest {

    @InjectMocks
    private CartService cartService  ;

    @Mock
    private ProductService productService ;

    @Spy
    private CurrenceyConverter currenceyConverter ;

    @Mock
    private CartCache cartCache ;

    @Mock
    private StockClient stockClient ;

    @Test
    void should_add_product_to_cart() {
        // Given
        Long cartId = 1L;
        Long productId = 10L ;
        final List<AddProduct> products = LongStream.range(0, 4).mapToObj(i -> new AddProduct(i, 1)).collect(toList());
        Cart initialCart = Cart.builder()
                .totalPrice(new Money(93f, EURO))
                .products(products)
                .build();

        when(cartCache.getCart(cartId)).thenReturn(initialCart) ;
        when(currenceyConverter.getLocalCurrency()).thenReturn(EURO) ;
        when(productService.getProductById(productId)).thenReturn(Product.builder().id(productId).weightInKg(1f).price(new Money(1f, EURO)).build()) ;
        when(stockClient.getStock(productId)).thenReturn(Stock.builder().productId(productId).quantityAvailable(3).build()) ;
        //When
        cartService.addToCart(new AddProduct(productId, 3), cartId);


        // then

        verify(cartCache).getCart(cartId);
        verify(cartCache).saveCart(Mockito.any(Cart.class)) ;

        verify(productService).getProductById(productId);
        verify(stockClient).getStock(productId);
    }


    @Test
    public void should_add_product_several_times(){
        // Given
        Long cartId = 1L;
        Long productId = 10L ;
        List<AddProduct> products = new ArrayList<>();
        products.add(new AddProduct(productId, 1));
        Cart initialCart = Cart.builder()
                .totalPrice(new Money(0f, EURO))
                .products(products)
                .build();

        when(cartCache.getCart(cartId)).thenReturn(initialCart) ;
        when(currenceyConverter.getLocalCurrency()).thenReturn(EURO) ;

        when(productService.getProductById(productId)).thenReturn(Product.builder().id(productId).weightInKg(0.5f).price(new Money(1f, EURO)).build()) ;
        when(stockClient.getStock(productId)).thenReturn(Stock.builder().productId(productId).quantityAvailable(5).build()) ;
        //When

        cartService.addToCart(new AddProduct(productId, 1), cartId) ;


        // then
        verify(cartCache).getCart(cartId);
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartCache).saveCart(captor.capture()) ;
        assertThat(captor.getValue().getProducts()).hasSize(1) ;
        assertThat(captor.getValue().getProducts().get(0).getQuantity()).isEqualTo(2) ;
        verify(productService).getProductById(productId);
        verify(stockClient).getStock(productId);
    }

    @Test
    public void should_throwIllegalArgumentException_when_cart_id_is_missing(){
        should_throwIllegalArgumentException(null, 1L, "Identifiant du panier est obligatoire");

    }

    @Test
    public void should_throwIllegalArgumentException_when_product_id_is_missing(){
        should_throwIllegalArgumentException(1L, null, "Produit à rajouter dans le panier est obligatoire");
    }


    private void should_throwIllegalArgumentException(Long cartId, Long productId, String message) {
        final TestResult<Cart> cartResult = tryToExecute(() -> cartService.addToCart(new AddProduct(productId, 1), cartId));

        // then
        assertThat(cartResult.getExceptionType()).isAssignableFrom(IllegalArgumentException.class);
        assertThat(cartResult.getMessage()).isEqualTo(message);

        verify(cartCache, never()).getCart(cartId);
        verify(cartCache, never()).saveCart(Mockito.any(Cart.class));
        verify(productService, never()).getProductById(anyLong());
        verify(stockClient, never()).getStock(anyLong());
    }


    @Test
    void should_not_add_product_to_cart_when_initiat_total_price_egale_100_euros() {
        // Given
        Long cartId = 1L;
        Long productId = 10L ;
        Cart initialCart = Cart.builder()
                .totalPrice(new Money(100f, EURO))
                .build();

        when(cartCache.getCart(cartId)).thenReturn(initialCart) ;
        when(currenceyConverter.getLocalCurrency()).thenReturn(EURO) ;

        //When
        final TestResult<Cart> cartResult = tryToExecute(() -> cartService.addToCart(new AddProduct(productId, 1), cartId));


        // then
        assertThat(cartResult.getExceptionType()).isAssignableFrom(BusinessException.class) ;
        assertThat(cartResult.getMessage()).isEqualTo("Le montant total du panier ne doit pas dépasser 100 euros") ;

        verify(cartCache).getCart(cartId);
        verify(cartCache, never()).saveCart(Mockito.any(Cart.class)) ;
        verify(productService,  never()).getProductById(anyLong());
        verify(stockClient, never()).getStock(anyLong());
    }

    @Test
    void should_not_add_product_to_cart_when_total_price_exceeds_100_euros() {
        // Given
        Long cartId = 1L;
        Long productId = 10L ;
        Cart initialCart = Cart.builder()
                .totalPrice(new Money(99f, EURO))
                .build();

        when(cartCache.getCart(cartId)).thenReturn(initialCart) ;
        when(productService.getProductById(productId)).thenReturn(Product.builder().id(productId).weightInKg(0.5f).price(new Money(10.02F, EURO)).build()) ;
        when(currenceyConverter.getLocalCurrency()).thenReturn(EURO) ;

        // When
        final TestResult<Cart> cartResult = tryToExecute(() -> cartService.addToCart(new AddProduct(productId, 1), cartId));

        // then
        assertThat(cartResult.getExceptionType()).isAssignableFrom(BusinessException.class) ;
        assertThat(cartResult.getMessage()).isEqualTo("Le montant total du panier ne doit pas dépasser 100 euros") ;

        verify(cartCache).getCart(cartId);
        verify(cartCache, never()).saveCart(Mockito.any(Cart.class)) ;
        verify(productService).getProductById(productId);
        verify(stockClient, never()).getStock(anyLong());
    }


    @Test
    void cart_cannot_contain_more_than_5_products() {

        // Given
        Long cartId = 1L;
        Long productId = 10L ;
        final List<AddProduct> products = LongStream.range(0, 5).mapToObj(i -> new AddProduct(i, 1)).collect(toList());
        Cart initialCart = Cart.builder()
                .totalPrice(new Money(99f, EURO))
                .products(products)
                .build();

        when(cartCache.getCart(cartId)).thenReturn(initialCart) ;
        when(currenceyConverter.getLocalCurrency()).thenReturn(EURO) ;

        // When
        final TestResult<Cart> cartResult = tryToExecute(() -> cartService.addToCart(new AddProduct(productId, 1), cartId));

        // then
        assertThat(cartResult.getExceptionType()).isAssignableFrom(BusinessException.class) ;
        assertThat(cartResult.getMessage()).isEqualTo("Le panier ne peut pas contenir plus de 5 produits") ;

        verify(cartCache, never()).saveCart(Mockito.any(Cart.class)) ;

        verify(cartCache).getCart(cartId);
        verify(productService, never()).getProductById(productId);
        verify(stockClient, never()).getStock(anyLong());
    }

    @Test
    public void should_throw_data_not_found_exception_when_cart_not_found(){
        // Given
        Long cartId = 1L;
        Long productId = 10L ;

        when(cartCache.getCart(cartId)).thenReturn(null) ;

        // When
        final TestResult<Cart> cartResult = tryToExecute(() -> cartService.addToCart(new AddProduct(productId, 1), cartId));

        // then
        assertThat(cartResult.getExceptionType()).isAssignableFrom(DataNotFoundException.class) ;
        assertThat(cartResult.getMessage()).isEqualTo("Panier non trouvé") ;

        verify(cartCache).getCart(cartId);
        verify(cartCache, never()).saveCart(Mockito.any(Cart.class)) ;
        verify(productService, never()).getProductById(anyLong());
        verify(stockClient, never()).getStock(anyLong());
    }

    @Test
    public void the_quantity_of_each_product_added_must_not_exceed_the_quantity_available(){
        // Given
        Long cartId = 1L;
        Long productId = 10L ;
        Cart initialCart = Cart.builder()
                .totalPrice(new Money(0f, EURO))
                .build();

        when(cartCache.getCart(cartId)).thenReturn(initialCart) ;
        when(stockClient.getStock(productId)).thenReturn(Stock.builder().productId(productId).quantityAvailable(3).build()) ;
        when(productService.getProductById(productId)).thenReturn(Product.builder().id(productId).weightInKg(0.5f).price(new Money(10.02F, EURO)).build()) ;
        when(currenceyConverter.getLocalCurrency()).thenReturn(EURO) ;

        // When
        final TestResult<Cart> cartResult = tryToExecute(() -> cartService.addToCart(new AddProduct(productId, 4), cartId));

        // then
        assertThat(cartResult.getExceptionType()).isAssignableFrom(BusinessException.class) ;
        assertThat(cartResult.getMessage()).isEqualTo("La quantité de chaque produit ajoutée dans le panier ne doit pas dépasser la quantité disponible en stock") ;

        verify(cartCache).getCart(cartId);
        verify(cartCache, never()).saveCart(Mockito.any(Cart.class)) ;
        verify(productService).getProductById(productId);
        verify(stockClient).getStock(productId);
    }

    @Test
    public void the_weight_for_each_product_in_cart_must_not_exceed_3_kg(){

        // Given
        Long cartId = 1L;
        Long productId = 10L ;
        Cart initialCart = Cart.builder()
                .totalPrice(new Money(0f, EURO))
                .build();

        when(cartCache.getCart(cartId)).thenReturn(initialCart) ;
        when(stockClient.getStock(productId)).thenReturn(Stock.builder().productId(productId).quantityAvailable(10).build()) ;
        when(productService.getProductById(productId)).thenReturn(Product.builder().id(productId).weightInKg(1.34f).price(new Money(1.00F, EURO)).build()) ;
        when(currenceyConverter.getLocalCurrency()).thenReturn(EURO) ;

        // When
        final TestResult<Cart> cartResult = tryToExecute(() -> cartService.addToCart(new AddProduct(productId, 4), cartId));

        // then
        assertThat(cartResult.getExceptionType()).isAssignableFrom(BusinessException.class) ;
        assertThat(cartResult.getMessage()).isEqualTo("Le poids pour chaque produit dans le panier ne doit pas excéder 3 kg") ;

        verify(cartCache, never()).saveCart(Mockito.any(Cart.class)) ;

        verify(cartCache).getCart(cartId);
        verify(productService).getProductById(productId);
        verify(stockClient, never()).getStock(productId);
    }
}
