package src.test.java.org.example;

import org.junit.jupiter.api.Test;

import src.main.java.org.example.*;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MultiprocessingTest {
    @Test
    void shouldSucceedEnrichmentInConcurrentEnvironmentSuccessfully() throws InterruptedException {
        UserRepository userRepository = new InMemoryUserRepository();
        EnrichmentService enrichmentService = new EnrichmentService(userRepository);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);
        
        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                Message message = new Message(new HashMap<>(), Message.EnrichmentType.MSISDN);
                enrichmentService.enrich(message);
                latch.countDown();
            });
        }
        
        latch.await();
    }
}