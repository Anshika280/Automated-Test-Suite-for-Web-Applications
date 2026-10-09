# SDET & Senior Test Automation Engineer Interview Guide

This guide contains key architectural questions, technical design trade-offs, and behavioral scenarios based on the **Automated Test Suite (ATS)** framework.

---

## 1. Thread Safety & Parallel Execution

### Q1: How did you ensure thread safety for multi-threaded parallel test execution in your Java framework?
**Answer**:
Thread safety is achieved using a `ThreadLocal<WebDriver>` pattern encapsulated in the static `DriverManager` class.
- Each execution thread obtains its own isolated `WebDriver` instance via `DriverFactory.createDriver()`.
- During `@AfterMethod` teardown, `DriverManager.quitDriver()` closes the browser and invokes `driverThreadLocal.remove()` to prevent memory leaks and thread pool state bleed.

---

## 2. Dynamic Configuration Inheritance

### Q2: How does your framework handle configuration switching across environments (QA, Staging, Production)?
**Answer**:
The framework uses a 3-tier configuration hierarchy implemented in `ConfigManager`:
1. **Tier 1 (Highest Priority)**: Command-line Java System Properties (`-Dbrowser=firefox -Denvironment=staging`).
2. **Tier 2**: Environment-specific properties (`staging.properties`, `qa.properties`).
3. **Tier 3 (Fallback)**: Base default properties (`config.properties`).

---

## 3. Element Synchronization & Resiliency

### Q3: How do you handle flaky elements and dynamic web page synchronization in Selenium WebDriver?
**Answer**:
We avoid `Thread.sleep()` entirely in favor of explicit waits encapsulated in `WaitUtils` (built on Selenium `WebDriverWait`).
- All `BasePage` interaction methods (`click`, `sendKeys`, `getText`) wait for element visibility or clickability.
- In cases where JavaScript animation transitions or focus shifts intercept standard click events, `BasePage.click()` automatically catches the exception and executes a JavaScript fallback (`((JavascriptExecutor) driver).executeScript("arguments[0].click();", element)`).

---

## 4. API & UI Hybrid Integration Strategy

### Q4: Why would you combine REST API automation with UI automation in a single integration test?
**Answer**:
Setting up test data via the UI is slow and fragile. In `ApiUiIntegrationTest`, we seed backend data (authenticating and creating user entities) via REST API calls using REST Assured before performing the target UI purchasing workflow. Afterwards, we execute API teardown to delete created test data, reducing test execution time by over 60%.

---

## 5. Database Testing Strategy

### Q5: How do you perform database validation in your test automation framework?
**Answer**:
We implemented `DatabaseUtils`, a thread-safe JDBC wrapper.
- Uses `PreparedStatement` binding to prevent SQL injection and map parameters dynamically.
- Returns query results as `List<Map<String, Object>>`, allowing tests to assert database state directly using TestNG assertions.
- Supports running DDL scripts for setting up in-memory database schemas (H2) during CI runs.

---

## 6. TestNG Listeners & Retry Logic

### Q6: How do you handle flaky test retries without masking genuine application bugs?
**Answer**:
- We implemented `RetryAnalyzer` implementing `IRetryAnalyzer`, configured with a strict `MAX_RETRY_COUNT` (default: 1 retry).
- We registered an `IAnnotationTransformer` (`AnnotationTransformer`) to attach `RetryAnalyzer` dynamically across all `@Test` methods.
- Failed attempts are logged as `WARN` and captured in Allure reports as skipped/retried attempts rather than hiding failures.

---

## 7. Reporting & CI/CD Integration

### Q7: How are test reports generated and integrated into CI/CD pipelines?
**Answer**:
- We integrated `Allure TestNG` with `AspectJ Weaver` bytecode instrumentation.
- `TestListener` implements `onTestFailure` to take screenshots via `ScreenshotUtils` and attach PNG bytes directly to Allure test steps via `@Attachment`.
- In GitHub Actions (`test.yml`), test execution triggers `mvn allure:report`, and the resulting HTML site is uploaded as a build artifact.
