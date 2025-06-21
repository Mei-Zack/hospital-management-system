@echo off
echo 正在启动HIMS系统(PowerShell兼容版)...

REM 检查配置文件是否存在
if not exist "start-hims-config.bat" (
    echo 配置文件不存在，正在创建默认配置...
    echo @echo off > start-hims-config.bat
    echo REM HIMS系统配置 >> start-hims-config.bat
    echo set JAVA_HOME=C:\Program Files\Java\jdk-17 >> start-hims-config.bat
    echo set NODE_PATH=C:\Program Files\nodejs >> start-hims-config.bat
    echo set BACKEND_PORT=3002 >> start-hims-config.bat
    echo set FRONTEND_PORT=5173 >> start-hims-config.bat
    echo set DB_URL=jdbc:mysql://localhost:3306/hims >> start-hims-config.bat
    echo set DB_USERNAME=root >> start-hims-config.bat
    echo set DB_PASSWORD=root >> start-hims-config.bat
    echo set ACTIVE_PROFILE=local >> start-hims-config.bat
    echo 已创建默认配置文件，请根据需要修改 start-hims-config.bat
)

REM 加载配置
call start-hims-config.bat

REM 检查Java环境
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Java未安装或未添加到PATH，尝试使用配置的JAVA_HOME...
    if exist "%JAVA_HOME%\bin\java.exe" (
        set PATH=%JAVA_HOME%\bin;%PATH%
    ) else (
        echo 错误：找不到Java。请安装JDK 17或在start-hims-config.bat中设置正确的JAVA_HOME路径。
        pause
        exit /b 1
    )
)

REM 检查Node环境
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Node.js未安装或未添加到PATH，尝试使用配置的NODE_PATH...
    if exist "%NODE_PATH%\node.exe" (
        set PATH=%NODE_PATH%;%PATH%
    ) else (
        echo 错误：找不到Node.js。请安装Node.js或在start-hims-config.bat中设置正确的NODE_PATH路径。
        pause
        exit /b 1
    )
)

REM 创建本地配置文件（如果不存在）
if not exist "backend\src\main\resources\application-local.yml" (
    echo 创建本地配置文件...
    echo spring: > backend\src\main\resources\application-local.yml
    echo   datasource: >> backend\src\main\resources\application-local.yml
    echo     url: %DB_URL% >> backend\src\main\resources\application-local.yml
    echo     username: %DB_USERNAME% >> backend\src\main\resources\application-local.yml
    echo     password: %DB_PASSWORD% >> backend\src\main\resources\application-local.yml
    echo server: >> backend\src\main\resources\application-local.yml
    echo   port: %BACKEND_PORT% >> backend\src\main\resources\application-local.yml
)

REM 启动后端（PowerShell兼容方式）
start powershell -NoProfile -ExecutionPolicy Bypass -Command "cd backend; ./mvnw spring-boot:run -Dspring-boot.run.profiles=%ACTIVE_PROFILE%"

REM 等待后端启动
echo 等待后端服务启动...
timeout /t 10 /nobreak > nul

REM 启动前端（PowerShell兼容方式）
start powershell -NoProfile -ExecutionPolicy Bypass -Command "cd frontend; npm run dev"

echo HIMS系统启动成功！
echo 后端地址：http://localhost:%BACKEND_PORT%
echo 前端地址：http://localhost:%FRONTEND_PORT%
echo.
echo 按任意键退出此窗口（不会关闭系统）...
pause > nul 