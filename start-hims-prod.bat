@echo off
chcp 65001 >nul
echo 正在启动医院管理系统(生产环境)...

rem 设置工作目录
set "projectRoot=%~dp0"
set "backendDir=%projectRoot%backend"
set "frontendDir=%projectRoot%frontend"
set "configFile=%projectRoot%start-hims-config.bat"

rem 加载配置文件
if exist "%configFile%" (
    echo 正在加载配置文件...
    call "%configFile%"
) else (
    echo 未找到配置文件，将使用默认配置
    set MYSQL_HOST=localhost
    set MYSQL_PORT=3306
    set MYSQL_DATABASE=hims
    set MYSQL_USERNAME=root
    set MYSQL_PASSWORD=65353804778
    set BACKEND_PORT=8080
    set FRONTEND_PORT=3002
    set STARTUP_DELAY_BACKEND=15
    set STARTUP_DELAY_FRONTEND=8
)

rem 设置Java路径
if not "%JAVA_PATH%"=="" (
    set "PATH=%JAVA_PATH%;%PATH%"
)

rem 设置Node路径
if not "%NODE_PATH%"=="" (
    set "PATH=%NODE_PATH%;%PATH%"
)

rem 检查Java环境
echo 检查Java环境...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未检测到Java环境，请安装JDK 8或更高版本
    echo 您可以从以下地址下载JDK: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
    echo 或在start-hims-config.bat中设置JAVA_PATH变量
    goto :error
)

rem 检查Node.js环境
echo 检查Node.js环境...
node -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未检测到Node.js环境，请安装Node.js 14或更高版本
    echo 您可以从以下地址下载Node.js: https://nodejs.org/
    echo 或在start-hims-config.bat中设置NODE_PATH变量
    goto :error
)

rem 检查MySQL服务
echo 检查MySQL服务...
sc query MySQL >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [警告] MySQL服务可能未启动，请确保MySQL已安装并正在运行
    echo 您可以从以下地址下载MySQL: https://dev.mysql.com/downloads/installer/
    echo 当前配置的MySQL连接信息:
    echo   主机: %MYSQL_HOST%
    echo   端口: %MYSQL_PORT%
    echo   数据库: %MYSQL_DATABASE%
    echo   用户名: %MYSQL_USERNAME%
    choice /C YN /M "是否继续启动系统？"
    if %ERRORLEVEL% EQU 2 goto :error
)

rem 检查后端目录
if not exist "%backendDir%\mvnw.cmd" (
    echo [错误] 未找到后端启动文件: %backendDir%\mvnw.cmd
    goto :error
)

rem 检查前端目录
if not exist "%frontendDir%\package.json" (
    echo [错误] 未找到前端配置文件: %frontendDir%\package.json
    goto :error
)

echo 正在构建前端生产版本...
cd /d "%frontendDir%" && npm install && npm run build
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 构建前端生产版本失败
    goto :error
)

rem 创建临时的application-local.yml文件
echo 正在配置后端数据库连接...
(
echo spring:
echo   datasource:
echo     url: jdbc:mysql://%MYSQL_HOST%:%MYSQL_PORT%/%MYSQL_DATABASE%?useUnicode=true^&characterEncoding=utf-8^&serverTimezone=Asia/Shanghai^&useSSL=false^&allowPublicKeyRetrieval=true
echo     username: %MYSQL_USERNAME%
echo     password: %MYSQL_PASSWORD%
echo server:
echo   port: %BACKEND_PORT%
) > "%backendDir%\src\main\resources\application-local.yml"

echo 环境检查完成，开始启动服务...

echo 正在启动后端服务(生产环境，端口: %BACKEND_PORT%)...
start cmd /k "cd /d "%backendDir%" && call .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod,local"

echo 等待后端服务启动...
echo 正在等待后端服务启动，请稍候...
timeout /t %STARTUP_DELAY_BACKEND% /nobreak >nul

echo 正在启动前端服务(生产环境预览，端口: %FRONTEND_PORT%)...
cd /d "%frontendDir%" && npx serve -s dist -l %FRONTEND_PORT%

echo.
echo 医院管理系统(生产环境)已启动
echo 后端服务运行在 http://localhost:%BACKEND_PORT%
echo 前端服务运行在 http://localhost:%FRONTEND_PORT%
echo.
echo 如遇到问题，请检查:
echo 1. MySQL服务是否正常运行
echo 2. 端口%BACKEND_PORT%和%FRONTEND_PORT%是否被占用
echo 3. 防火墙是否阻止了应用程序
echo 4. 可以修改start-hims-config.bat文件调整配置
echo.
goto :end

:error
echo.
echo 启动失败，请解决上述问题后重试
pause
exit /b 1

:end 