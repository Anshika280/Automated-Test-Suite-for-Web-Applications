package framework.listeners;

import framework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Controlled Test Retry Analyzer that automatically reruns flaky or transient test failures
 * up to a configured threshold without masking underlying application defects.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    private int retryCount = 0;
    private int maxRetryCount = 1;

    public RetryAnalyzer() {
        String countProp = ConfigManager.get("retryCount", "1");
        try {
            this.maxRetryCount = Integer.parseInt(countProp);
        } catch (NumberFormatException e) {
            this.maxRetryCount = 1;
        }
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            log.warn("Retrying test [{}.{}] - Attempt {} of {}",
                    result.getTestClass().getRealClass().getSimpleName(),
                    result.getMethod().getMethodName(),
                    retryCount,
                    maxRetryCount);
            return true;
        }
        return false;
    }
}
