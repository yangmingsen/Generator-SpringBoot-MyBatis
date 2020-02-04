# SpringBoot+MyBatis代码生成工程

### 主要功能
- 生成Mapper文件
- 生成Service文件
- 生成Controller文件
- 生成Entity文件

### 如何启动
直接运行App类的main()方法即可

### 配置相关介绍
**1.数据库配置**
- driver: 数据库驱动
- url: url
- username: 数据库用户名
- password: 数据库密码

**2.代码生成目标选择配置**
- allTables: 所有表
- specificTables: 指定表,以英文“,”分隔

**3.代码输出配置**
- outputTo: 选择输出目标(1.到控制台; 2.到文件)
- outPath: 输出目标路径

**4.代码生成配置**
- controller: 是否生成Ctroller文件(true,false)
- service: 是否生成service文件(true,false)
- mapper: 是否生成mapper文件(true,false)
- entity: 是否生成entity文件(true,false)
- angularJS: 是否生成angularJS文件(true,false)
- entityGet: 是否生成类的get方法
- entitySet: 是否生成类的set方法
- entityConstructor: 是否生成类的构造器方法

**5.其他配置**
- author: 代码生成作者
