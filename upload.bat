@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Qiandian Guardian - Upload to GitHub
echo ========================================
echo.

REM Try common Git installation paths
set GIT_CMD=
.exe
if exist "C:\Program Files\Git\bin\git.exe" set GIT_CMD=C:\Program Files\Git\bin\git.exe
if exist "C:\Program Files\Git\cmd\git.exe" set GIT_CMD=C:\Program Files\Git\cmd\git.exe
if exist "C:\Program Files (x86)\Git\bin\git.exe" set GIT_CMD=C:\Program Files (x86)\Git\bin\git.exe
if exist "C:\Program Files (x86)\Git\cmd\git.exe" set GIT_CMD=C:\Program Files (x86)\Git\cmd\git.exe
if exist "C:\Users\%USERNAME%\AppData\Local\Programs\Git\bin\git.exe" set GIT_CMD=C:\Users\%USERNAME%\AppData\Local\Programs\Git\bin\git.exe
if exist "%USERPROFILE%\AppData\Local\Programs\Git\bin\git.exe" set GIT_CMD=%USERPROFILE%\AppData\Local\Programs\Git\bin\git.exe

if "%GIT_CMD%"=="" (
    echo ERROR: Git not found!
    echo.
    echo Please enter the full path to git.exe
    echo Example: C:\Program Files\Git\bin\git.exe
    echo.
    set /p GIT_CMD="Git path: "
)

if not exist "%GIT_CMD%" (
    echo ERROR: File not found: %GIT_CMD%
    pause
    exit /b 1
)

echo Found Git: %GIT_CMD%
echo.

cd /d "%~dp0"

echo [1/6] Initializing Git repository...
"%GIT_CMD%" init
if errorlevel 1 (
    echo Git init failed
    pause
    exit /b 1
)
echo Done.
echo.

echo [2/6] Adding all files...
"%GIT_CMD%" add .
echo Done.
echo.

echo [3/6] Committing code...
"%GIT_CMD%" commit -m "Initial commit: Qiandian Guardian v1.0"
echo Done.
echo.

echo [4/6] Adding remote repository...
"%GIT_CMD%" remote add origin https://github.com/greycake/QiandianGuardian.git
echo Done.
echo.

echo [5/6] Pushing to GitHub...
"%GIT_CMD%" branch -M main
"%GIT_CMD%" push -u origin main
if errorlevel 1 (
    echo.
    echo Push failed! You may need to login to GitHub.
    echo.
    echo Please check:
    echo 1. Repository exists
    echo 2. Network connection
    echo 3. GitHub authentication
    echo.
    pause
    exit /b 1
)
echo Done.
echo.

echo [6/6] Complete!
echo.
echo ========================================
echo Upload successful!
echo ========================================
echo.
echo Next steps:
echo 1. Visit https://github.com/greycake/QiandianGuardian/actions
echo 2. Wait for build to complete (2-3 minutes)
echo 3. Download the APK file
echo.
echo Press any key to open GitHub Actions page...
pause >nul

start https://github.com/greycake/QiandianGuardian/actions
