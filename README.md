# StuInfoSystem 学生成绩信息管理系统

基于 Spring Boot + MyBatis + Thymeleaf 的学生成绩信息管理系统。系统分为**管理员、教师、学生**三种角色，
除基础信息维护外，还提供成绩排名、班级平均分、成绩分布等多维度的统计与图表展示。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| JDK | 1.8 |
| Spring Boot | 1.5.10.RELEASE |
| MyBatis | mybatis-spring-boot-starter 1.3.1 |
| 分页 | PageHelper 1.2.10 |
| 连接池 | Druid 1.1.8 |
| 数据库 | MySQL（驱动 mysql-connector-java 5.1.49） |
| 模板引擎 | Thymeleaf 3.0.9 + thymeleaf-layout-dialect 2.2.2 |
| 前端 | Bootstrap 4.0.0、jQuery 3.3.1、Chart.js |

## 功能

### 管理员
- 登录 / 注销
- 学生信息：分页列表、按学号查询、按班级查询、新增、修改、删除
- 教师信息：分页列表、按工号查询、新增、修改、删除
- 班级信息：分页列表、按班级名查询、新增、修改、删除

### 教师
- 登录 / 注销、修改个人信息
- 成绩录入、按成绩 ID / 学号查询、修改、删除
- 统计报表：
  - 总排名、班级排名、单科排名
  - 班级平均分
  - 成绩分布
  - 班级单科平均分图表

### 学生
- 登录 / 注销、修改个人信息
- 查询个人成绩，按学期筛选
- 个人成绩图表

登录态由 `LoginHandlerInterceptor` 统一拦截，未登录访问任意页面都会跳回登录页。

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE stuinfosystem DEFAULT CHARACTER SET utf8;
```

### 2. 导入 SQL

| 文件 | 说明 |
| --- | --- |
| `sql/stuinfosystem.sql` | **必需**。建表语句 + 管理员账号 |
| `sql/final3.0.sql` | 可选。演示数据：班级、学生、教师、科目、成绩 |

```bash
mysql -uroot -p stuinfosystem < sql/stuinfosystem.sql
mysql -uroot -p stuinfosystem < sql/final3.0.sql   # 可选
```

### 3. 修改数据库连接

编辑 `src/main/resources/application.yml`，把用户名密码改成你自己的：

```yaml
spring:
  datasource:
    username: root
    password: 123456   # 改成你的密码
    url: jdbc:mysql://127.0.0.1:3306/stuinfosystem?...
```

### 4. 启动

用 IDE 直接运行 `com.lc.demo.DemoApplication`，或者：

```bash
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

启动后访问 <http://localhost:8080/>（默认端口 8080）。

## 演示账号

导入 `sql/final3.0.sql` 后可直接使用以下账号登录：

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `123` |
| 教师 | `1001` | `123` |
| 学生 | `20181685330001` | `123` |

> 演示数据中除 `admin` 外的账号密码统一为 `123`。

## 目录结构

```
src/main/java/com/lc/demo
├── bean/         实体类
├── config/       SpringMVC 配置（视图映射、拦截器注册）
├── controller/   管理员 / 教师 / 学生三个控制器
├── interceptor/  登录拦截器
├── mapper/       MyBatis Mapper 接口
└── service/      业务层接口与实现

src/main/resources
├── application.yml   数据库与 MyBatis 配置
├── static/asserts/   静态资源（css / js / img）
└── templates/        Thymeleaf 页面
    ├── adm/   管理员端
    ├── tea/   教师端
    ├── stu/   学生端
    └── commons/  公共导航栏

sql/   建表脚本与演示数据
```

## 说明

- 数据库表：`admin`、`student`、`teacher`、`class`、`subject`、`result`。
- 学期字段（`res_term`）格式形如 `18-2020-1`。
- 仓库不包含 IDE 配置（`.idea`、`*.iml`）与 Maven 编译产物（`target/`），克隆后重新构建即可。

## License

[GPL-3.0](LICENSE)
