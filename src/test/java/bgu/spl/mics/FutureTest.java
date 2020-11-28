package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testResolve(){
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @Test
    public void testGet() {
        String result = "someResult";
        future.resolve(result);
        assertTrue(result.equals(future.get()));
    }

    @Test
    public void testIsDone() {
        String result = "someResult";
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
    }

    @Test
    public void testGetTimeout() {
        String result = "someResult";
        long start = System.nanoTime();
        assertNull(future.get(50, TimeUnit.NANOSECONDS));
        long end = System.nanoTime();
        assertTrue(end - start > 50);
        future.resolve(result);
        assertTrue(result.equals(future.get(1,TimeUnit.MILLISECONDS)));
        System.currentTimeMillis();
    }
}
