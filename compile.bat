@echo off
echo ==========================================================
echo        Compiling Student Grade Tracker
echo ==========================================================

if not exist bin mkdir bin

javac -d bin src\com\app\gradetracker\*.java src\com\app\gradetracker\model\*.java src\com\app\gradetracker\cli\*.java src\com\app\gradetracker\gui\*.java src\com\app\gradetracker\util\*.java test\com\app\gradetracker\*.java

if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Compilation finished successfully! Classes located in bin/
) else (
    echo [ERROR] Compilation failed. Please check your Java installation.
)

pause
