package com.hims.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hims.entity.MedicineSale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MedicineSaleService extends IService<MedicineSale> {

    /**
     * 分页查询销售记录
     * 
     * @param page        分页参数
     * @param medicineId  药品ID（可选）
     * @param saleNumber  销售单号（可选）
     * @param patientName 患者姓名（可选）
     * @param startTime   开始时间（可选）
     * @param endTime     结束时间（可选）
     * @return 分页结果
     */
    IPage<MedicineSale> page(Page<MedicineSale> page, Long medicineId, String saleNumber, String patientName,
            LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 添加销售记录
     * 
     * @param sale 销售信息
     * @return 是否成功
     */
    boolean add(MedicineSale sale);

    /**
     * 退货
     * 
     * @param id 销售记录ID
     * @return 是否成功
     */
    boolean refund(Long id);

    /**
     * 统计指定日期范围内的销售总金额
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 总金额
     */
    BigDecimal sumTotalAmountByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定日期范围内的销售数量
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 销售数量
     */
    Integer countByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 按日期统计销售金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期和金额的映射
     */
    List<Map<String, Object>> statisticsByDate(LocalDate startDate, LocalDate endDate);

    /**
     * 按药品分类统计销售金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分类和金额的映射
     */
    List<Map<String, Object>> statisticsByCategory(LocalDate startDate, LocalDate endDate);

    /**
     * 统计销售量前十的药品
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 药品和销售量的映射
     */
    List<Map<String, Object>> findTopSellingMedicines(LocalDate startDate, LocalDate endDate);
}