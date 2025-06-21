package com.hims.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hims.entity.Medicine;

import java.util.List;
import java.util.Map;

public interface MedicineService extends IService<Medicine> {

    /**
     * 分页查询药品
     * 
     * @param page     分页参数
     * @param name     药品名称（可选）
     * @param category 药品分类（可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    IPage<Medicine> page(Page<Medicine> page, String name, String category, Integer status);

    /**
     * 添加药品
     * 
     * @param medicine 药品信息
     * @return 是否成功
     */
    boolean add(Medicine medicine);

    /**
     * 更新药品
     * 
     * @param medicine 药品信息
     * @return 是否成功
     */
    boolean update(Medicine medicine);

    /**
     * 更新药品库存
     * 
     * @param id       药品ID
     * @param quantity 数量变化（正数增加，负数减少）
     * @return 是否成功
     */
    boolean updateStock(Long id, Integer quantity);

    /**
     * 查询即将过期的药品
     * 
     * @return 药品列表
     */
    List<Medicine> findNearExpiration();

    /**
     * 查询已过期的药品
     * 
     * @return 药品列表
     */
    List<Medicine> findExpired();

    /**
     * 查询库存不足的药品
     * 
     * @return 药品列表
     */
    List<Medicine> findLowStock();

    /**
     * 获取药品分类统计
     * 
     * @return 分类和数量的映射
     */
    List<Map<String, Object>> getCategoryStatistics();

    /**
     * 获取库存金额统计
     * 
     * @return 分类和金额的映射
     */
    List<Map<String, Object>> getStockValueStatistics();
}