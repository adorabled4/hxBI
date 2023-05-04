# 项目名称

该项目是一个基于Spring Boot框架的开发模板，旨在帮助开发者迅速的进行开发，不必花费时间在大量的重复工作。


## 版本信息
包含了主要项的版本信息，更多内容请参考**pom.xml**

| 项            | 版本     |
|--------------|--------|
| JDK          | 1.8    |
| springboot   | 2.7.7  |
| MySQL        | 8.0.31 |
| Redis        | 6.2.6  |
| Maven        | 3.8.6  |
| Mybatis-plus | 3.5.3  |
| knife4j      | 4.0.13 |


## 内容

项目主要包含以下内容：

- MySQL
- Mybatis-plus
- Redis
- Logback日志配置
- 线程池配置
- 统一返回结果
- 全局异常处理、统一错误枚举
- Dockerfile文件
- AOP权限认证以及日志处理
- 基本的用户CRUD、登录注册等逻辑、对应的SQL文件
- JWT双Token登录
- Knife4j接口文档配置: 访问`http://localhost:6848/apicore/doc.html`
- Springboot`@Scheduled`定时任务


## 如何运行

1. `git clone https://github.com/adorabled4/bankend-template.git`
2. 修改数据库和Redis配置信息：`application.yml`
3. 构建项目：`mvn clean package`
4. 运行项目：`java -jar target/template.jar`
5. 构建镜像：`docker build -f Dockerfile -t template:v1 .`
6. 运行容器：`docker run -d -p 6848:6848 --name tempalte template:v1`

## 如何贡献

我们非常欢迎您为该项目做出贡献，您可以：

- 在[Issues页面](https://github.com/adorabled4/bankend-template/issues)中报告漏洞或提出改进意见。
- 提交Pull Request。
- 分享该项目给你的朋友和社区。

## 许可证

该项目基于MIT许可证开源，详情请查看[LICENSE文件](./LICENSE)。