package com.hims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hims.entity.MedicineSale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MedicineSaleMapper extends BaseMapper<MedicineSale> {

    /**
     * 统计指定日期范围内的销售总金额
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 总金额
     */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM medicine_sales WHERE sale_time BETWEEN #{startTime} AND #{endTime} AND status = 1 AND deleted = 0")
    BigDecimal sumTotalAmountByTimeRange(@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定日期范围内的销售数量
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 销售数量
     */
    @Select("SELECT COUNT(*) FROM medicine_sales WHERE sale_time BETWEEN #{startTime} AND #{endTime} AND status = 1 AND deleted = 0")
    Integer countByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按日期统计销售金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期和金额的映射
     */
    @Select("SELECT DATE(sale_time) as sale_date, SUM(total_amount) as total_amount FROM medicine_sales WHERE DATE(sale_time) BETWEEN #{startDate} AND #{endDate} AND status = 1 AND deleted = 0 GROUP BY DATE(sale_time)")
    List<Map<String, Object>> statisticsByDate(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 按药品分类统计销售金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分类和金额的映射
     */
    @Select("SELECT m.category, SUM(s.total_amount) as total_amount FROM medicine_sales s JOIN medicines m ON s.medicine_id = m.id WHERE DATE(s.sale_time) BETWEEN #{startDate} AND #{endDate} AND s.status = 1 AND s.deleted = 0 GROUP BY m.category")
    List<Map<String, Object>> statisticsByCategory(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 统计销售量前十的药品
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 药品和销售量的映射
     */
    @Select("SELECT m.name, SUM(s.quantity) as total_quantity FROM medicine_sales s JOIN medicines m ON s.medicine_id = m.id WHERE DATE(s.sale_time) BETWEEN #{startDate} AND #{endDate} AND s.status = 1 AND s.deleted = 0 GROUP BY s.medicine_id ORDER BY total_quantity DESC LIMIT 10")
    List<Map<String, Object>> findTopSellingMedicines(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}