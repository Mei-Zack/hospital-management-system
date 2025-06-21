@echo off
chcp 65001 >nul
echo 医院管理系统(HIMS) - 权限修复工具
echo =====================================
echo.
echo 此脚本将尝试修复系统中可能存在的权限问题
echo 请确保您以管理员身份运行此脚本

rem 检查是否以管理员身份运行
net session >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 此脚本需要管理员权限才能运行
    echo 请右键点击此脚本，选择"以管理员身份运行"
    pause
    exit /b 1
)

rem 设置工作目录
set "projectRoot=%~dp0"
set "backendDir=%projectRoot%backend"
set "frontendDir=%projectRoot%frontend"

echo 正在修复项目文件权限...
echo.

rem 修复后端目录权限
echo 修复后端目录权限...
icacls "%backendDir%" /grant:r "%USERNAME%:(OI)(CI)F" /T
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 无法完全修复后端目录权限
) else (
    echo [成功] 后端目录权限已修复
)

rem 修复前端目录权限
echo 修复前端目录权限...
icacls "%frontendDir%" /grant:r "%USERNAME%:(OI)(CI)F" /T
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 无法完全修复前端目录权限
) else (
    echo [成功] 前端目录权限已修复
)

rem 修复Maven Wrapper权限
echo 修复Maven Wrapper执行权限...
icacls "%backendDir%\mvnw.cmd" /grant:r "%USERNAME%:F"
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 无法修复Maven Wrapper权限
) else (
    echo [成功] Maven Wrapper权限已修复
)

rem 修复启动脚本权限
echo 修复启动脚本权限...
icacls "%projectRoot%\start-hims.bat" /grant:r "%USERNAME%:F"
icacls "%projectRoot%\start-hims-prod.bat" /grant:r "%USERNAME%:F"
icacls "%projectRoot%\start-hims-config.bat" /grant:r "%USERNAME%:F"
icacls "%projectRoot%\install-dependencies.bat" /grant:r "%USERNAME%:F"
icacls "%projectRoot%\create-database.bat" /grant:r "%USERNAME%:F"

echo.
echo 正在清理临时文件...
if exist "%frontendDir%\.vite" (
    rmdir /s /q "%frontendDir%\.vite"
    echo [成功] 已清理Vite缓存
)

if exist "%frontendDir%\node_modules\.cache" (
    rmdir /s /q "%frontendDir%\node_modules\.cache"
    echo [成功] 已清理npm缓存
)

echo.
echo =====================================
echo 权限修复完成！
echo.
echo 如果您仍然遇到权限问题，请尝试以下操作：
echo 1. 确保您的用户账户对项目目录有完全控制权限
echo 2. 尝试以管理员身份运行所有批处理脚本
echo 3. 检查杀毒软件是否阻止了某些操作
echo =====================================
pause 