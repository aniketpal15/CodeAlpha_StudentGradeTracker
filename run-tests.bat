@echo off
if not exist bin\com (
    echo Compiled classes not found. Compiling first...
    call compile.bat
)

java -cp bin com.app.gradetracker.GradeTrackerTest
pause
