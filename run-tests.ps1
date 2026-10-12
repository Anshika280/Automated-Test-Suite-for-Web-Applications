# ====================================================================
# ATS (Automated Test Suite) - Interactive Command Line Test Runner
# ====================================================================

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "             AUTOMATED TEST SUITE (ATS) TEST RUNNER                 " -ForegroundColor Yellow
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Target Website URL Input
Write-Host "Step 1: Target Website URL" -ForegroundColor Green
Write-Host " Enter website URL to test (e.g. https://example.com, https://google.com)"
Write-Host " Press [ENTER] to use default (SauceDemo: https://www.saucedemo.com)"
$targetUrl = Read-Host "Enter Website URL"

if ([string]::IsNullOrWhiteSpace($targetUrl)) {
    $targetUrl = "https://www.saucedemo.com"
} elseif (-not ($targetUrl.StartsWith("http://") -or $targetUrl.StartsWith("https://"))) {
    $targetUrl = "https://" + $targetUrl
}

Write-Host " Target URL selected: $targetUrl" -ForegroundColor Cyan
Write-Host ""

# 2. Suite Selection
Write-Host "Step 2: Select Test Suite to Execute:" -ForegroundColor Green

$suiteXml = ""
$testClass = ""

if ($targetUrl -eq "https://www.saucedemo.com") {
    Write-Host " [1] Master Suite (All 36 Tests: UI, API, Integration, DB - Default)"
    Write-Host " [2] Smoke Test Suite"
    Write-Host " [3] Functional Test Suite"
    Write-Host " [4] Regression Test Suite"
    Write-Host " [5] Database Validation Suite Only"
    Write-Host " [6] Website Health, Performance & Visual Smoke Suite (Generic)"
    $suiteChoice = Read-Host "Enter Choice [1-6] (Default: 1)"

    switch ($suiteChoice) {
        "2" { $suiteXml = "src/test/resources/testng/smoke.xml" }
        "3" { $suiteXml = "src/test/resources/testng/functional.xml" }
        "4" { $suiteXml = "src/test/resources/testng/regression.xml" }
        "5" { $suiteXml = "src/test/resources/testng/testng.xml" }
        "6" { $suiteXml = "src/test/resources/testng/dynamic-website.xml" }
        Default { $suiteXml = "src/test/resources/testng/testng.xml" }
    }
} else {
    Write-Host " [1] Website Quality, Health, Performance & Visual Smoke Suite (Default)"
    Write-Host " [2] Run Specific Custom Test Class"
    Write-Host " [3] Run Master Test Suite against target URL"
    $suiteChoice = Read-Host "Enter Choice [1-3] (Default: 1)"

    switch ($suiteChoice) {
        "2" {
            $customClass = Read-Host "Enter Test Class Name (e.g. MyWebsiteTest)"
            if (-not [string]::IsNullOrWhiteSpace($customClass)) {
                $testClass = $customClass
            } else {
                $suiteXml = "src/test/resources/testng/dynamic-website.xml"
            }
        }
        "3" {
            $suiteXml = "src/test/resources/testng/testng.xml"
        }
        Default {
            $suiteXml = "src/test/resources/testng/dynamic-website.xml"
        }
    }
}

# 3. Environment Selection
Write-Host ""
Write-Host "Step 3: Select Environment:" -ForegroundColor Green
Write-Host " [1] QA Environment (Default)"
Write-Host " [2] Staging Environment"
$envChoice = Read-Host "Enter Choice [1-2] (Default: 1)"

$envName = "qa"
if ($envChoice -eq "2") { $envName = "staging" }

# 4. Browser Selection
Write-Host ""
Write-Host "Step 4: Select Browser:" -ForegroundColor Green
Write-Host " [1] Google Chrome (Default)"
Write-Host " [2] Mozilla Firefox"
Write-Host " [3] Microsoft Edge"
$browserChoice = Read-Host "Enter Choice [1-3] (Default: 1)"

$browserName = "chrome"
switch ($browserChoice) {
    "2" { $browserName = "firefox" }
    "3" { $browserName = "edge" }
}

# 5. Execution Mode
Write-Host ""
Write-Host "Step 5: Select Browser Mode:" -ForegroundColor Green
Write-Host " [1] Visible Browser Mode (Headed)"
Write-Host " [2] Silent Memory Mode (Headless - Default)"
$headlessChoice = Read-Host "Enter Choice [1-2] (Default: 2)"

$headlessMode = "true"
if ($headlessChoice -eq "1") { $headlessMode = "false" }

Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "Executing Tests with Configuration:" -ForegroundColor Yellow
Write-Host " Target URL:  $targetUrl"
if (-not [string]::IsNullOrWhiteSpace($testClass)) {
    Write-Host " Test Class:  $testClass"
} else {
    Write-Host " Suite XML:   $suiteXml"
}
Write-Host " Environment: $envName"
Write-Host " Browser:     $browserName"
Write-Host " Headless:    $headlessMode"
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

$mvnCmd = "C:\Users\udit\tools\apache-maven-3.9.9\bin\mvn.cmd"
if (-not (Test-Path $mvnCmd)) {
    $mvnCmd = "mvn"
}

# Execute Maven Test
if (-not [string]::IsNullOrWhiteSpace($testClass)) {
    & $mvnCmd test "-Dtest=$testClass" "-DbaseUrl=$targetUrl" "-Dbase.url=$targetUrl" "-Denvironment=$envName" "-Dbrowser=$browserName" "-Dheadless=$headlessMode"
} else {
    & $mvnCmd test "-DsuiteXmlFile=$suiteXml" "-DbaseUrl=$targetUrl" "-Dbase.url=$targetUrl" "-Denvironment=$envName" "-Dbrowser=$browserName" "-Dheadless=$headlessMode"
}

Write-Host ""
Write-Host "Generating Allure Visual Report..." -ForegroundColor Green
& $mvnCmd allure:report

Write-Host ""
$openReport = Read-Host "Do you want to launch the Allure Visual Report in browser? (Y/N) [Default: Y]"
if ($openReport -ne "N" -and $openReport -ne "n") {
    & $mvnCmd allure:serve
}
