@echo off
chcp 65001 >nul
echo ========================================
echo 黔电卫士 - GitHub 上传脚本
echo ========================================
echo.

echo [1/6] 初始化 Git 仓库...
cd /d "%~dp0"
git init
if errorlevel 1 (
    echo.
    echo 错误：未找到 Git！
    echo.
    echo 请先安装 Git：
    echo https://git-scm.com/download/win
    echo.
    pause
    exit /b 1
)
echo ✓ Git 仓库初始化完成
echo.

echo [2/6] 添加所有文件...
git add .
echo ✓ 文件已添加
echo.

echo [3/6] 提交代码...
git commit -m "Initial commit: 黔电卫士 v1.0 - 贵州电话短信拦截器"
echo ✓ 代码已提交
echo.

echo [4/6] 添加远程仓库...
git remote add origin https://github.com/greycake/QiandianGuardian.git
echo ✓ 远程仓库已添加
echo.

echo [5/6] 推送到 GitHub...
git branch -M main
git push -u origin main
if errorlevel 1 (
    echo.
    echo 推送失败！可能的原因：
    echo 1. 需要登录 GitHub（会弹出浏览器窗口）
    echo 2. 仓库不存在或 URL 错误
    echo 3. 网络连接问题
    echo.
    echo 请检查后重试
    pause
    exit /b 1
)
echo ✓ 代码已推送到 GitHub
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
