@echo off
echo Starting Student Grade Tracker (GUI)...

if not exist bin (
    echo bin directory not found. Compiling first...
    call compile.bat
)

start javaw -cp bin com.app.gradetracker.Main --gui
