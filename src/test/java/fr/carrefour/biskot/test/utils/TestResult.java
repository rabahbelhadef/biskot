package fr.carrefour.biskot.test.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TestResult<T> {
    private Class<? extends Exception> exceptionType;
    private String message;
    private T result;

    static TestResult valid() {
        return new TestResult();
    }

    static <T> TestResult<T> valid(T result) {
        return new TestResult<T>(null, null, result);
    }


    static <T> TestResult<T> error(Exception e) {
        return new TestResult<T>(e.getClass(), e.getMessage(), null);
    }
}
