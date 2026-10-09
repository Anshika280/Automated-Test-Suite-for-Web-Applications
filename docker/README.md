# Docker Containerization & Distributed Execution Guide

This document explains how to build, run, and scale containerized test execution for the **Automated Test Suite (ATS)** using **Docker** and **Selenium Grid**.

---

## Architecture Overview

```
+-----------------------------------------------------------------------+
|                           Docker Compose Host                         |
|                                                                       |
|  +-------------------+      +--------------------+                   |
|  |   ats-tests       | ---> |   selenium-hub     |                   |
|  |   (Test Runner)   |      |   (Port 4444)      |                   |
|  +-------------------+      +---------+----------+                   |
|                                       |                               |
|                     +-----------------+-----------------+             |
|                     |                                   |             |
|          +----------v---------+               +---------v----------+  |
|          |    chrome-node     |               |    firefox-node    |  |
|          | (Chrome 154 Headed)|               |  (Firefox Headed)  |  |
|          +--------------------+               +--------------------+  |
+-----------------------------------------------------------------------+
```

---

## 1. Single Container Headless Execution

Build the Docker image:

```bash
docker build -t ats-framework:latest .
```

Run test suite headlessly inside the container:

```bash
docker run --rm \
  -v $(pwd)/target/surefire-reports:/app/target/surefire-reports \
  -v $(pwd)/allure-results:/app/allure-results \
  -v $(pwd)/logs:/app/logs \
  ats-framework:latest
```

---

## 2. Distributed Execution via Selenium Grid (Docker Compose)

Start Selenium Grid Hub, Chrome Node, Firefox Node, and Test Runner container:

```bash
docker-compose up --build --exit-code-from ats-tests
```

Tear down containers after test completion:

```bash
docker-compose down -v
```

---

## 3. Viewing Allure Reports after Docker Runs

After Docker container execution completes, host volume mounts contain the generated `allure-results`. Generate and open the HTML report:

```bash
mvn allure:report
mvn allure:serve
```
