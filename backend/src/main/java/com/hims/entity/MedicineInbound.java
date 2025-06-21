package com.hims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("medicine_inbounds")
public class MedicineInbound extends BaseEntity {

    /**
     * 药品ID
     */
    private Long medicineId;

    /**
     * 入库批次号
     */
    private String batchNumber;

    /**
     * 入库数量
     */
    private Integer quantity;

    /**
     * 单位：盒/瓶/支等
     */
    private String unit;

    /**
     * 进价
     */
    private BigDecimal purchasePrice;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 供应商
     */
    private String supplier;

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
     * 入库日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inboundDate;

    /**
     * 入库人员ID
     */
    private Long operatorId;

    /**
     * 入库人员名称
     */
    private String operatorName;

    /**
     * 状态：1-正常/0-已撤销
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}