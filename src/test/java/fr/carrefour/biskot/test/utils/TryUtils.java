package fr.carrefour.biskot.test.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class TryUtils {

    public static TestResult tryToExecute(Runnable runnable) {
        try {
            runnable.run();
            return TestResult.valid();
        } catch (Exception e) {
            return TestResult.error(e);
        }
    }

    public static <T>TestResult<T> tryToExecute(Callable<T> callable) {
        try {
            return TestResult.valid(callable.call());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return TestResult.error(e);
        }
    }
}
