package fr.carrefour.biskot.rest.config;

import fr.carrefour.biskot.exception.BusinessException;
import fr.carrefour.biskot.exception.DataNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHundler {

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<Object> dataNotFound(DataNotFoundException exception) {
        return ResponseEntity.notFound().header("error_message" , exception.getMessage()).build() ;
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<String> businessException(BusinessException exception) {
        return ResponseEntity.badRequest().header("error_message" , exception.getMessage()).build() ;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> businessIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().header("error_message" , exception.getMessage()).build() ;
    }
}
