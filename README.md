# 医院管理系统 (HIMS)

## 项目简介

医院管理系统(HIMS)是一个基于 Spring Boot 和 Vue 3 的全栈应用程序，旨在帮助医院管理预约挂号、医生排班、患者信息等功能。系统采用前后端分离架构，前端使用 Vue 3 + Element Plus 构建用户界面，后端使用 Spring Boot 提供 API 服务。
![image](https://github.com/user-attachments/assets/d146722d-9705-4064-b0f7-1a6a5cc91e46)


G:\StudentCode\HIMS\PixPin_2025-06-09_20-12-57.png

## 技术栈

### 后端

- Spring Boot 2.7.0
- Spring Security
- MyBatis-Plus 3.5.2
- JWT 认证
- MySQL 数据库

### 前端

- Vue 3
- TypeScript
- Element Plus
- Echarts
- Pinia (状态管理)
- Vue Router

## 系统功能

- 用户认证与授权
- 医生管理
- 患者管理
- 预约挂号
- 科室管理
- 数据统计与可视化

## 快速开始

### 环境要求

- JDK 8+
- Node.js 14+
- Maven 3.6+
- MySQL 5.7+

### 首次使用

如果您是首次使用本系统，请按照以下步骤操作：

1. 双击运行`create-database.bat`创建 MySQL 数据库
2. 双击运行`install-dependencies.bat`安装所有必要的依赖
3. 根据提示完成依赖安装
4. 安装完成后，使用下方的启动方式启动系统

### 启动方式

#### 方式一：使用批处理脚本(推荐)

1. 双击运行项目根目录下的`start-hims.bat`文件
2. 系统将自动检查环境并启动前端和后端服务
3. 如遇问题，请参考脚本提示和下方的常见问题解决方案

#### 方式二：生产环境启动

1. 双击运行项目根目录下的`start-hims-prod.bat`文件
2. 系统将构建前端生产版本并以生产模式启动服务

#### 方式三：自定义配置启动

1. 编辑`start-hims-config.bat`文件，根据您的环境设置参数
2. 双击运行`start-hims.bat`或`start-hims-prod.bat`启动系统

#### 方式四：PowerShell 兼容启动（适用于 PowerShell 环境）

1. 如果在 PowerShell 中遇到"&&"不是有效语句分隔符的错误
2. 双击运行`start-hims-powershell.bat`文件启动系统
3. 此脚本专为 PowerShell 环境优化，解决了命令连接符问题

#### 方式五：手动启动

**启动后端:**

```bash
cd backend
./mvnw spring-boot:run
```

**启动前端:**

```bash
cd frontend
npm install
npm run dev
```

### 常见问题

1. 如果遇到"&&"不是有效语句分隔符的错误，请使用 PowerShell 兼容版启动脚本：

   ```
   start-hims-powershell.bat
   ```

2. 如果需要自动安装依赖：

   ```
   install-dependencies.bat
   ```

3. 如果需要创建数据库：

   ```
   create-database.bat
   ```

4. 如果遇到文件权限问题：
   ```
   fix-permissions.bat
   ```

### 访问系统

- 后端 API 地址: http://localhost:8080
- 前端页面地址: http://localhost:3002

## 系统账号

系统预设了以下账号，可用于测试：

- 管理员账号:

  - 用户名: admin
  - 密码: admin123

## 项目结构

```
HIMS/
├── backend/                # 后端项目
│   ├── src/                # 源代码
│   │   ├── main/
│   │   │   ├── java/com/hims/
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── mapper/        # MyBatis映射
│   │   │   │   ├── security/      # 安全配置
│   │   │   │   ├── service/       # 服务层
│   │   │   │   └── HimsApplication.java  # 启动类
│   │   │   └── resources/         # 资源文件
│   │   │       ├── application.yml  # 应用配置
│   │   │       └── mapper/        # MyBatis XML
│   │   │
│   └── pom.xml              # Maven配置
├── frontend/               # 前端项目
│   ├── public/             # 静态资源
│   ├── src/                # 源代码
│   │   ├── assets/         # 资源文件
│   │   ├── components/     # 组件
│   │   ├── router/         # 路由
│   │   ├── stores/         # Pinia状态
│   │   ├── views/          # 视图
│   │   ├── App.vue         # 根组件
│   │   └── main.ts         # 入口文件
│   ├── index.html          # HTML模板
│   └── package.json        # NPM配置
├── start-hims.bat          # 开发环境快速启动脚本
├── start-hims-prod.bat     # 生产环境启动脚本
├── start-hims-config.bat   # 系统配置文件
├── install-dependencies.bat # 依赖安装脚本
├── create-database.bat     # 数据库初始化脚本
└── fix-permissions.bat     # 权限修复工具
```

## 辅助脚本说明

系统提供了多个辅助脚本，帮助您更轻松地部署和维护系统：

| 脚本名称                   | 用途             | 使用场景                       |
| -------------------------- | ---------------- | ------------------------------ |
| `start-hims.bat`           | 开发环境启动脚本 | 日常开发和测试使用             |
| `start-hims-prod.bat`      | 生产环境启动脚本 | 部署到生产环境使用             |
| `start-hims-config.bat`    | 系统配置文件     | 自定义数据库连接、端口等配置   |
| `install-dependencies.bat` | 依赖安装脚本     | 首次使用或依赖更新时使用       |
| `create-database.bat`      | 数据库初始化脚本 | 首次使用或需要重建数据库时使用 |
| `fix-permissions.bat`      | 权限修复工具     | 遇到权限相关问题时使用         |

## 配置说明

### 系统配置文件 (start-hims-config.bat)

您可以通过编辑`start-hims-config.bat`文件来自定义系统配置：

```batch
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
```

### 环境配置文件

系统支持多环境配置：

- `application.yml`: 基础配置
- `application-dev.yml`: 开发环境配置
- `application-prod.yml`: 生产环境配置
- `application-local.yml`: 本地环境配置（由启动脚本自动生成）

## 开发指南

### 后端开发

1. 使用 IntelliJ IDEA 或 Eclipse 打开 backend 目录
2. 修改`application.yml`中的数据库配置
3. 运行`HimsApplication.java`启动应用

### 前端开发

1. 使用 VS Code 打开 frontend 目录
2. 安装依赖: `npm install`
3. 启动开发服务器: `npm run dev`
4. 构建生产版本: `npm run build`

## Windows 兼容性指南

为了确保系统在 Windows 环境(特别是 Windows 10/11)下正常运行，请注意以下事项：

### 系统要求

- Windows 10/11 (64 位)
- 已安装 JDK 8 或更高版本，并正确配置 JAVA_HOME 环境变量
- 已安装 Node.js 14 或更高版本
- 已安装 MySQL 5.7 或 8.0，并启动 MySQL 服务
- 确保防火墙未阻止应用程序访问网络

### 常见问题解决方案

#### 1. MySQL 连接问题

如果遇到 MySQL 连接错误，请检查：

- MySQL 服务是否已启动（可在服务管理中查看）
- 数据库用户名和密码是否正确（默认为 root/65353804778）
- 是否已创建 hims 数据库（可使用`create-database.bat`脚本创建）
- 修改`start-hims-config.bat`中的数据库配置以匹配您的环境

#### 2. 端口占用问题

如果 8080 或 3002 端口被占用：

- 修改`start-hims-config.bat`中的`BACKEND_PORT`和`FRONTEND_PORT`参数
- 使用`netstat -ano | findstr 8080`查找占用端口的进程

#### 3. 路径问题

如果遇到路径相关错误：

- 确保项目路径不包含中文字符或特殊符号
- 确保使用管理员权限运行命令提示符或 PowerShell
- 如果使用 IDE，尝试以管理员身份运行 IDE
- 运行`fix-permissions.bat`脚本修复文件权限问题

#### 4. Node.js 依赖问题

如果前端依赖安装失败：

- 运行`install-dependencies.bat`脚本，它会自动尝试多种方式安装依赖
- 如果仍然失败，尝试手动执行以下命令：
  ```
  cd frontend
  npm cache clean --force
  npm install --legacy-peer-deps
  ```
- 如果网络问题导致安装失败，可以尝试使用 cnpm：
  ```
  npm install -g cnpm --registry=https://registry.npmmirror.com
  cnpm install
  ```

#### 5. Java 相关问题

如果后端启动失败：

- 确认 JDK 版本兼容（推荐 JDK 8 或 JDK 11）
- 检查 JAVA_HOME 环境变量是否正确设置
- 在`start-hims-config.bat`中设置正确的`JAVA_PATH`

#### 6. 权限问题

如果遇到"拒绝访问"或权限相关错误：

- 以管理员身份运行`fix-permissions.bat`脚本
- 确保当前用户对项目目录有完全控制权限
- 检查杀毒软件是否阻止了某些操作

## 注意事项

- 确保 MySQL 服务已启动且配置正确
- 首次运行时，系统会自动初始化数据库结构
- 开发环境下使用的是开发证书，生产环境需要更新安全配置
- 如需在局域网内访问，请确保防火墙允许相应端口通行

## 故障排除

如果启动脚本无法正常工作，可以尝试以下步骤：

1. 运行`fix-permissions.bat`修复可能的权限问题

2. 运行`create-database.bat`确保数据库已正确创建

3. 运行`install-dependencies.bat`确保所有依赖已正确安装

4. 检查 Java 和 Node.js 是否正确安装：

   - 在命令提示符中运行`java -version`和`node -v`
   - 确保版本符合要求

5. 手动启动服务：

   - 打开两个命令提示符窗口
   - 在第一个窗口中，导航到 backend 目录并运行`mvnw spring-boot:run`
   - 在第二个窗口中，导航到 frontend 目录并运行`npm install`然后`npm run dev`

6. 检查日志：

   - 后端日志位于`logs/hims.log`
   - 前端日志会显示在命令提示符窗口中

7. 数据库重置：
   - 如果数据库结构有问题，可以删除 hims 数据库并重新创建
   - 运行`create-database.bat`脚本重新创建数据库
   - 然后重新启动后端服务
