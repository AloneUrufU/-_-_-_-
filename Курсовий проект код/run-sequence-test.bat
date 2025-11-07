@echo off
REM Run only the BookingSequenceTest (методом функціональних діаграм)
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

REM Keep classpath minimal to avoid picking other tests
set SEQDIR=test_sequence
set SEQFILE=test_sequence\BookingSequenceTest.java

REM Resolve libs from either lib/, autotests/lib/ or test_sequence/lib/
set JUNIT_JAR=lib\junit-4.13.2.jar
set HAMCREST_JAR=lib\hamcrest-core-1.3.jar
if exist "autotests\lib\junit-4.13.2.jar" set JUNIT_JAR=autotests\lib\junit-4.13.2.jar
if exist "autotests\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=autotests\lib\hamcrest-core-1.3.jar
if exist "test_sequence\lib\junit-4.13.2.jar" set JUNIT_JAR=test_sequence\lib\junit-4.13.2.jar
if exist "test_sequence\lib\hamcrest-core-1.3.jar" set HAMCREST_JAR=test_sequence\lib\hamcrest-core-1.3.jar

set CP=%JUNIT_JAR%;%HAMCREST_JAR%;test_sequence;.

javac -encoding UTF-8 -cp "%CP%" "%SEQFILE%"
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)

echo Running: test_sequence.BookingSequenceTest (classpath: %CP%)
java -cp "%CP%" org.junit.runner.JUnitCore test_sequence.BookingSequenceTest
set ERR=%ERRORLEVEL%
if "%CMDCMDLINE%" NEQ "" pause
exit /b %ERR%

