package com.hims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hims.entity.Medicine;
import com.hims.mapper.MedicineMapper;
import com.hims.service.MedicineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MedicineServiceImpl extends ServiceImpl<MedicineMapper, Medicine> implements MedicineService {

    @Override
    public IPage<Medicine> page(Page<Medicine> page, String name, String category, Integer status) {
        LambdaQueryWrapper<Medicine> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(name)) {
            queryWrapper.like(Medicine::getName, name);
        }

        if (StringUtils.hasText(category)) {
            queryWrapper.eq(Medicine::getCategory, category);
        }

        if (status != null) {
            queryWrapper.eq(Medicine::getStatus, status);
        }

        queryWrapper.orderByDesc(Medicine::getUpdatedTime);

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean add(Medicine medicine) {
        // 设置默认状态
        if (medicine.getStatus() == null) {
            medicine.setStatus(1); // 正常状态
        }

        // 检查有效期
        if (medicine.getExpirationDate() != null) {
            LocalDate now = LocalDate.now();
            if (medicine.getExpirationDate().isBefore(now)) {
                medicine.setStatus(4); // 已过期
            } else if (medicine.getExpirationDate().isBefore(now.plusDays(30))) {
                medicine.setStatus(3); // 即将过期
            }
        }

        // 检查库存
        if (medicine.getStock() != null && medicine.getStock() < 10) {
            medicine.setStatus(2); // 缺货
        }

        return this.save(medicine);
    }

    @Override
    @Transactional
    public boolean update(Medicine medicine) {
        // 获取原药品信息
        Medicine oldMedicine = this.getById(medicine.getId());
        if (oldMedicine == null) {
            return false;
        }

        // 检查有效期
        if (medicine.getExpirationDate() != null) {
            LocalDate now = LocalDate.now();
            if (medicine.getExpirationDate().isBefore(now)) {
                medicine.setStatus(4); // 已过期
            } else if (medicine.getExpirationDate().isBefore(now.plusDays(30))) {
                medicine.setStatus(3); // 即将过期
            }
        }

        // 检查库存
        if (medicine.getStock() != null && medicine.getStock() < 10) {
            medicine.setStatus(2); // 缺货
        }

        return this.updateById(medicine);
    }

    @Override
    @Transactional
    public boolean updateStock(Long id, Integer quantity) {
        if (id == null || quantity == null) {
            return false;
        }

        int rows = baseMapper.updateStock(id, quantity);

        if (rows > 0) {
            // 更新成功后，检查并更新药品状态
            Medicine medicine = this.getById(id);
            if (medicine != null) {
                Integer status = medicine.getStatus();
                boolean needUpdate = false;

                // 检查库存状态
                if (medicine.getStock() < 10 && status != 2 && status != 3 && status != 4) {
                    medicine.setStatus(2); // 缺货
                    needUpdate = true;
                } else if (medicine.getStock() >= 10 && status == 2) {
                    medicine.setStatus(1); // 恢复正常
                    needUpdate = true;
                }

                if (needUpdate) {
                    this.updateById(medicine);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public List<Medicine> findNearExpiration() {
        return baseMapper.findNearExpiration();
    }

    @Override
    public List<Medicine> findExpired() {
        return baseMapper.findExpired();
    }

    @Override
    public List<Medicine> findLowStock() {
        return baseMapper.findLowStock();
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics() {
        // 查询所有药品分类及其数量
        LambdaQueryWrapper<Medicine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Medicine::getCategory);
        queryWrapper.groupBy(Medicine::getCategory);

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> categoryCountMap = new HashMap<>();

        // 统计每个分类的药品数量
        List<Medicine> medicines = this.list();
        for (Medicine medicine : medicines) {
            String category = medicine.getCategory();
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        // 转换为前端需要的格式
        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("category", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getStockValueStatistics() {
        // 计算每个分类的库存金额
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, BigDecimal> categoryValueMap = new HashMap<>();

        List<Medicine> medicines = this.list();
        for (Medicine medicine : medicines) {
            String category = medicine.getCategory();
            BigDecimal stockValue = medicine.getPurchasePrice().multiply(new BigDecimal(medicine.getStock()));

            if (categoryValueMap.containsKey(category)) {
                categoryValueMap.put(category, categoryValueMap.get(category).add(stockValue));
            } else {
                categoryValueMap.put(category, stockValue);
            }
        }

        // 转换为前端需要的格式
        for (Map.Entry<String, BigDecimal> entry : categoryValueMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("category", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }

        return result;
    }
}