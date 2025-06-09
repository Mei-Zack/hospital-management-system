@echo off
chcp 65001 >nul
echo 正在启动医院管理系统...

rem 设置工作目录
set "projectRoot=%~dp0"
set "backendDir=%projectRoot%backend"
set "frontendDir=%projectRoot%frontend"

echo 正在启动后端服务...
start cmd /k "cd /d %backendDir% && .\mvnw.cmd spring-boot:run"

echo 等待后端服务启动...
timeout /t 10 /nobreak >nul

echo 正在启动前端服务...
start cmd /k "cd /d %frontendDir% && npm run dev"

echo 等待前端服务启动...
timeout /t 5 /nobreak >nul

echo 正在打开浏览器...
start http://localhost:3002

echo.
echo 医院管理系统已启动
echo 后端服务运行在 http://localhost:8080
echo 前端服务运行在 http://localhost:3002
echo.
echo 服务已在独立窗口中启动，关闭相应窗口可停止服务
echo.
pause 