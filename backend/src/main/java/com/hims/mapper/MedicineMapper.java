package com.hims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hims.entity.Medicine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MedicineMapper extends BaseMapper<Medicine> {

    /**
     * 更新药品库存
     * 
     * @param id       药品ID
     * @param quantity 数量变化（正数增加，负数减少）
     * @return 影响行数
     */
    @Update("UPDATE medicines SET stock = stock + #{quantity} WHERE id = #{id} AND stock + #{quantity} >= 0")
    int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 查询即将过期的药品（有效期小于30天）
     * 
     * @return 药品列表
     */
    @Select("SELECT * FROM medicines WHERE DATEDIFF(expiration_date, CURDATE()) <= 30 AND DATEDIFF(expiration_date, CURDATE()) > 0 AND deleted = 0")
    List<Medicine> findNearExpiration();

    /**
     * 查询已过期的药品
     * 
     * @return 药品列表
     */
    @Select("SELECT * FROM medicines WHERE expiration_date < CURDATE() AND deleted = 0")
    List<Medicine> findExpired();

    /**
     * 查询库存不足的药品（库存小于10）
     * 
     * @return 药品列表
     */
    @Select("SELECT * FROM medicines WHERE stock < 10 AND deleted = 0")
    List<Medicine> findLowStock();
}