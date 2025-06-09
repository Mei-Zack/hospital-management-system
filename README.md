# 医院管理系统 (HIMS)

## 项目简介

医院管理系统(HIMS)是一个基于 Spring Boot 和 Vue 3 的全栈应用程序，旨在帮助医院管理预约挂号、医生排班、患者信息等功能。系统采用前后端分离架构，前端使用 Vue 3 + Element Plus 构建用户界面，后端使用 Spring Boot 提供 API 服务。
![image](https://github.com/user-attachments/assets/d146722d-9705-4064-b0f7-1a6a5cc91e46)


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

### 启动方式

#### 方式一：使用批处理脚本(推荐)

1. 双击运行项目根目录下的`start-hims-fix.bat`文件
2. 系统将自动启动前端和后端服务

#### 方式二：手动启动

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

### 访问系统

- 后端 API 地址: http://localhost:8080
- 前端页面地址: http://localhost:5173

## 系统账号

系统预设了以下账号，可用于测试：

- 管理员账号:

  - 用户名: admin
  - 密码: admin



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
└── start-hims.bat          # 快速启动脚本
```

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

## 注意事项

- 确保 MySQL 服务已启动且配置正确
- 首次运行时，系统会自动初始化数据库结构
- 开发环境下使用的是开发证书，生产环境需要更新安全配置
