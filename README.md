# 基于Spring Boot和Vue的课题组项目协同与周报管理系统

## 项目简介

一个帮助导师和学生协作的工具，功能包括：
1. 用户登录认证。
2. 多课题组管理。
3. 项目任务协作。
4. 周报提交与导师审核。
5. 审计与可追溯。

---

### 技术栈
- **后端**: Spring Boot, Spring Security, Spring Data JPA
- **前端**: Vue.js 3, Vite
- **数据库**: MySQL
- **其他工具**: Docker, JWT

### 快速启动

确保你的电脑安装了 Git、Docker、Docker Compose。

#### 克隆仓库
```bash
git clone https://github.com/2854737843/lab.git
cd lab
```

#### 配置环境变量
在 `backend` 目录下创建 `.env` 文件，并填写以下内容：
```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/lab_manage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=your_mysql_user
SPRING_DATASOURCE_PASSWORD=your_mysql_password
JWT_SECRET=your_jwt_secret
```

#### 启动项目
使用 Docker Compose 一键启动整个项目。
```bash
docker-compose up
```
---

## 毕业论文
项目根目录将提供一份毕业论文内容。

