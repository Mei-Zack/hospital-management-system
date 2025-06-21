-- 药品表
CREATE TABLE IF NOT EXISTS medicines (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '药品名称',
    code VARCHAR(50) COMMENT '药品编码',
    category VARCHAR(50) NOT NULL COMMENT '药品分类：WESTERN_EXTERNAL-西药外服/WESTERN_INTERNAL-西药内用/WESTERN_INJECTION-西药注射/CHINESE_PATENT-中成药/CHINESE_HERBAL-中药饮片/PILL_POWDER-丸散膏等',
    dosage_form VARCHAR(50) COMMENT '剂型：片剂/胶囊/注射液/口服液等',
    specification VARCHAR(100) COMMENT '规格',
    `usage` TEXT COMMENT '用法用量',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    unit VARCHAR(20) COMMENT '单位：盒/瓶/支等',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    purchase_price DECIMAL(10, 2) NOT NULL COMMENT '进价',
    sale_price DECIMAL(10, 2) NOT NULL COMMENT '售价',
    batch_number VARCHAR(50) COMMENT '批号',
    production_date DATE COMMENT '生产日期',
    expiration_date DATE COMMENT '有效期至',
    contraindication TEXT COMMENT '特殊禁忌',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '药品状态：1-正常/0-禁用/2-缺货/3-即将过期/4-已过期',
    remark TEXT COMMENT '备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品表';

-- 药品入库表
CREATE TABLE IF NOT EXISTS medicine_inbounds (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    medicine_id BIGINT NOT NULL COMMENT '药品ID',
    batch_number VARCHAR(50) COMMENT '入库批次号',
    quantity INT NOT NULL COMMENT '入库数量',
    unit VARCHAR(20) COMMENT '单位：盒/瓶/支等',
    purchase_price DECIMAL(10, 2) NOT NULL COMMENT '进价',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '总金额',
    supplier VARCHAR(100) COMMENT '供应商',
    production_date DATE COMMENT '生产日期',
    expiration_date DATE COMMENT '有效期至',
    inbound_date DATE NOT NULL COMMENT '入库日期',
    operator_id BIGINT COMMENT '入库人员ID',
    operator_name VARCHAR(50) COMMENT '入库人员名称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常/0-已撤销',
    remark TEXT COMMENT '备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    INDEX idx_medicine_id (medicine_id),
    INDEX idx_inbound_date (inbound_date),
    INDEX idx_batch_number (batch_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品入库表';

-- 药品销售表
CREATE TABLE IF NOT EXISTS medicine_sales (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    sale_number VARCHAR(50) NOT NULL COMMENT '销售单号',
    medicine_id BIGINT NOT NULL COMMENT '药品ID',
    batch_number VARCHAR(50) COMMENT '批次号',
    quantity INT NOT NULL COMMENT '销售数量',
    unit VARCHAR(20) COMMENT '单位：盒/瓶/支等',
    sale_price DECIMAL(10, 2) NOT NULL COMMENT '售价',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '总金额',
    patient_id BIGINT COMMENT '患者ID（可选）',
    patient_name VARCHAR(50) COMMENT '患者姓名（可选）',
    sale_time DATETIME NOT NULL COMMENT '销售日期',
    operator_id BIGINT COMMENT '销售人员ID',
    operator_name VARCHAR(50) COMMENT '销售人员名称',
    prescription_id BIGINT COMMENT '处方ID（可选）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常/0-已退货',
    remark TEXT COMMENT '备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    INDEX idx_medicine_id (medicine_id),
    INDEX idx_sale_number (sale_number),
    INDEX idx_sale_time (sale_time),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品销售表';

-- 插入示例数据
INSERT INTO medicines (name, code, category, dosage_form, specification, `usage`, stock, unit, manufacturer, purchase_price, sale_price, batch_number, production_date, expiration_date, status)
VALUES 
('阿莫西林胶囊', 'AMX001', 'WESTERN_INTERNAL', '胶囊', '0.25g*24粒/盒', '口服，一次1粒，一日3次', 100, '盒', '哈药集团制药总厂', 15.00, 25.00, 'BN20230101', '2023-01-01', '2025-01-01', 1),
('布洛芬片', 'BLF002', 'WESTERN_INTERNAL', '片剂', '0.2g*24片/盒', '口服，一次1片，一日3次', 80, '盒', '上海信谊药厂有限公司', 8.50, 15.00, 'BN20230102', '2023-01-02', '2025-01-02', 1),
('头孢克肟片', 'TBKX003', 'WESTERN_INTERNAL', '片剂', '0.1g*12片/盒', '口服，一次1片，一日2次', 60, '盒', '国药集团致君(深圳)制药有限公司', 25.00, 38.00, 'BN20230103', '2023-01-03', '2025-01-03', 1),
('999感冒灵颗粒', 'GML004', 'CHINESE_PATENT', '颗粒剂', '10g*9袋/盒', '开水冲服，一次1袋，一日3次', 120, '盒', '华润三九医药股份有限公司', 12.00, 20.00, 'BN20230104', '2023-01-04', '2025-01-04', 1),
('云南白药喷雾剂', 'YNBY005', 'WESTERN_EXTERNAL', '喷雾剂', '85g/瓶', '外用，喷于患处', 50, '瓶', '云南白药集团股份有限公司', 35.00, 58.00, 'BN20230105', '2023-01-05', '2025-01-05', 1),
('维生素C片', 'WSC006', 'WESTERN_INTERNAL', '片剂', '0.1g*100片/瓶', '口服，一次1片，一日1次', 70, '瓶', '北京双鹤药业股份有限公司', 18.00, 30.00, 'BN20230106', '2023-01-06', '2025-01-06', 1),
('牛黄解毒片', 'NHJD007', 'CHINESE_PATENT', '片剂', '0.27g*12片*2板/盒', '口服，一次2片，一日3次', 90, '盒', '同仁堂科技发展股份有限公司制药厂', 22.00, 35.00, 'BN20230107', '2023-01-07', '2025-01-07', 1),
('青霉素注射液', 'QMS008', 'WESTERN_INJECTION', '注射液', '80万单位*10支/盒', '肌肉注射，一次80万单位，一日2次', 40, '盒', '华北制药股份有限公司', 30.00, 45.00, 'BN20230108', '2023-01-08', '2024-07-08', 1),
('板蓝根颗粒', 'BLG009', 'CHINESE_PATENT', '颗粒剂', '10g*20袋/盒', '开水冲服，一次1袋，一日3次', 110, '盒', '广州白云山和记黄埔中药有限公司', 20.00, 32.00, 'BN20230109', '2023-01-09', '2025-01-09', 1),
('红花油', 'HHY010', 'WESTERN_EXTERNAL', '油剂', '20ml/瓶', '外用，涂抹患处', 60, '瓶', '广州白云山陈李济药厂有限公司', 15.00, 25.00, 'BN20230110', '2023-01-10', '2025-01-10', 1);

-- 插入入库记录
INSERT INTO medicine_inbounds (medicine_id, batch_number, quantity, unit, purchase_price, total_amount, supplier, production_date, expiration_date, inbound_date, operator_id, operator_name, status)
VALUES 
(1, 'BN20230101', 100, '盒', 15.00, 1500.00, '哈药集团医药有限公司', '2023-01-01', '2025-01-01', '2023-02-01', 1, '管理员', 1),
(2, 'BN20230102', 80, '盒', 8.50, 680.00, '上海医药集团股份有限公司', '2023-01-02', '2025-01-02', '2023-02-02', 1, '管理员', 1),
(3, 'BN20230103', 60, '盒', 25.00, 1500.00, '国药控股股份有限公司', '2023-01-03', '2025-01-03', '2023-02-03', 1, '管理员', 1),
(4, 'BN20230104', 120, '盒', 12.00, 1440.00, '华润医药商业集团有限公司', '2023-01-04', '2025-01-04', '2023-02-04', 1, '管理员', 1),
(5, 'BN20230105', 50, '瓶', 35.00, 1750.00, '云南白药集团医药有限公司', '2023-01-05', '2025-01-05', '2023-02-05', 1, '管理员', 1);

-- 插入销售记录
INSERT INTO medicine_sales (sale_number, medicine_id, batch_number, quantity, unit, sale_price, total_amount, patient_name, sale_time, operator_id, operator_name, status)
VALUES 
('SALE2023030100001', 1, 'BN20230101', 2, '盒', 25.00, 50.00, '张三', '2023-03-01 10:00:00', 1, '管理员', 1),
('SALE2023030100002', 2, 'BN20230102', 1, '盒', 15.00, 15.00, '李四', '2023-03-01 11:30:00', 1, '管理员', 1),
('SALE2023030200001', 3, 'BN20230103', 1, '盒', 38.00, 38.00, '王五', '2023-03-02 09:15:00', 1, '管理员', 1),
('SALE2023030200002', 4, 'BN20230104', 2, '盒', 20.00, 40.00, '赵六', '2023-03-02 14:20:00', 1, '管理员', 1),
('SALE2023030300001', 5, 'BN20230105', 1, '瓶', 58.00, 58.00, '钱七', '2023-03-03 16:45:00', 1, '管理员', 1); 