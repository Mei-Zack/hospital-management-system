package com.hims.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("medicines")
public class Medicine extends BaseEntity {

    /**
     * 药品名称
     */
    private String name;

    /**
     * 药品编码
     */
    private String code;

    /**
     * 药品分类：WESTERN_EXTERNAL-西药外服/WESTERN_INTERNAL-西药内用/WESTERN_INJECTION-西药注射/
     * CHINESE_PATENT-中成药/CHINESE_HERBAL-中药饮片/PILL_POWDER-丸散膏等
     */
    private String category;

    /**
     * 剂型：片剂/胶囊/注射液/口服液等
     */
    private String dosageForm;

    /**
     * 规格
     */
    private String specification;

    /**
     * 用法用量
     */
    @TableField("`usage`")
    private String usage;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 单位：盒/瓶/支等
     */
    private String unit;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 进价
     */
    private BigDecimal purchasePrice;

    /**
     * 售价
     */
    private BigDecimal salePrice;

    /**
     * 批号
     */
    private String batchNumber;

    /**
     * 生产日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /**
     * 有效期至
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    /**
     * 特殊禁忌
     */
    private String contraindication;

    /**
     * 药品状态：1-正常/0-禁用/2-缺货/3-即将过期/4-已过期
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}