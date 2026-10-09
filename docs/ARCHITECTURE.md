# Framework Architecture & Design Specifications

This document details the architectural principles, component design, control flow, and design patterns implemented in the **Automated Test Suite (ATS)**.

---

## Architecture Diagram

```
+-----------------------------------------------------------------------------------+
|                                 Test Execution Layer                              |
|   [SmokeTest]    [FunctionalTest]    [RegressionTest]    [ApiUiIntegrationTest]   |
+------------------------------------------+----------------------------------------+
                                           |
+------------------------------------------v----------------------------------------+
|                                Abstract Base Test Layer                           |
|                                       BaseTest                                    |
|             @BeforeSuite  @BeforeMethod  @AfterMethod  @AfterSuite                |
+---------------+--------------------------+--------------------------+-------------+
                |                          |                          |
+---------------v-------+  +---------------v-------+  +---------------v-------------+
|    Driver Management  |  | Configuration System  |  |    Reporting & Listeners    |
|     DriverFactory     |  |     ConfigManager     |  |        TestListener        |
|      (Factory)        |  |  (3-Tier Inheritance) |  |       RetryAnalyzer         |
|     DriverManager     |  |                       |  |   AnnotationTransformer     |
|    (ThreadLocal)      |  +-----------------------+  +-----------------------------+
+---------------+-------+
                |
+---------------v-------------------------------------------------------------------+
|                                 Page Object Model Layer                           |
|                                        BasePage                                   |
|                 (Explicit Wait Synchronization & JS Click Fallback)               |
|                                           |                                       |
|    [LoginPage]          [InventoryPage]          [CartPage]        [CheckoutPage] |
+-----------------------------------------------------------------------------------+
```

---

## Core Architectural Components

### 1. ThreadLocal Driver Management
- **Problem**: Global static `WebDriver` instances cause race conditions during parallel execution.
- **Solution**: `DriverManager` wraps `WebDriver` in `ThreadLocal<WebDriver>`.
- **Implementation**:
  ```java
  private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

  public static WebDriver getDriver() {
      return driverThreadLocal.get();
  }

  public static void quitDriver() {
      WebDriver driver = driverThreadLocal.get();
      if (driver != null) {
          try {
              driver.quit();
          } finally {
              driverThreadLocal.remove(); // Prevents memory leaks
          }
      }
  }
  ```

### 2. 3-Tier Property Inheritance System
Properties are resolved with priority:
1. **System Property Override**: `-Dkey=value` CLI flag.
2. **Environment Property File**: `src/main/resources/config/<environment>.properties`.
3. **Base Property File**: `src/main/resources/config/config.properties`.

### 3. Page Object Model & Resilient Element Interaction
- `BasePage` encapsulates `WaitUtils` (built on Selenium `WebDriverWait`).
- All element interactions (`click`, `sendKeys`, `getText`) use explicit condition matching before execution.
- Includes automatic JavaScript execution fallback when standard element clicks are intercepted by web animations or focus transitions.

### 4. REST Assured API Automation Layer
- `BaseApiClient` constructs shared `RequestSpecBuilder` (base URL, JSON headers, LogConfig).
- Endpoints encapsulated in domain clients (`UserClient`, `AuthClient`).
- Data models mapped via Jackson annotations (`@JsonProperty`).

### 5. JDBC Database Validation Layer
- `DatabaseUtils` manages JDBC connection pooling for H2/RDBMS environments.
- Implements `executeQuery` returning structured `List<Map<String, Object>>` for simple assertion verification.
- Implements parameterized statements to prevent SQL injection and support clean type binding.

### 6. TestNG Listeners & Retry Strategy
- `TestListener`: Hooks into `onTestFailure` to take screenshots via `ScreenshotUtils` and attach them to Allure using `@Attachment`.
- `RetryAnalyzer`: Implements `IRetryAnalyzer` to rerun transient failures up to `MAX_RETRY_COUNT`.
- `AnnotationTransformer`: Dynamically registers `RetryAnalyzer` across all suite test methods without requiring manual annotation parameters.

---

## Design Patterns Utilized

| Design Pattern | Location | Purpose |
| :--- | :--- | :--- |
| **Factory Pattern** | `DriverFactory.java` | Decouples browser driver instantiation (`Chrome`, `Firefox`, `Edge`). |
| **Singleton / ThreadLocal** | `DriverManager.java` | Encapsulates single-thread driver access per execution thread. |
| **Page Object Model** | `BasePage`, `LoginPage`, etc. | Decouples page element locators and UI actions from test logic. |
| **Builder Pattern** | `BaseApiClient.java` | Utilizes REST Assured `RequestSpecBuilder` for API requests. |
| **Template Method** | `BaseTest.java` | Defines TestNG execution lifecycle hooks (`@BeforeSuite`, `@AfterMethod`). |
| **Data Provider Pattern** | `JsonDataReader.java` | Supplies external JSON test data objects to TestNG methods. |
