# ====================================================================
# ATS (Automated Test Suite) - Interactive Command Line Test Runner
# ====================================================================

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "             AUTOMATED TEST SUITE (ATS) TEST RUNNER                 " -ForegroundColor Yellow
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Suite Selection
Write-Host "Select Test Suite to Execute:" -ForegroundColor Green
Write-Host " [1] Master Suite (All 34 Tests: UI, API, Integration, DB)"
Write-Host " [2] Smoke Test Suite"
Write-Host " [3] Functional Test Suite"
Write-Host " [4] Regression Test Suite"
Write-Host " [5] Database Validation Suite Only"
$suiteChoice = Read-Host "Enter Choice [1-5] (Default: 1)"

$suiteXml = "src/test/resources/testng/testng.xml"
switch ($suiteChoice) {
    "2" { $suiteXml = "src/test/resources/testng/smoke.xml" }
    "3" { $suiteXml = "src/test/resources/testng/functional.xml" }
    "4" { $suiteXml = "src/test/resources/testng/regression.xml" }
    "5" { $suiteXml = "src/test/resources/testng/testng.xml" } # Master xml includes DB
}

# 2. Environment Selection
Write-Host ""
Write-Host "Select Environment:" -ForegroundColor Green
Write-Host " [1] QA Environment (Default)"
Write-Host " [2] Staging Environment"
$envChoice = Read-Host "Enter Choice [1-2] (Default: 1)"

$envName = "qa"
if ($envChoice -eq "2") { $envName = "staging" }

# 3. Browser Selection
Write-Host ""
Write-Host "Select Browser:" -ForegroundColor Green
Write-Host " [1] Google Chrome (Default)"
Write-Host " [2] Mozilla Firefox"
Write-Host " [3] Microsoft Edge"
$browserChoice = Read-Host "Enter Choice [1-3] (Default: 1)"

$browserName = "chrome"
switch ($browserChoice) {
    "2" { $browserName = "firefox" }
    "3" { $browserName = "edge" }
}

# 4. Execution Mode
Write-Host ""
Write-Host "Select Browser Mode:" -ForegroundColor Green
Write-Host " [1] Visible Browser Mode (Headed)"
Write-Host " [2] Silent Memory Mode (Headless - Default)"
$headlessChoice = Read-Host "Enter Choice [1-2] (Default: 2)"

$headlessMode = "true"
if ($headlessChoice -eq "1") { $headlessMode = "false" }

Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "Executing Tests with Configuration:" -ForegroundColor Yellow
Write-Host " Suite XML:   $suiteXml"
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
& $mvnCmd test "-DsuiteXmlFile=$suiteXml" "-Denvironment=$envName" "-Dbrowser=$browserName" "-Dheadless=$headlessMode"

Write-Host ""
Write-Host "Generating Allure Visual Report..." -ForegroundColor Green
& $mvnCmd allure:report

Write-Host ""
$openReport = Read-Host "Do you want to launch the Allure Visual Report in browser? (Y/N) [Default: Y]"
if ($openReport -ne "N" -and $openReport -ne "n") {
    & $mvnCmd allure:serve
}
