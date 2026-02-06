# 宙斯大酒店智能便利店系统 (Zeus Hotel Smart Convenience Store)

一个完整的酒店大堂无人便利店管理系统，包含Spring Boot后端API服务和Vue 3前端用户界面。

## 项目结构

```
Big_hotel/
├── ZeusHotelShop-Backend/          # Spring Boot 3 后端服务
│   ├── src/main/java/org/zeushotel/fastmart/nucleus/
│   │   ├── webportal/              # REST API控制器
│   │   ├── bizcore/                # 业务逻辑层
│   │   ├── dataschema/             # 数据模型实体
│   │   ├── dbgateway/              # MyBatis数据访问层
│   │   ├── transferobject/         # 数据传输对象
│   │   ├── viewresponse/           # 视图响应对象
│   │   ├── setupconfig/            # 配置类
│   │   ├── paymentbridge/          # 支付渠道集成
│   │   └── qrcodegen/              # 二维码生成
│   └── src/main/resources/
│       ├── application.yml         # 应用配置
│       └── schema.sql              # 数据库初始化脚本
│
└── ZeusShop-VueInterface/          # Vue 3 + TypeScript 前端
    ├── src/
    │   ├── views/                  # 页面视图
    │   ├── components/             # 自定义UI组件
    │   ├── stores/                 # Pinia状态管理
    │   ├── api/                    # API接口封装
    │   ├── utils/                  # 工具函数
    │   └── router/                 # 路由配置
    ├── package.json
    └── vite.config.ts
```

## 技术栈

### 后端技术
- **Spring Boot 3.2.1** - 核心框架
- **MyBatis-Plus 3.5.5** - ORM框架，简化数据访问
- **SaToken 1.37.0** - 轻量级权限认证框架
- **Knife4j 4.4.0** - Swagger UI增强，API文档自动生成
- **MySQL 8.0** - 关系型数据库
- **Google ZXing 3.5.2** - 二维码生成库
- **BCrypt** - 密码加密
- **Jackson** - JSON序列化

### 前端技术
- **Vue 3.4** - 渐进式前端框架，使用Composition API
- **TypeScript 5.3** - 类型安全的JavaScript超集
- **Pinia 2.1** - Vue官方状态管理库
- **Vue Router 4.2** - 官方路由管理器
- **Axios 1.6** - Promise基础的HTTP客户端
- **Vite 5.0** - 下一代前端构建工具
- **纯CSS实现** - 无Element Plus等UI库，完全自定义组件

## 核心功能特性

### 1. 用户账户管理
- ✅ 用户注册与登录认证
- ✅ 个人资料管理（昵称、头像、手机号）
- ✅ 头像上传与预览
- ✅ 员工身份标识与特权
- ✅ 会话自动恢复
- ✅ 安全退出登录

### 2. 商品目录系统
- ✅ 商品分页浏览
- ✅ 分类筛选（饮料、零食、方便食品等）
- ✅ 员工折扣价格自动展示
- ✅ 库存实时显示
- ✅ 低库存预警标识
- ✅ 商品详情查看

### 3. 电子优惠券
- ✅ 可领取优惠券列表展示
- ✅ 一键领取优惠券
- ✅ 我的优惠券管理
- ✅ 优惠券使用状态跟踪
- ✅ 订单结算时优惠券抵扣
- ✅ 管理员优惠券发放（满减、折扣）

### 4. 购物篮功能
- ✅ 添加商品到购物篮
- ✅ 修改商品数量（+/-按钮）
- ✅ 删除单个商品
- ✅ 清空整个购物篮
- ✅ 实时价格计算
- ✅ 员工价自动应用
- ✅ 购物篮角标提示

### 5. 订单管理
- ✅ 创建购买订单
- ✅ 订单列表查看
- ✅ 订单详情展示
- ✅ 订单状态跟踪（待支付、已支付、已完成）
- ✅ 优惠券抵扣计算
- ✅ 订单编号自动生成
- ✅ 库存自动扣减

### 6. 支付集成
- ✅ 微信支付接入（模拟）
- ✅ 支付宝支付接入（模拟）
- ✅ 支付二维码生成与展示
- ✅ 支付回调处理
- ✅ 支付状态更新

### 7. 扫码购物
- ✅ 调用设备摄像头
- ✅ 实时扫描二维码
- ✅ 扫描结果展示
- ✅ 快速添加商品

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Node.js 18+
- Maven 3.6+

### 后端部署步骤

#### 1. 数据库初始化
```bash
# 登录MySQL
mysql -u root -p

# 执行数据库脚本
source /path/to/ZeusHotelShop-Backend/src/main/resources/schema.sql
```

#### 2. 配置数据库连接
编辑 `ZeusHotelShop-Backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zeus_hotel_shop?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_mysql_username
    password: your_mysql_password
```

#### 3. 启动后端服务
```bash
cd ZeusHotelShop-Backend
mvn clean install
mvn spring-boot:run
```

后端服务将运行在: **http://localhost:9876/zeus-api**

API文档访问地址: **http://localhost:9876/zeus-api/doc.html**

### 前端部署步骤

#### 1. 安装依赖
```bash
cd ZeusShop-VueInterface
npm install
```

#### 2. 启动开发服务器
```bash
npm run dev
```

前端服务将运行在: **http://localhost:5173**

#### 3. 构建生产版本
```bash
npm run build
# 构建产物在 dist/ 目录
```

## 默认测试数据

数据库初始化脚本已包含测试数据：

### 测试账号
- **用户名**: admin
- **密码**: (需要通过注册页面创建新账号)

### 测试商品
1. 可口可乐 330ml - ¥3.50 (员工价 ¥2.80)
2. 康师傅红烧牛肉面 - ¥5.00 (员工价 ¥4.00)
3. 奥利奥饼干 - ¥8.50 (员工价 ¥6.80)
4. 农夫山泉 550ml - ¥2.00 (员工价 ¥1.50)
5. 德芙巧克力 - ¥12.00 (员工价 ¥9.60)

## API接口文档

访问 **http://localhost:9876/zeus-api/doc.html** 查看完整的交互式API文档。

### 主要接口模块

#### 1. 购物者账户接入点 (`/gateway/shopper-accounts`)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/register-new` | 新用户注册 |
| POST | `/authenticate` | 用户登录认证 |
| GET | `/my-profile` | 获取当前用户资料 |
| PUT | `/update-profile` | 更新用户资料 |
| POST | `/sign-out` | 退出登录 |

#### 2. 商品目录接入点 (`/gateway/merchandise`)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/browse-catalog` | 浏览商品目录（分页） |
| GET | `/details/{itemId}` | 查看商品详情 |
| POST | `/admin/add-new` | 添加新商品（管理员） |
| PUT | `/admin/modify/{itemId}` | 修改商品（管理员） |

#### 3. 优惠券分发接入点 (`/gateway/vouchers`)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/available-list` | 列出可用优惠券 |
| POST | `/claim/{voucherId}` | 领取优惠券 |
| GET | `/my-collection` | 查看我的优惠券 |
| POST | `/admin/create-new` | 创建优惠券（管理员） |

#### 4. 购物篮接入点 (`/gateway/shopping-basket`)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/add-item` | 添加商品到购物篮 |
| GET | `/my-basket` | 查看我的购物篮 |
| PUT | `/modify-quantity/{basketItemId}` | 修改商品数量 |
| DELETE | `/remove-item/{basketItemId}` | 移除商品 |
| DELETE | `/clear-all` | 清空购物篮 |

#### 5. 采购订单接入点 (`/gateway/purchase-orders`)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/create-new` | 创建新订单 |
| GET | `/my-orders` | 查看我的订单列表 |
| GET | `/details/{orderId}` | 查看订单详情 |
| POST | `/update-payment/{orderId}` | 更新订单支付状态 |

#### 6. 支付渠道接入点 (`/gateway/payment-channels`)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/wechat/initiate` | 发起微信支付 |
| POST | `/alipay/initiate` | 发起支付宝支付 |
| POST | `/wechat/callback` | 微信支付回调 |
| POST | `/alipay/callback` | 支付宝支付回调 |
| GET | `/generate-qr` | 生成二维码图片 |

## 数据库设计

### 核心数据表

#### 1. tbl_shopper_profiles (购物者档案表)
- profile_identifier - 档案ID（主键）
- login_username - 登录用户名（唯一）
- secret_hash - 密码哈希（BCrypt加密）
- display_nickname - 显示昵称
- portrait_image_link - 头像图片链接
- contact_phone_number - 联系电话
- employee_status_flag - 员工状态标志（0-非员工，1-员工）
- account_active_flag - 账户激活标志

#### 2. tbl_merchandise_catalog (商品目录表)
- item_identifier - 商品ID（主键）
- item_sku - SKU编码（唯一）
- item_display_name - 商品名称
- standard_price_yuan - 标准价格
- staff_discount_price_yuan - 员工折扣价格
- stock_quantity - 库存数量
- shelf_status - 上架状态

#### 3. tbl_discount_vouchers (折扣优惠券表)
- voucher_identifier - 优惠券ID（主键）
- voucher_code - 优惠券代码（唯一）
- voucher_type - 类型（1-满减，2-折扣）
- discount_amount - 折扣金额
- minimum_purchase - 最低消费
- total_issue_quantity - 总发行数量
- claimed_quantity - 已领取数量

#### 4. tbl_shopper_voucher_claims (优惠券领取记录表)
- claim_identifier - 领取ID（主键）
- shopper_profile_id - 购物者ID（外键）
- voucher_id - 优惠券ID（外键）
- usage_status - 使用状态（0-未使用，1-已使用）

#### 5. tbl_shopping_baskets (购物篮表)
- basket_item_id - 购物篮项ID（主键）
- shopper_profile_id - 购物者ID
- merchandise_item_id - 商品ID
- selected_quantity - 选择数量

#### 6. tbl_purchase_orders (采购订单表)
- order_identifier - 订单ID（主键）
- order_number - 订单号（唯一，格式：ZH+时间戳+随机数）
- total_amount_yuan - 总金额
- discount_amount_yuan - 折扣金额
- final_payment_yuan - 最终支付金额
- payment_method - 支付方式（1-微信，2-支付宝）
- order_status - 订单状态（0-待支付，1-已支付，2-已完成）

## 项目亮点与特色

### 1. 完全原创的代码实现
- ❌ 不使用常见的命名模式（如UserService、ProductController）
- ✅ 创意性命名：ShopperAccountOrchestrator、MerchandiseCatalogOrchestrator
- ✅ 独特的包结构：webportal、bizcore、dataschema、dbgateway
- ✅ 原创的业务逻辑实现模式

### 2. 自定义UI组件库
- ❌ 不依赖Element Plus、Ant Design等第三方UI库
- ✅ 纯手工打造的Vue组件：ZeusButton、ZeusInput、ProductCard
- ✅ 原创的渐变色主题设计
- ✅ 流畅的CSS动画效果
- ✅ 响应式布局设计

### 3. 员工特权系统
- 自动识别员工账号
- 差异化价格展示
- 员工专属折扣
- 视觉化员工标识

### 4. 完整的支付流程
- 支付方式选择
- 二维码生成与展示
- 支付状态模拟
- 订单自动流转

### 5. 现代化前端架构
- TypeScript类型安全
- Pinia状态管理
- Composition API
- 路由守卫认证
- HTTP拦截器
- 统一错误处理

### 6. 安全性设计
- BCrypt密码加密
- SaToken令牌认证
- SQL注入防护（MyBatis-Plus）
- XSS防护
- CORS跨域配置

## 使用指南

### 用户注册与登录
1. 访问 http://localhost:5173
2. 点击"注册"标签
3. 填写用户名、密码、昵称、手机号
4. 注册成功后自动登录

### 浏览商品
1. 登录后进入商品大厅
2. 使用分类按钮筛选商品
3. 点击商品卡片查看详情
4. 点击"+"按钮添加到购物篮

### 领取优惠券
1. 点击顶部个人图标进入个人中心
2. 选择"我的优惠券"
3. 在可领取列表中点击"领取"按钮

### 购物结算
1. 点击购物篮图标查看已选商品
2. 调整商品数量或删除商品
3. 点击"去结算"进入支付页面
4. 选择支付方式
5. 提交订单并扫码支付

### 查看订单
1. 进入个人中心
2. 点击"我的订单"
3. 查看订单历史和状态

## 部署建议

### 后端生产部署
```bash
# 打包
cd ZeusHotelShop-Backend
mvn clean package -DskipTests

# 运行
java -jar target/zeus-fastmart-backend.jar

# 或使用nohup后台运行
nohup java -jar target/zeus-fastmart-backend.jar > app.log 2>&1 &
```

### 前端生产部署

#### 方案一：Nginx部署
```bash
# 构建
cd ZeusShop-VueInterface
npm run build

# Nginx配置
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /zeus-api {
        proxy_pass http://localhost:9876;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

#### 方案二：Docker部署
```dockerfile
# 后端Dockerfile
FROM openjdk:17-jdk-slim
COPY target/zeus-fastmart-backend.jar app.jar
EXPOSE 9876
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 前端Dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

## 技术支持

- 📧 邮箱: tech@zeushotel.org
- 📚 文档: 查看各模块代码注释
- 🐛 问题: 提交Issue到项目仓库

## 许可证

本项目仅用于学习和演示目的，不得用于商业用途。

---

**开发团队**: Zeus Hotel Technology Team  
**最后更新**: 2024年  
**版本**: v2.8.0
酒店无人店
