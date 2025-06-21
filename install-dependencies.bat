@echo off
chcp 65001 >nul
echo 医院管理系统(HIMS) - 依赖安装脚本
echo =====================================
echo.

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
)

rem 设置Java路径
if not "%JAVA_PATH%"=="" (
    set "PATH=%JAVA_PATH%;%PATH%"
)

rem 设置Node路径
if not "%NODE_PATH%"=="" (
    set "PATH=%NODE_PATH%;%PATH%"
)

echo 检查Java环境...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未检测到Java环境，请安装JDK 8或更高版本
    echo 您可以从以下地址下载JDK: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
    echo 或在start-hims-config.bat中设置JAVA_PATH变量
    goto :error
) else (
    java -version
    echo [成功] Java环境检测通过
)

echo 检查Node.js环境...
node -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未检测到Node.js环境，请安装Node.js 14或更高版本
    echo 您可以从以下地址下载Node.js: https://nodejs.org/
    echo 或在start-hims-config.bat中设置NODE_PATH变量
    goto :error
) else (
    node -v
    echo [成功] Node.js环境检测通过
)

echo 检查npm...
npm -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未检测到npm，请重新安装Node.js
    goto :error
) else (
    npm -v
    echo [成功] npm检测通过
)

echo 检查MySQL...
sc query MySQL >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 未检测到MySQL服务，请确保MySQL已安装并正在运行
    echo 您可以从以下地址下载MySQL: https://dev.mysql.com/downloads/installer/
    echo 当前配置的MySQL连接信息:
    echo   主机: %MYSQL_HOST%
    echo   端口: %MYSQL_PORT%
    echo   数据库: %MYSQL_DATABASE%
    echo   用户名: %MYSQL_USERNAME%
    choice /C YN /M "是否继续安装其他依赖？"
    if %ERRORLEVEL% EQU 2 goto :error
) else (
    echo [成功] MySQL服务检测通过
)

echo.
echo 开始安装前端依赖...
if not exist "%frontendDir%\package.json" (
    echo [错误] 未找到前端配置文件: %frontendDir%\package.json
    goto :error
)

cd /d "%frontendDir%"
echo 清理npm缓存...
call npm cache clean --force
echo 安装前端依赖(这可能需要几分钟时间)...
call npm install --legacy-peer-deps

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 安装前端依赖失败，尝试使用cnpm...
    
    echo 检查cnpm是否已安装...
    call npm list -g cnpm >nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        echo 安装cnpm...
        call npm install -g cnpm --registry=https://registry.npmmirror.com
    )
    
    echo 使用cnpm安装依赖...
    call cnpm install --legacy-peer-deps
    
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] 使用cnpm安装依赖也失败了
        goto :error
    )
) else (
    echo [成功] 前端依赖安装完成
)

echo.
echo 安装全局依赖...
call npm install -g serve

echo.
echo 检查后端Maven包装器...
if not exist "%backendDir%\mvnw.cmd" (
    echo [错误] 未找到后端Maven包装器: %backendDir%\mvnw.cmd
    goto :error
)

echo 预下载Maven依赖...
cd /d "%backendDir%"
call .\mvnw.cmd dependency:go-offline

if %ERRORLEVEL% NEQ 0 (
    echo [警告] Maven依赖预下载失败，但这不会影响系统运行
) else (
    echo [成功] Maven依赖预下载完成
)

echo.
echo =====================================
echo 所有依赖安装完成！
echo 现在您可以运行start-hims.bat启动系统
echo =====================================
goto :end

:error
echo.
echo [错误] 依赖安装失败，请解决上述问题后重试
pause
exit /b 1

:end
pause 