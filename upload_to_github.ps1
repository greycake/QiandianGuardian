# 黔电卫士 - GitHub 上传脚本 (PowerShell 版本)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
" 黛电卫士 - GitHub 上传脚本"
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Git 是否安装
Write-Host "[1/6] 检查 Git..." -ForegroundColor Yellow
try {
    $gitVersion = git --version
    Write-Host "✓ $gitVersion" -ForegroundColor Green
} catch {
    Write-Host ""
    Write-Host "错误：未找到 Git！" -ForegroundColor Red
    Write-Host ""
    Write-Host "请先安装 Git：" -ForegroundColor Yellow
    Write-Host "https://git-scm.com/download/win" -ForegroundColor Cyan
    Write-Host ""
    Read-Host "按 Enter 键退出"
    exit 1
}

# 切换到脚本所在目录
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# 初始化 Git 仓库
Write-Host "[2/6] 初始化 Git 仓库..." -ForegroundColor Yellow
git init
Write-Host "✓ Git 仓库初始化完成" -ForegroundColor Green

# 添加所有文件
Write-Host "[3/6] 添加所有文件..." -ForegroundColor Yellow
git add .
Write-Host "✓ 文件已添加" -ForegroundColor Green

# 提交代码
Write-Host "[4/6] 提交代码..." -ForegroundColor Yellow
git commit -m "Initial commit: 黔电卫士 v1.0 - 贵州电话短信拦截器"
Write-Host "✓ 代码已提交" -ForegroundColor Green

# 添加远程仓库
Write-Host "[5/6] 添加远程仓库..." -ForegroundColor Yellow
git remote add origin https://github.com/greycake/QiandianGuardian.git
Write-Host "✓ 远程仓库已添加" -ForegroundColor Green

# 推送到 GitHub
Write-Host "[6/6] 推送到 GitHub..." -ForegroundColor Yellow
git branch -M main
try {
    git push -u origin main
    Write-Host "✓ 代码已推送到 GitHub" -ForegroundColor Green
} catch {
    Write-Host ""
    Write-Host "推送失败！" -ForegroundColor Red
    Write-Host ""
    Write-Host "可能的原因：" -ForegroundColor Yellow
    Write-Host "1. 需要登录 GitHub（会弹出浏览器窗口）"
    Write-Host "2. 仓库不存在或 URL 错误"
    Write-Host "3. 网络连接问题"
    Write-Host ""
    Write-Host "请检查后重试" -ForegroundColor Yellow
    Read-Host "按 Enter 键退出"
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host " 上传成功！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "下一步：" -ForegroundColor Yellow
Write-Host "1. 访问 https://github.com/greycake/QiandianGuardian/actions" -ForegroundColor Cyan
Write-Host "2. 等待构建完成（约 2-3 分钟）" -ForegroundColor Cyan
Write-Host "3. 下载生成的 APK 文件" -ForegroundColor Cyan
Write-Host ""

# 询问是否打开 GitHub Actions 页面
$openActions = Read-Host "是否打开 GitHub Actions 页面？(Y/n)"
if ($openActions -ne "n" -and $openActions -ne "N") {
    Start-Process "https://github.com/greycake/QiandianGuardian/actions"
}

Write-Host ""
Write-Host "完成！" -ForegroundColor Green
