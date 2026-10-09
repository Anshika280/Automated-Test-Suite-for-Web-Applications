package tests.listeners;

import framework.listeners.RetryAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Verification test class for TestNG TestListener and RetryAnalyzer.
 */
public class ListenerAndRetryTest {

    private static final Logger log = LoggerFactory.getLogger(ListenerAndRetryTest.class);
    private static int retryCounter = 0;

    @Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
    public void testFlakyRetryMechanism() {
        retryCounter++;
        log.info("Executing testFlakyRetryMechanism - Execution Count: {}", retryCounter);

        if (retryCounter == 1) {
            log.warn("Simulating transient failure on attempt 1 to trigger RetryAnalyzer...");
            Assert.fail("Simulated transient network/element failure on first execution attempt");
        }

        Assert.assertTrue(retryCounter > 1, "Test should succeed on retry attempt 2");
        log.info("Flaky test successfully recovered and passed on retry attempt {}", retryCounter);
    }

    @Test(priority = 2)
    public void testListenerLifecycleLogging() {
        log.info("Executing standard test to verify listener start and success hooks");
        Assert.assertTrue(true, "Standard passing test for listener tracking");
    }
}
