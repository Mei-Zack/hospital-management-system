package com.hims.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hims.entity.MedicineInbound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MedicineInboundService extends IService<MedicineInbound> {

    /**
     * 分页查询入库记录
     * 
     * @param page        分页参数
     * @param medicineId  药品ID（可选）
     * @param batchNumber 批次号（可选）
     * @param startDate   开始日期（可选）
     * @param endDate     结束日期（可选）
     * @return 分页结果
     */
    IPage<MedicineInbound> page(Page<MedicineInbound> page, Long medicineId, String batchNumber, LocalDate startDate,
            LocalDate endDate);

    /**
     * 添加入库记录
     * 
     * @param inbound 入库信息
     * @return 是否成功
     */
    boolean add(MedicineInbound inbound);

    /**
     * 取消入库记录
     * 
     * @param id 入库记录ID
     * @return 是否成功
     */
    boolean cancel(Long id);

    /**
     * 统计指定日期范围内的入库总金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 总金额
     */
    BigDecimal sumTotalAmountByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 统计指定日期范围内的入库数量
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 入库数量
     */
    Integer countByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 按日期统计入库金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期和金额的映射
     */
    List<Map<String, Object>> statisticsByDate(LocalDate startDate, LocalDate endDate);

    /**
     * 按药品分类统计入库金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分类和金额的映射
     */
    List<Map<String, Object>> statisticsByCategory(LocalDate startDate, LocalDate endDate);
}