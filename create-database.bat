@echo off
chcp 65001 >nul
echo 医院管理系统(HIMS) - 数据库初始化脚本
echo =====================================
echo.

rem 设置工作目录
set "projectRoot=%~dp0"
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
)

echo 即将创建数据库: %MYSQL_DATABASE%
echo 主机: %MYSQL_HOST%
echo 端口: %MYSQL_PORT%
echo 用户名: %MYSQL_USERNAME%
echo.

choice /C YN /M "确认以上信息并继续?"
if %ERRORLEVEL% EQU 2 goto :end

echo.
echo 正在尝试连接MySQL服务器...

rem 创建临时SQL文件
set "tempSqlFile=%TEMP%\create_hims_db.sql"
echo CREATE DATABASE IF NOT EXISTS %MYSQL_DATABASE% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; > "%tempSqlFile%"
echo USE %MYSQL_DATABASE%; >> "%tempSqlFile%"

rem 检查mysql命令是否可用
mysql --version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo 使用mysql命令行工具创建数据库...
    mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USERNAME% -p%MYSQL_PASSWORD% < "%tempSqlFile%"
    if %ERRORLEVEL% EQU 0 (
        echo [成功] 数据库 %MYSQL_DATABASE% 已创建或已存在
    ) else (
        echo [错误] 数据库创建失败，请检查MySQL连接信息
        goto :error
    )
) else (
    echo MySQL命令行工具不可用，请手动执行以下SQL语句创建数据库:
    echo.
    echo CREATE DATABASE IF NOT EXISTS %MYSQL_DATABASE% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    echo.
    echo 您可以通过以下方式手动创建:
    echo 1. 打开MySQL命令行或图形化工具(如MySQL Workbench)
    echo 2. 连接到您的MySQL服务器
    echo 3. 执行上述SQL语句
    echo.
    pause
    goto :end
)

echo.
echo 数据库初始化完成后，您可以运行start-hims.bat启动系统
echo 系统首次启动时会自动创建所需的表和初始数据
echo.
goto :end

:error
echo.
echo [错误] 数据库初始化失败，请解决上述问题后重试
pause
exit /b 1

:end
echo.
echo 操作完成
pause 