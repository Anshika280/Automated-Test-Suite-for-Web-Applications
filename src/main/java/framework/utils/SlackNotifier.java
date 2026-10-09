package framework.utils;

import framework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Real-time Execution Notification Utility for publishing test suite execution
 * summaries to Slack or webhook-compatible messaging platforms.
 */
public class SlackNotifier {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifier.class);

    private SlackNotifier() {
        // Utility class
    }

    /**
     * Publishes structured test execution summary to configured Slack webhook URL.
     *
     * @param context TestNG ITestContext containing execution metrics
     */
    public static void sendExecutionSummary(ITestContext context) {
        String webhookUrl = ConfigManager.get("slack.webhook.url");
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            log.debug("Slack webhook URL not configured. Skipping execution notification.");
            return;
        }

        try {
            int passed = context.getPassedTests().size();
            int failed = context.getFailedTests().size();
            int skipped = context.getSkippedTests().size();
            int total = passed + failed + skipped;
            String suiteName = context.getName();
            String env = ConfigManager.getEnvironment().toUpperCase();
            String status = (failed == 0) ? "PASSED" : "FAILED";

            String jsonPayload = String.format(
                    "{\"text\": \"*Automated Test Suite Execution Summary*\\n" +
                            "> *Suite*: %s\\n" +
                            "> *Environment*: %s\\n" +
                            "> *Status*: %s\\n" +
                            "> *Total Tests*: %d | *Passed*: %d | *Failed*: %d | *Skipped*: %d\"}",
                    suiteName, env, status, total, passed, failed, skipped
            );

            log.info("Sending test execution summary to Slack webhook...");
            URL url = URI.create(webhookUrl.trim()).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            log.info("Slack Webhook response status code: {}", responseCode);

        } catch (Exception e) {
            log.warn("Failed to publish execution summary to Slack webhook: {}", e.getMessage());
        }
    }
}
