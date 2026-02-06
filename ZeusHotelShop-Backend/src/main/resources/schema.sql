CREATE DATABASE IF NOT EXISTS zeus_hotel_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zeus_hotel_shop;

CREATE TABLE tbl_shopper_profiles (
    profile_identifier BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物者档案标识',
    login_username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    secret_hash VARCHAR(200) NOT NULL COMMENT '密码哈希',
    display_nickname VARCHAR(100) COMMENT '显示昵称',
    portrait_image_link VARCHAR(500) COMMENT '头像图片链接',
    contact_phone_number VARCHAR(20) COMMENT '联系电话号码',
    employee_status_flag TINYINT DEFAULT 0 COMMENT '员工状态标志 0-非员工 1-员工',
    account_active_flag TINYINT DEFAULT 1 COMMENT '账户激活标志 0-禁用 1-启用',
    record_created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    record_updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    deletion_marker TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物者档案表';

CREATE TABLE tbl_merchandise_catalog (
    item_identifier BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品标识',
    item_sku VARCHAR(100) UNIQUE COMMENT '商品SKU编码',
    item_display_name VARCHAR(200) NOT NULL COMMENT '商品展示名称',
    item_description TEXT COMMENT '商品描述',
    category_label VARCHAR(100) COMMENT '类别标签',
    standard_price_yuan DECIMAL(10,2) NOT NULL COMMENT '标准价格（元）',
    staff_discount_price_yuan DECIMAL(10,2) COMMENT '员工折扣价格（元）',
    main_image_url VARCHAR(500) COMMENT '主图链接',
    additional_images_json TEXT COMMENT '附加图片JSON',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    shelf_status TINYINT DEFAULT 1 COMMENT '上架状态 0-下架 1-上架',
    record_created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    record_updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    deletion_marker TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品目录表';

CREATE TABLE tbl_discount_vouchers (
    voucher_identifier BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券标识',
    voucher_code VARCHAR(50) UNIQUE NOT NULL COMMENT '优惠券代码',
    voucher_title VARCHAR(200) NOT NULL COMMENT '优惠券标题',
    voucher_type TINYINT NOT NULL COMMENT '优惠券类型 1-满减 2-折扣',
    discount_amount DECIMAL(10,2) NOT NULL COMMENT '折扣金额',
    minimum_purchase DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费',
    total_issue_quantity INT NOT NULL COMMENT '总发行数量',
    claimed_quantity INT DEFAULT 0 COMMENT '已领取数量',
    valid_from_time DATETIME NOT NULL COMMENT '有效开始时间',
    valid_until_time DATETIME NOT NULL COMMENT '有效结束时间',
    active_status TINYINT DEFAULT 1 COMMENT '激活状态 0-禁用 1-启用',
    record_created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    record_updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    deletion_marker TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='折扣优惠券表';

CREATE TABLE tbl_shopper_voucher_claims (
    claim_identifier BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '领取标识',
    shopper_profile_id BIGINT NOT NULL COMMENT '购物者档案ID',
    voucher_id BIGINT NOT NULL COMMENT '优惠券ID',
    usage_status TINYINT DEFAULT 0 COMMENT '使用状态 0-未使用 1-已使用',
    claimed_at_time DATETIME NOT NULL COMMENT '领取时间',
    used_at_time DATETIME COMMENT '使用时间',
    record_created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    deletion_marker TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_shopper_voucher (shopper_profile_id, voucher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物者优惠券领取表';

CREATE TABLE tbl_shopping_baskets (
    basket_item_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物篮项标识',
    shopper_profile_id BIGINT NOT NULL COMMENT '购物者档案ID',
    merchandise_item_id BIGINT NOT NULL COMMENT '商品ID',
    selected_quantity INT NOT NULL COMMENT '选择数量',
    record_created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    record_updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    INDEX idx_shopper_basket (shopper_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物篮表';

CREATE TABLE tbl_purchase_orders (
    order_identifier BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单标识',
    order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
    shopper_profile_id BIGINT NOT NULL COMMENT '购物者档案ID',
    total_amount_yuan DECIMAL(10,2) NOT NULL COMMENT '总金额（元）',
    discount_amount_yuan DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额（元）',
    final_payment_yuan DECIMAL(10,2) NOT NULL COMMENT '最终支付金额（元）',
    payment_method TINYINT NOT NULL COMMENT '支付方式 1-微信 2-支付宝',
    payment_transaction_id VARCHAR(100) COMMENT '支付交易ID',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态 0-待支付 1-已支付 2-已完成',
    order_items_json TEXT COMMENT '订单项JSON',
    applied_voucher_id BIGINT COMMENT '应用的优惠券ID',
    record_created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    record_updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    deletion_marker TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_shopper_orders (shopper_profile_id),
    INDEX idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

INSERT INTO tbl_shopper_profiles (login_username, secret_hash, display_nickname, employee_status_flag) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

INSERT INTO tbl_merchandise_catalog (item_sku, item_display_name, item_description, category_label, standard_price_yuan, staff_discount_price_yuan, stock_quantity) VALUES
('SKU001', '可口可乐 330ml', '经典可乐饮料', '饮料', 3.50, 2.80, 100),
('SKU002', '康师傅红烧牛肉面', '方便面经典口味', '方便食品', 5.00, 4.00, 80),
('SKU003', '奥利奥饼干', '巧克力夹心饼干', '零食', 8.50, 6.80, 60),
('SKU004', '农夫山泉 550ml', '天然矿泉水', '饮料', 2.00, 1.50, 150),
('SKU005', '德芙巧克力', '丝滑牛奶巧克力', '零食', 12.00, 9.60, 50);
