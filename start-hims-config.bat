@echo off
rem 医院管理系统配置文件 - 可根据实际环境进行修改

rem MySQL配置
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_DATABASE=hims
set MYSQL_USERNAME=root
set MYSQL_PASSWORD=65353804778

rem 服务端口配置
set BACKEND_PORT=8080
set FRONTEND_PORT=3002

rem JDK路径配置（如果已设置JAVA_HOME环境变量，可以保留为空）
set JAVA_PATH=

rem Node.js路径配置（如果已添加到PATH环境变量，可以保留为空）
set NODE_PATH=

rem 其他配置
set STARTUP_DELAY_BACKEND=15
set STARTUP_DELAY_FRONTEND=8 