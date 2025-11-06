@echo off
REM Ensure the following JARs exist in the lib folder:
REM   - lib\junit-4.13.2.jar
REM   - lib\hamcrest-core-1.3.jar
REM If missing, download them from Maven Central and place into lib\

setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set CP=lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar;autotests;.

REM Compile tests
javac -encoding UTF-8 -cp "%CP%" autotests\*.java
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)

REM Run JUnit tests
java -cp "%CP%" org.junit.runner.JUnitCore BookingServiceTest RegistrationServiceTest
exit /b %errorlevel%

