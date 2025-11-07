@echo off
REM Run only equivalent class tests (duration validation)
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set TESTFILE=test_equivalent\DurationValidationTest.java

REM Resolve libs from common locations
set JUNIT_JAR=lib\junit-4.13.2.jar
set HAMCREST_JAR=lib\hamcrest-core-1.3.jar
if exist "autotests\lib\junit-4.13.2.jar" set JUNIT_JAR=autotests\lib\junit-4.13.2.jar
if exist "autotests\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=autotests\lib\hamcrest-core-1.3.jar
if exist "test_suites\lib\junit-4.13.2.jar" set JUNIT_JAR=test_suites\lib\junit-4.13.2.jar
if exist "test_suites\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=test_suites\lib\hamcrest-core-1.3.jar
if exist "test_sequence\lib\junit-4.13.2.jar" set JUNIT_JAR=test_sequence\lib\junit-4.13.2.jar
if exist "test_sequence\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=test_sequence\lib\hamcrest-core-1.3.jar
if exist "test_equivalent\lib\junit-4.13.2.jar" set JUNIT_JAR=test_equivalent\lib\junit-4.13.2.jar
if exist "test_equivalent\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=test_equivalent\lib\hamcrest-core-1.3.jar

set CP=%JUNIT_JAR%;%HAMCREST_JAR%;test_equivalent;.

javac -encoding UTF-8 -cp "%CP%" "%TESTFILE%"
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)

echo Running: test_equivalent.DurationValidationTest (classpath: %CP%)
java -cp "%CP%" org.junit.runner.JUnitCore test_equivalent.DurationValidationTest
set ERR=%ERRORLEVEL%
if "%CMDCMDLINE%" NEQ "" pause
exit /b %ERR%

