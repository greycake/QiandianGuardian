@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo 黔电卫士 - 查找 Git 并上传
echo ========================================
echo.

REM 尝试常见的 Git 安装路径
set GIT_PATHS[0]=C:\Program Files\Git\bin\git.exe
set GIT_PATHS[1]=C:\Program Files\Git\cmd\git.exe
set GIT_PATHS[2]=C:\Program Files (x86)\Git\bin\git.exe
set GIT_PATHS[3]=C:\Program Files (x86)\Git\cmd\git.exe
set GIT_PATHS[4]=C:\Users\%USERNAME%\AppData\Local\Programs\Git\bin\git.exe
set GIT_PATHS[5]=%USERPROFILE%\AppData\Local\Programs\Git\bin\git.exe

set GIT_CMD=
for /L %%i in (0,1,5) do (
    if exist "!GIT_PATHS[%%i]!" (
        set GIT_CMD=!GIT_PATHS[%%i]!
        echo 找到 Git: !GIT_CMD!
        goto :found_git
    )
)

echo.
echo 错误：未找到 Git！
echo.
echo 请手动输入 Git 的完整路径（例如：C:\Program Files\Git\bin\git.exe）
echo.
set /p GIT_CMD="Git 路径: "

if not exist "!GIT_CMD!" (
    echo.
    echo 错误：文件不存在：!GIT_CMD!
    pause
    exit /b 1
)

:found_git
echo.
echo ========================================
echo 开始上传到 GitHub
echo ========================================
echo.

cd /d "%~dp0"

echo [1/6] 初始化 Git 仓库...
"%GIT_CMD%" init
if errorlevel 1 (
    echo Git 初始化失败
    pause
    exit /b 1
)
echo ✓ 完成
echo.

echo [2/6] 添加所有文件...
"%GIT_CMD%" add .
echo ✓ 完成
echo.

echo [3/6] 提交代码...
"%GIT_CMD%" commit -m "Initial commit: 黔电卫士 v1.0 - 贵州电话短信拦截器"
echo ✓ 完成
echo.

echo [4/6] 添加远程仓库...
"%GIT_CMD%" remote add origin https://github.com/greycake/QiandianGuardian.git
echo ✓ 完成
echo.

echo [5/6] 推送到 GitHub...
"%GIT_CMD%" branch -M main
"%GIT_CMD%" push -u origin main
if errorlevel 1 (
    echo.
    echo 推送失败！可能需要登录 GitHub
    echo.
    echo 请检查：
    echo 1. 仓库是否存在
    echo 2. 网络连接
    echo 3. GitHub 认证
    echo.
    pause
    exit /b 1
)
echo ✓ 完成
echo.

echo [6/6] 完成！
echo.
echo ========================================
echo 上传成功！
echo ========================================
echo.
echo 下一步：
echo 1. 访问 https://github.com/greycake/QiandianGuardian/actions
echo 2. 等待构建完成（约 2-3 分钟）
echo 3. 下载生成的 APK 文件
echo.
echo 按任意键打开 GitHub Actions 页面...
pause >nul

start https://github.com/greycake/QiandianGuardian/actions
