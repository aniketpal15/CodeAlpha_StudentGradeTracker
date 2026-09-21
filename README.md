# Student Grade Tracker (Task 1)

[![Java Version](https://img.shields.io/badge/Java-8%20%7C%2011%20%7C%2017%20%7C%2021%20%7C%2025-blue.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](#license)
[![Internship](https://img.shields.io/badge/-Java%20Programming-orange.svg)](https://www.app.tech)

> **Task 1: Student Grade Tracker** for the ** Java Programming Internship**.  
> A Java application featuring both a **Modern Swing Graphical User Interface (GUI)** and an interactive **Command-Line Interface (CLI)** to input, manage, analyze, and report student academic performance.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Requirements Fulfilled](#-requirements-fulfilled)
- [Key Features](#-key-features)
- [Project Architecture](#-project-architecture)
- [Directory Structure](#-directory-structure)
- [Prerequisites](#-prerequisites)
- [How to Compile and Run](#-how-to-compile-and-run)
  - [Option 1: Windows Batch Scripts (Quickest)](#option-1-windows-batch-scripts-quickest)
  - [Option 2: PowerShell / Terminal](#option-2-powershell--terminal)
  - [Option 3: IDE (VS Code, IntelliJ IDEA, Eclipse)](#option-3-ide-vs-code-intellij-idea-eclipse)
- [Command-Line Arguments](#-command-line-arguments)
- [Automated Testing](#-automated-testing)
- [Sample Data & Demonstration](#-sample-data--demonstration)
- [Sample Output Preview](#-sample-output-preview)
- [Author & Submission Info](#-author--submission-info)

---

## 📖 Overview

The **Student Grade Tracker** is designed to enable educators and administrators to record and monitor student grades across courses, compute individual and class-wide statistics (mean average, top score, lowest score, GPA, and letter grades), and produce clean summary reports with export capabilities.

---

## ✅ Requirements Fulfilled

| Requirement | Implementation Detail | Status |
| :--- | :--- | :---: |
| **Input and manage student grades** | Add, edit, remove, and search students and grades dynamically. | ✅ Complete |
| **Calculate average, highest, lowest** | Implemented at both individual student level and class-wide level. | ✅ Complete |
| **Use arrays or ArrayLists** | Built using `ArrayList<Double>` for grades and `ArrayList<Student>` for the collection. | ✅ Complete |
| **Display a summary report** | Generates detailed ASCII tables and KPI statistics cards. | ✅ Complete |
| **Console-based or GUI-based** | **Both included:** Full interactive CLI menu + modern Swing desktop GUI. | ✅ Complete |

---

## 🚀 Key Features

### 1. Dual Interface Support
- **Modern Desktop GUI (Swing):**
  - KPI Stat Cards: Live updates for *Total Students*, *Class Average*, *Class Highest*, *Class Lowest*, and *Pass Rate %*.
  - Searchable `JTable`: Instant live filtering by Student ID or Name.
  - Interactive Form: Add, update, or remove student grades with validation.
  - Color-coded badges for **PASSED** (green) and **FAILED** (red).
  - Modal report viewer with one-click file saving.
- **Interactive Command-Line Interface (CLI):**
  - Clean numbered ASCII menu system.
  - Bulletproof input validation (prevents crashes from invalid numbers, blanks, or out-of-range inputs).
  - Visual grade distribution bar charts in terminal.

### 2. Comprehensive Statistical Analytics
- **Per Student:** Total score, arithmetic mean (average), highest score, lowest score, letter grade (A/B/C/D/F), standard 4.0 GPA, and passing status.
- **Class-Wide:** Class average, class highest individual score, class lowest individual score, top-performing student identification, lowest-performing student identification, passing vs. failing counts, and pass rate percentage.

### 3. File Persistence & Data Handling
- **CSV Import & Export:** Easily save student records to `.csv` or import existing rosters.
- **Text Report Export:** Export full summary reports directly to formatted `.txt` files.
- **Pre-loaded Demo Data:** Instant 1-click loading of realistic student datasets.

---

## 🏗 Project Architecture

The project follows standard Object-Oriented Design (OOD) and Separation of Concerns (SoC):

```
com.app.gradetracker
 ├── model/
 │    ├── Student.java        --> Represents a student entity with ArrayList<Double> grades
 │    └── GradeTracker.java   --> Manages ArrayList<Student>, calculations, CSV I/O, reports
 ├── cli/
 │    └── ConsoleUI.java      --> Interactive terminal UI with menus and input sanitization
 ├── gui/
 │    └── GradeTrackerGUI.java--> Swing GUI with live cards, JTable, filters, and modals
 ├── util/
 │    └── GradeUtils.java     --> Rounding, letter grades, GPA, and validation helpers
 └── Main.java                --> Central launcher with auto-detection and flag routing
```

---

## 📁 Directory Structure

```
_StudentGradeTracker/
├── src/
│   └── com/
│       └── app/
│           └── gradetracker/
│               ├── Main.java
│               ├── model/
│               │   ├── Student.java
│               │   └── GradeTracker.java
│               ├── cli/
│               │   └── ConsoleUI.java
│               ├── gui/
│               │   └── GradeTrackerGUI.java
│               └── util/
│                   └── GradeUtils.java
├── test/
│   └── com/
│       └── app/
│           └── gradetracker/
│               └── GradeTrackerTest.java
├── data/
│   └── sample_students.csv
├── compile.bat
├── compile.ps1
├── run-gui.bat
├── run-gui.ps1
├── run-console.bat
├── run-console.ps1
├── run-tests.bat
├── run-tests.ps1
├── .gitignore
└── README.md
```

---

## ⚙ Prerequisites

- **Java Development Kit (JDK):** Version 8, 11, 17, 21, or 25.
- Verify installation by running in your terminal:
  ```bash
  javac -version
  java -version
  ```

---

## 💻 How to Compile and Run

### Option 1: Windows Batch Scripts (Quickest)
Double-click or run from Command Prompt:
- **Compile project:** `compile.bat`
- **Launch GUI:** `run-gui.bat`
- **Launch Console CLI:** `run-console.bat`
- **Run Unit Tests:** `run-tests.bat`

### Option 2: PowerShell / Terminal

1. **Compile:**
   ```powershell
   # Compile all sources into bin directory
   javac -d bin src/com/app/gradetracker/*.java src/com/app/gradetracker/model/*.java src/com/app/gradetracker/cli/*.java src/com/app/gradetracker/gui/*.java src/com/app/gradetracker/util/*.java test/com/app/gradetracker/*.java
   ```

2. **Run GUI Mode:**
   ```powershell
   java -cp bin com.app.gradetracker.Main --gui
   ```

3. **Run Console (CLI) Mode:**
   ```powershell
   java -cp bin com.app.gradetracker.Main --cli
   ```

### Option 3: IDE (VS Code, IntelliJ IDEA, Eclipse)
- Open the `_StudentGradeTracker` directory as a Java project.
- Locate `src/com/app/gradetracker/Main.java`.
- Right-click and select **Run 'Main.main()'**.

---

## 🎛 Command-Line Arguments

| Flag | Description |
| :--- | :--- |
| *(no arguments)* | Automatically launches the **GUI** (or CLI if in headless environment). |
| `--gui`, `-g` | Explicitly forces the **Swing Graphical User Interface**. |
| `--cli`, `-c`, `--console` | Explicitly forces the interactive **Console Interface**. |
| `--help`, `-h` | Displays usage options and help information. |

---

## 🧪 Automated Testing

A dedicated test suite `GradeTrackerTest.java` is included, containing **42 automated assertions** with 0 external library dependencies:
- Verifies edge cases (boundary scores 0 and 100).
- Verifies rejection of negative scores (`< 0`) and scores exceeding 100.
- Verifies accuracy of average, highest, and lowest score computations.
- Verifies GPA and letter grade mappings.
- Verifies CSV export and import fidelity.

To run the test suite:
```powershell
java -cp bin com.app.gradetracker.GradeTrackerTest
```
**Output Preview:**
```
==========================================================
       RUNNING STUDENT GRADE TRACKER TESTS      
==========================================================
 [PASS] Student ID set correctly (S101)
 [PASS] Average score is 80.0 (expected=80.0, actual=80.0)
 [PASS] Highest score is 90.0 (expected=90.0, actual=90.0)
 [PASS] Lowest score is 70.0 (expected=70.0, actual=70.0)
 [PASS] Class average score (expected=71.67, actual=71.67)
 [PASS] Class highest score (expected=100.0, actual=100.0)
 [PASS] Class lowest score (expected=40.0, actual=40.0)
...
==========================================================
Test Results: 42 Passed | 0 Failed
==========================================================
```

---

## 📊 Sample Output Preview

```
=========================================================================================================
                            STUDENT GRADE TRACKER REPORT                             
=========================================================================================================
| ID       | NAME               | GRADES RECORDED          | AVG     | HIGH    | LOW     | GRADE  | STATUS  |
---------------------------------------------------------------------------------------------------------
| S101     | Emma Watson        | [95.0, 88.5, 92.0, 98... |   93.00 |   98.00 |   88.50 | A      | PASSED  |
| S102     | James Smith        | [78.0, 82.5, 69.0, 74... |   77.70 |   85.00 |   69.00 | C      | PASSED  |
| S103     | Sophia Miller      | [92.0, 96.0, 89.5, 94... |   94.10 |   99.00 |   89.50 | A      | PASSED  |
| S104     | Liam Brown         | [55.0, 62.0, 58.5, 61... |   57.30 |   62.00 |   50.00 | F      | FAILED  |
| S105     | Olivia Davis       | [84.0, 88.0, 81.5, 90... |   86.00 |   90.00 |   81.50 | B      | PASSED  |
| S106     | Noah Wilson        | [42.0, 50.0, 48.0, 59... |   50.10 |   59.00 |   42.00 | F      | FAILED  |
---------------------------------------------------------------------------------------------------------

------------------------------------------ CLASS STATISTICS ---------------------------------------------
 Total Students Registered    : 6
 Class Average Score          : 76.37%
 Class Highest Score          : 99.00
 Class Lowest Score           : 42.00
 Top Performing Student       : Sophia Miller (S103) with Average 94.10%
 Lowest Performing Student    : Noah Wilson (S106) with Average 50.10%
 Passing Students (>= 60%)    : 4
 Failing Students (< 60%)     : 2
 Overall Pass Rate            : 66.67%

 Grade Distribution:
   - Grade A :  2 student(s)  [#######             ]
   - Grade B :  1 student(s)  [###                 ]
   - Grade C :  1 student(s)  [###                 ]
   - Grade D :  0 student(s)  [                    ]
   - Grade F :  2 student(s)  [#######             ]
=========================================================================================================
```

---

## 👤 Author & Submission Info

- **Program:**  Internship
- **Domain:** Java Programming
- **Task:** Task 1 — Student Grade Tracker
- **GitHub Repository Name:** `_StudentGradeTracker`
