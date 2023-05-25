## 项目背景
一个类似OpenApi的BinAPi微服务后台接口调用平台，旨在为开发者可以快速接入一些接口服务，从而提高开发效率，减少开发成本，避免重复造轮子，为业务高效赋能。

- 用户：注册登录，开通购买接口权限，调用接口
- 后台：用户角色校验，提供接口的调用，分析接口的调用情况；管理员发布接口、下线接口、新增接口
## 主要功能

- 接口接入
- 角色权限校验
- 网关统一接收请求
- 调用次数限流
- 统计调用次数
- 统一接收请求映射地址
- 接口计费
- 流量保护
## 设计架构图
![](https://cdn.nlark.com/yuque/0/2023/jpeg/23038111/1685017486843-538e7bff-ce02-48ed-8ebe-d5592f132b76.jpeg)
## 技术模块介绍：
### 后端技术栈：

- SpringBoot2.6.13
- SpringCloud2021.0.5
- Dubbo3.0.9（RPC远程调用）
- Nacos2.2.2（注册中心）
- SpringCloud Gateway（全局网关、接口网关）
- Mybatis
- Redis
- MybatisPlus
- SpringBoot Starter（SDK开发）
### 后端模块介绍：

- binapi-global-gateway：后端服务全局网关，主要负责后端请求接口的转发，请求规则匹配，所有普通用户请求必须经过网关才能访问到具体服务，最终才能到达接口服务的调用。
- binapi-service：后端核心服务模块，主要功能：用户注册、登录、信息修改、接口浏览、接口上下线；管理员用户对接口的增删改查；用户权限校验、身份信息存储等核心业务。
- binapi-client-sdk：远程客户端调用  SDK Starter 工具；主要功能：集中将所有请求统一发送到接口网关，并设置待定规则的请求头信息等逻辑。
- binapi-common：后端服务通用模块，抽取后端服务的实体类、视图、工具包、字符串常量、枚举等
- binapi-interfaces-gateway：接口网关模块，主要集中接收接口被调用的请求，进行请求转发，并做相应的日志聚集、请求校验、用户鉴权、接口次数统计、接口权限校验、流量染色、访问控制等功能。
- binapi-interfaces：接口服务模块，主要提供被调用的后端接口api服务，并将统一从网关转发过来的请求，利用反射机制统一将其请求路径映射到对应的请求Controller类方法上执行。
## 数据库表设计
### tb_user表
该表中主要存储用户信息

| 字段名 | 数据类型 | 长度 | 约束条件 | 说明 |
| --- | --- | --- | --- | --- |
| id | bigint |  | NOT NULL AUTO_INCREMENT UNSIGN | 主键 |
| username | varchar | 256 |  | 用户昵称 |
| account | varchar | 256 | NOT NULL | 账号 |
| phone | varchar | 256 |  | 手机号 |
| email | varchar | 255 | NOT NULL | 邮箱 |
| avatar | varchar | 1024 |  | 用户头像 |
| gender | tinyint |  |  | 性别 |
| role | varchar | 256 | NOT NULL DEFAULT 'user' | 用户角色：user / admin |
| password | varchar | 512 | NOT NULL | 密码 |
| access_key | varchar | 512 | NOT NULL | accessKey |
| secret_key | varchar | 512 | NOT NULL | secretKey |
| create_time | datetime |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime |  | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| is_delete | tinyint |  | NOT NULL DEFAULT '0' | 是否删除(0-未删, 1-已删) |

### tb_interface_info表
该表用于存储接口信息。

| 字段名 | 数据类型 | 长度 | 约束条件 | 说明 |
| --- | --- | --- | --- | --- |
| id | bigint |  | NOT NULL AUTO_INCREMENT UNSIGN | 主键 |
| name | varchar | 256 | NOT NULL | 名称 |
| description | varchar | 256 |  | 描述 |
| method | varchar | 256 | NOT NULL | 请求类型 |
| url | varchar | 512 | NOT NULL | 接口地址 |
| request_params | text |  |  | 请求参数 |
| request_header | text |  |  | 请求头 |
| response_header | text |  |  | 响应头 |
| price | decimal | 10 | NOT NULL | 计费规则(元/条) |
| status | int |  | NOT NULL DEFAULT '0' | 接口状态（0-关闭，1-开启） |
| left_num | int |  | NOT NULL UNSIGN | 接口剩余能够被调用的次数 |
| total_num | int |  | NOT NULL DEFAULT 0 UNSIGN | 接口已经被调用的总次数 |
| user_id | bigint |  | NOT NULL UNSIGN | 创建人 |
| create_time | datetime |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime |  | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| is_delete | tinyint |  | NOT NULL DEFAULT '0' | 是否删除(0-未删, 1-已删) |

### user_interface_info表
该表用于存储用户调用接口的信息。

| 字段名 | 数据类型 | 长度 | 约束条件 | 说明 |
| --- | --- | --- | --- | --- |
| id | bigint |  | NOT NULL AUTO_INCREMENT UNSIGN | 主键 |
| user_id | bigint |  | NOT NULL UNSIGN | 调用用户 id |
| interface_info_id | bigint |  | NOT NULL UNSIGN | 接口 id |
| left_num | int |  | NOT NULL UNSIGN | 用户的接口剩余调用次数 |
| total_num | int |  | NOT NULL DEFAULT '0' UNSIGN | 用户的接口总调用次数 |
| status | int |  | NOT NULL DEFAULT '1' | 0-禁用，1-正常 |
| create_time | datetime |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime |  | NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| is_delete | tinyint |  | NOT NULL DEFAULT '0' | 是否删除(0-未删, 1-已删) |

## 项目设计
### alias-openapi-service微服务

- 设计通用返回类
   - 自定义错误码
   - 自定义业务异常类
- JSON数据统一序列化处理
- 跨域处理
   - 在每次请求前进行拦截，并添加一些响应头，来允许跨域请求。如果是OPTIONS请求，则返回NO_CONTENT状态码，否则继续执行后续的过滤器或者请求处理器
- AOP
   - 权限校验AOP，使用注解拦截用户请求进行权限校验
   - 请求响应日志AOP，收到请求-->拦截-->获取请求路径-->生成唯一请求id-->获取请求参数-->输出请求日志-->处理原请求
   - 接口调用次数初始化AOP，在接口信息表、用户信息表更新后，自动初始化接口请求次数
- 用户Service
   - 增删改查
   - 用户注册、登录
   - 保存、获取用户登录态
- 接口信息Service
   - 增删改查
   - 上线、下线接口
   - 接口调用，更新数据库统计调用次数并接受用户参数，校验权限，将请求转发到网关（对每个接口都要执行统计调用次数的操作，那么我们可以使用AOP切面实现，独立于接口，但缺点是只适用于单体项目以内，如果有多个团队开发自己的模拟接口，就可以使用网关实现）
- 用户接口Service
   - 增删改查
   - 查询可用接口，涉及到多表查询
- API签名认证用于用户鉴权，适用于无需保存登录态的场景（只用私钥密钥鉴权），确保接口请求的合法性和安全性，保护用户数据不被非法访问。用户每次调用接口都需要验证ak、sk使用MD5加密算法生成签名，在用户注册时分配私钥(accessKey)、密钥(secretKey)防止重放：请求加nonce随机数、加timestamp时间戳重放攻击（Replay Attack）是指攻击者截获合法用户的某个请求，然后在不经过用户的授权和知晓的情况下，将该请求发送给服务器，从而实现非法操作的一种攻击方式。重放攻击通常利用网络中的漏洞或者不安全的通信协议，来重复发送已经被截获的数据包，可能导致一些严重的后果，如恶意篡改数据、非法获取数据等。
## 项目遇到的问题
## 项目优化思路
## 项目正在开发的进展规划



