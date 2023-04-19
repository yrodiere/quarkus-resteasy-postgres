package org.acme;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MultiTests {

    @Test
    public void testMultithreading() {

        final ExecutorService executorService = Executors.newFixedThreadPool(3);

        List<CompletableFuture<Integer>> futures = List.of(
                CompletableFuture.supplyAsync(getIntegerSupplier(1), executorService),
                CompletableFuture.supplyAsync(getIntegerSupplier(2), executorService),
                CompletableFuture.supplyAsync(getIntegerSupplier(3), executorService)
        );

        List<Integer> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        StringBuilder sb = new StringBuilder();
        results.forEach(i -> sb.append(i).append(","));
        sout(sb.toString());
    }

    private static Supplier<Integer> getIntegerSupplier(Integer i) {
        return () -> {
            sleep(i*1000);
            // Perform parallel computation on separate thread
            sout("Hello from supply async!");
            return i * 2;
        };
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMultithreading2() {

        List<Integer> results = Stream.of(1, 6)
            .parallel()
            .map(MultiTests::doublerFunction)
            .toList();


        //alternative with custom ForkJoinPool
//        List<Integer> results = new ForkJoinPool(4).submit(() ->
//                Stream.of(1, 6)
//                        .parallel()
//                        .map(MultiTests::doublerFunction)
//        ).join().toList();;

        sout("all doubler function executions finished. Results: " +  results);
    }

    private static int doublerFunction(int i) {
        sout("entering doubler, calculating double of i="+i);
        try {
            Thread.sleep(i*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sout("exiting doubler");
        return i * 2;
    }

    private static void sout(String string){
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] %s \n", threadName, string);
    }
}
