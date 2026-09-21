@echo off
echo Starting Student Grade Tracker (GUI)...

if not exist bin\com (
    echo Compiled classes not found. Compiling first...
    call compile.bat
)

start javaw -cp bin com.app.gradetracker.Main --gui
