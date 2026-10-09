# Automated Test Suite for Web Applications (ATS)

[![Java 17](https://img.shields.io/badge/JDK-17-orange.svg)](https://www.oracle.com/java/)
[![Selenium 4](https://img.shields.io/badge/Selenium-4.25.0-green.svg)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-blue.svg)](https://testng.org/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.5.0-red.svg)](https://rest-assured.io/)
[![Allure Report](https://img.shields.io/badge/Allure-2.29.0-yellow.svg)](https://allurereport.org/)
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)](https://www.docker.com/)

A production-grade, highly modular, thread-safe automated test suite in **Java 17** demonstrating modern SDET/SDE architecture standards.

The framework integrates **Selenium WebDriver 4**, **TestNG**, **REST Assured**, **Page Object Model (POM)**, **Data-Driven Testing (Jackson)**, **JDBC Database Validation**, **Custom TestNG Execution Listeners**, **Retry Analyzers**, **Logback**, **Allure Reporting**, **Docker Containerization**, and **GitHub Actions CI/CD**.

---

## Technical Stack & Framework Architecture

- **Programming Language**: Java 17 (Target Java 17 bytecode)
- **UI Automation Engine**: Selenium WebDriver 4.25.0 (W3C Standard Compliant)
- **API Automation Framework**: REST Assured 5.5.0
- **Test Runner & Assertions**: TestNG 7.10.2
- **Data Serialization**: Jackson Databind 2.17.2
- **Database Engine**: H2 In-Memory JDBC Driver 2.3.232
- **Binary Management**: WebDriverManager 5.9.2
- **Logging**: SLF4J 2.0.16 + Logback 1.5.8
- **Reporting Engine**: Allure TestNG 2.29.0 + AspectJ Weaver 1.9.24
- **Containerization**: Docker Multi-stage + Docker Compose + Selenium Grid 4.25.0
- **Continuous Integration**: GitHub Actions CI/CD Pipeline

---

## Directory Structure & Folder Taxonomy

```
ATS/
├── .github/
│   └── workflows/
│       └── test.yml                 # GitHub Actions CI/CD Pipeline
├── docker/
│   ├── Dockerfile                   # Multi-stage Docker build
│   ├── docker-compose.yml           # Distributed Grid + Test Runner compose
│   └── README.md                    # Container execution documentation
├── docs/
│   ├── ARCHITECTURE.md              # Technical architectural deep dive
│   └── INTERVIEW_QA.md              # SDET interview design & strategy guide
├── logs/
│   └── framework.log                # Structured execution logs
├── postman/
│   ├── ATS_API_Collection.json      # Postman Collection v2.1.0
│   ├── ATS_Environment.json         # Postman Environment Variables
│   └── README.md                    # Newman execution guide
├── reports/                         # Extensible HTML reports
├── screenshots/                     # Automatic timestamped failure PNGs
├── src/
│   ├── main/
│   │   ├── java/framework/
│   │   │   ├── api/                 # REST Assured clients & spec builders
│   │   │   ├── base/                # BaseTest suite setup & teardown
│   │   │   ├── config/              # 3-Tier ConfigManager
│   │   │   ├── constants/           # Framework constants & file paths
│   │   │   ├── db/                  # JDBC DatabaseUtils & helpers
│   │   │   ├── driver/              # DriverFactory & ThreadLocal DriverManager
│   │   │   ├── listeners/           # TestListener & RetryAnalyzer & Transformer
│   │   │   ├── models/              # POJO Data Models (Jackson)
│   │   │   ├── pages/               # Page Object Model Encapsulated Classes
│   │   │   └── utils/               # WaitUtils, ScreenshotUtils, JsonDataReader
│   │   └── resources/
│   │       ├── config/              # Base, QA, and Staging properties
│   │       └── logback.xml          # Logback appenders & level suppression
│   └── test/
│       ├── java/tests/
│       │   ├── api/                 # REST API Test Cases
│       │   ├── config/              # Configuration validation tests
│       │   ├── db/                  # JDBC Database validation tests
│       │   ├── driver/              # WebDriver factory tests
│       │   ├── integration/         # API + UI Hybrid end-to-end tests
│       │   ├── listeners/           # Listener & retry mechanism tests
│       │   ├── regression/          # End-to-end regression workflows
│       │   ├── ui/                  # POM UI Functional & Smoke tests
│       │   └── utils/               # Wait & helper utility tests
│       └── resources/
│           ├── testdata/            # External JSON datasets (loginData.json)
│           └── testng/              # TestNG suite XML files
├── Dockerfile                       # Root Dockerfile
├── docker-compose.yml               # Root Docker Compose
├── pom.xml                          # Maven dependencies & build plugins
└── README.md                        # Master repository documentation
```

---

## Core Design Patterns & Architecture Features

### 1. 3-Tier Configuration Inheritance
`ConfigManager` resolves parameters using strict priority:
1. System Property Override (`-Dkey=value`)
2. Active Environment Properties (`qa.properties`, `staging.properties`)
3. Default Base Properties (`config.properties`)

### 2. Thread-Safe WebDriver Isolation
`DriverManager` utilizes `ThreadLocal<WebDriver>` ensuring complete thread isolation during concurrent multi-threaded execution.

### 3. Resilient Element Synchronization (`WaitUtils` & `BasePage`)
`BasePage` encapsulates `WebDriverWait` explicit synchronization with automatic JavaScript click fallbacks for animation transitions and focus shifts.

### 4. API + UI Hybrid Data Seeding
`ApiUiIntegrationTest` seeds authentication tokens and creates user entities via REST API (`UserClient`) before executing UI purchasing workflows, followed by API teardown.

### 5. Database Testing Engine (`DatabaseUtils`)
JDBC helper supporting parameterized queries, entity mapping, DML updates, and DDL schema initialization.

### 6. Custom TestNG Execution Listeners & Flaky Retry Analyzer
- `TestListener`: Tracks test lifecycle events and attaches failure screenshots to Allure HTML reports.
- `RetryAnalyzer`: Reruns transient failures up to a configured threshold.
- `AnnotationTransformer`: Dynamically attaches `RetryAnalyzer` across all `@Test` methods.

---

## Command Line Execution Guide

### 1. Default Master Suite Execution
```bash
mvn test
```

### 2. Execution with Environment & Browser CLI Overrides
```bash
mvn test -Dbrowser=firefox -Denvironment=staging -Dheadless=true
```

### 3. Specific Test Suite Execution
```bash
# Run Smoke Test Suite
mvn test -DsuiteXmlFile=src/test/resources/testng/smoke.xml

# Run Regression Test Suite
mvn test -DsuiteXmlFile=src/test/resources/testng/regression.xml

# Run Functional Test Suite
mvn test -DsuiteXmlFile=src/test/resources/testng/functional.xml
```

### 4. Generating & Viewing Allure Reports
```bash
# Generate Allure HTML Report
mvn allure:report

# Serve Allure Report on Local Web Server
mvn allure:serve
```

---

## Docker Containerization & Grid Setup

Run test suite inside Docker:
```bash
docker-compose up --build --exit-code-from ats-tests
```

Tear down grid environment:
```bash
docker-compose down -v
```
