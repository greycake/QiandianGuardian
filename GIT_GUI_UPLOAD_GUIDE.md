# 使用 Git GUI 上传到 GitHub

如果你有 Git GUI，可以按照以下步骤手动上传：

## 方法1：使用 Git CMD

1. **打开 Git CMD**
   - 在开始菜单搜索 "Git CMD"
   - 打开 Git CMD 窗口

2. **进入项目目录**
   ```bash
   cd C:\Users\hw\.easyclaw\workspace\QiandianGuardian
   ```

3. **初始化仓库**
   ```bash
   git init
   ```

4. **添加所有文件**
   ```bash
   git add .
   ```

5. **提交代码**
   ```bash
   git commit -m "Initial commit: 黔电卫士 v1.0"
   ```

6. **添加远程仓库**
   ```bash
   git remote add origin https://github.com/greycake/QiandianGuardian.git
   ```

7. **推送到 GitHub**
   ```bash
   git branch -M main
   git push -u origin main
   ```

8. **访问 GitHub Actions**
   - 打开浏览器访问：https://github.com/greycake/QiandianGuardian/actions
   - 等待构建完成（2-3分钟）
   - 下载 APK

---

## 方法2：使用 Git GUI

1. **打开 Git GUI**
   - 在开始菜单搜索 "Git GUI"
   - 打开 Git GUI

2. **打开现有仓库**
   - 菜单：File → Open
   - 选择：`C:\Users\hw\.easyclaw\workspace\QiandianGuardian`

3. **暂存所有文件**
   - 点击 "Stage Changed" 按钮
   - 或按 Ctrl+T

4. **提交代码**
   - 在 "Commit Message" 输入：`Initial commit: 黛电卫士 v1.0`
   - 点击 "Commit" 按钮

5. **推送到 GitHub**
   - 菜单：Remote → Push
   - Destination：`https://github.com/greycake/QiandianGuardian.git`
   - Branch：`main`
   - 点击 "Push"

6. **访问 GitHub Actions**
   - 打开浏览器访问：https://github.com/greycake/QiandianGuardian/actions
   - 等待构建完成（2-3分钟）
   - 下载 APK

---

## 方法3：使用自动脚本（推荐）

我为你创建了自动上传脚本，它会自动查找 Git：

1. **打开项目文件夹**
   ```
   C:\Users\hw\.easyclaw\workspace\QiandianGuardian
   ```

2. **双击运行**
   - 找到 `find_git_and_upload.bat` 文件
   - 双击运行

3. **按照提示操作**
   - 脚本会自动查找 Git
   - 如果找不到，会提示你输入 Git 路径
   - 完成后自动打开 GitHub Actions 页面

---

## 如果需要登录 GitHub

在推送时，可能会要求你登录 GitHub：

1. 会弹出浏览器窗口
2. 登录你的 GitHub 账户
3. 授权 Git 访问
4. 返回命令行窗口继续

---

## 完成！

上传成功后：

1. 访问 https://github.com/greycake/QiandianGuardian/actions
2. 等待构建完成（约 2-3 分钟）
3. 点击构建成功的任务
4. 在页面底部找到 "Artifacts"
5. 下载 `app-release` ZIP 文件
6. 解压得到 `app-release.apk`
7. 传输到手机安装
