@echo off
TITLE ATS Automated Test Suite - One Click Runner

cls

echo ====================================================================
echo             AUTOMATED TEST SUITE (ATS) ONE-CLICK RUNNER
echo ====================================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-tests.ps1"

pause
