package fr.carrefour.biskot.rest.config;

import fr.carrefour.biskot.exception.BusinessException;
import fr.carrefour.biskot.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHundler {

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<Object> dataNotFound(DataNotFoundException exception) {
        log.warn("Data not found : {}"  , exception.getMessage());
        return ResponseEntity.notFound().header("error_message" , exception.getMessage()).build() ;
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<String> businessException(BusinessException exception) {
        log.warn("business error : {}"  , exception.getMessage());
        return ResponseEntity.badRequest().header("error_message" , exception.getMessage()).build() ;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> businessIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("invalid param : {}"  , exception.getMessage());
        return ResponseEntity.badRequest().header("error_message" , exception.getMessage()).build() ;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> othersException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
    }

}
