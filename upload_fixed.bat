@echo off
echo ========================================
echo Qiandian Guardian - Upload to GitHub
echo ========================================
echo.

cd /d "%~dp0"

echo [1/6] Initializing Git repository...
"D:\Program Files\Git\cmd\git.exe" init
if errorlevel 1 (
    echo Git init failed
    pause
    exit /b 1
)
echo Done.
echo.

echo [2/6] Adding all files...
"D:\Program Files\Git\cmd\git.exe" add .
echo Done.
echo.

echo [3/6] Committing code...
"D:\Program Files\Git\cmd\git.exe" commit -m "Initial commit: Qiandian Guardian v1.0"
echo Done.
echo.

echo [4/6] Adding remote repository...
"D:\Program Files\Git\cmd\git.exe" remote add origin https://github.com/greycake/QiandianGuardian.git
echo Done.
echo.

echo [5/6] Pushing to GitHub...
"D:\Program Files\Git\cmd\git.exe" branch -M main
"D:\Program Files\Git\cmd\git.exe" push -u origin main
if errorlevel 1 (
    echo.
    echo Push failed! You may need to login to GitHub.
    echo.
    echo Please check:
    echo 1. Repository repository exists
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
