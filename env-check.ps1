# Environment Verification Script for ATS Project
Write-Host "================================================" -ForegroundColor Cyan
Write-Host " Automated Test Suite (ATS) - Environment Check " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

# 1. Java Version Check
Write-Host "`n[1/4] Checking Java Installation..." -ForegroundColor Yellow
try {
    $javaVer = java -version 2>&1 | Out-String
    Write-Host $javaVer -ForegroundColor Green
} catch {
    Write-Host "ERROR: Java is not installed or not in PATH!" -ForegroundColor Red
}

# 2. Maven Version Check
Write-Host "`n[2/4] Checking Maven Installation..." -ForegroundColor Yellow
try {
    $mvnVer = C:\Users\udit\tools\apache-maven-3.9.9\bin\mvn.cmd -version 2>&1 | Out-String
    Write-Host $mvnVer -ForegroundColor Green
} catch {
    Write-Host "ERROR: Maven is not installed or not in PATH!" -ForegroundColor Red
}

# 3. Git Version Check
Write-Host "`n[3/4] Checking Git Installation..." -ForegroundColor Yellow
try {
    $gitVer = git --version 2>&1 | Out-String
    Write-Host $gitVer -ForegroundColor Green
} catch {
    Write-Host "ERROR: Git is not installed or not in PATH!" -ForegroundColor Red
}

# 4. Docker Version Check
Write-Host "`n[4/4] Checking Docker Installation..." -ForegroundColor Yellow
try {
    $dockerVer = docker --version 2>&1 | Out-String
    Write-Host $dockerVer -ForegroundColor Green
} catch {
    Write-Host "ERROR: Docker is not installed or not running!" -ForegroundColor Red
}

Write-Host "`n================================================" -ForegroundColor Cyan
Write-Host " Environment Verification Complete! " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
