package com.hims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("medicine_sales")
public class MedicineSale extends BaseEntity {

    /**
     * 销售单号
     */
    private String saleNumber;

    /**
     * 药品ID
     */
    private Long medicineId;

    /**
     * 批次号
     */
    private String batchNumber;

    /**
     * 销售数量
     */
    private Integer quantity;

    /**
     * 单位：盒/瓶/支等
     */
    private String unit;

    /**
     * 售价
     */
    private BigDecimal salePrice;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 患者ID（可选）
     */
    private Long patientId;

    /**
     * 患者姓名（可选）
     */
    private String patientName;

    /**
     * 销售日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime saleTime;

    /**
     * 销售人员ID
     */
    private Long operatorId;

    /**
     * 销售人员名称
     */
    private String operatorName;

    /**
     * 处方ID（可选）
     */
    private Long prescriptionId;

    /**
     * 状态：1-正常/0-已退货
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}