@echo off
if not exist bin (
    echo bin directory not found. Compiling first...
    call compile.bat
)

java -cp bin com.app.gradetracker.GradeTrackerTest
pause
