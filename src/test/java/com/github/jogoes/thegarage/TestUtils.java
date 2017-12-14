package com.github.jogoes.thegarage;

import static org.junit.Assert.assertTrue;

public class TestUtils {

    /**
     * Helper function used to verify that a piece of code throws a specifiable exception.
     * @param expected com exception class expected to be thrown
     * @param runnable com code to be executed
     */
    public static void assertThrows(Class<? extends Throwable> expected, Runnable runnable) {

        try
        {
            runnable.run();
        } catch(Throwable e) {
            assertTrue(String.format("Unexpected exception thrown: expected: %s, actual: %s", expected.getName(), e.getClass().getName() ), e.getClass().isAssignableFrom(expected));
        }
    }
}
