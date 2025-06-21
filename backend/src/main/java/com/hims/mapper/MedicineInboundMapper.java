package com.hims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hims.entity.MedicineInbound;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface MedicineInboundMapper extends BaseMapper<MedicineInbound> {

    /**
     * 统计指定日期范围内的入库总金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 总金额
     */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM medicine_inbounds WHERE inbound_date BETWEEN #{startDate} AND #{endDate} AND status = 1 AND deleted = 0")
    BigDecimal sumTotalAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定日期范围内的入库数量
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 入库数量
     */
    @Select("SELECT COUNT(*) FROM medicine_inbounds WHERE inbound_date BETWEEN #{startDate} AND #{endDate} AND status = 1 AND deleted = 0")
    Integer countByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 按日期统计入库金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期和金额的映射
     */
    @Select("SELECT inbound_date, SUM(total_amount) as total_amount FROM medicine_inbounds WHERE inbound_date BETWEEN #{startDate} AND #{endDate} AND status = 1 AND deleted = 0 GROUP BY inbound_date")
    List<Map<String, Object>> statisticsByDate(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 按药品分类统计入库金额
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分类和金额的映射
     */
    @Select("SELECT m.category, SUM(i.total_amount) as total_amount FROM medicine_inbounds i JOIN medicines m ON i.medicine_id = m.id WHERE i.inbound_date BETWEEN #{startDate} AND #{endDate} AND i.status = 1 AND i.deleted = 0 GROUP BY m.category")
    List<Map<String, Object>> statisticsByCategory(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}