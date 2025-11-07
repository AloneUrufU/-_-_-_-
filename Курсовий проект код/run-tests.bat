@echo off
REM Ensure the following JARs exist in the lib folder:
REM   - lib\junit-4.13.2.jar
REM   - lib\hamcrest-core-1.3.jar
REM If missing, download them from Maven Central and place into lib\

setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

REM Resolve libs from either lib/, autotests/lib/ or test_suites/lib/
set JUNIT_JAR=lib\junit-4.13.2.jar
set HAMCREST_JAR=lib\hamcrest-core-1.3.jar
if exist "autotests\lib\junit-4.13.2.jar" set JUNIT_JAR=autotests\lib\junit-4.13.2.jar
if exist "autotests\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=autotests\lib\hamcrest-core-1.3.jar
if exist "test_suites\lib\junit-4.13.2.jar" set JUNIT_JAR=test_suites\lib\junit-4.13.2.jar
if exist "test_suites\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=test_suites\lib\hamcrest-core-1.3.jar

set CP=%JUNIT_JAR%;%HAMCREST_JAR%;test_suites;.

REM Compile tests explicitly (Windows cmd does not expand ** globs)
javac -encoding UTF-8 -cp "%CP%" test_suites\BookingServiceTest.java
if errorlevel 1 (
  echo Compilation failed: BookingServiceTest.java
  exit /b 1
)
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)

REM Run JUnit tests (fully-qualified class names)
echo Running: test_suites.BookingServiceTest
java -cp "%CP%" org.junit.runner.JUnitCore test_suites.BookingServiceTest
set ERR=%ERRORLEVEL%
if %ERR% NEQ 0 exit /b %ERR%
exit /b 0

