package com.hims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hims.common.Result;
import com.hims.entity.Medicine;
import com.hims.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    /**
     * 分页查询药品
     */
    @GetMapping("/page")
    public Result<IPage<Medicine>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        Page<Medicine> page = new Page<>(current, size);
        IPage<Medicine> result = medicineService.page(page, name, category, status);
        return Result.success(result);
    }

    /**
     * 获取药品详情
     */
    @GetMapping("/{id}")
    public Result<Medicine> getById(@PathVariable Long id) {
        Medicine medicine = medicineService.getById(id);
        return Result.success(medicine);
    }

    /**
     * 添加药品
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Boolean> add(@RequestBody Medicine medicine) {
        boolean result = medicineService.add(medicine);
        return Result.success(result);
    }

    /**
     * 更新药品
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Boolean> update(@RequestBody Medicine medicine) {
        boolean result = medicineService.update(medicine);
        return Result.success(result);
    }

    /**
     * 删除药品
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = medicineService.removeById(id);
        return Result.success(result);
    }

    /**
     * 更新药品库存
     */
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Boolean> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean result = medicineService.updateStock(id, quantity);
        return Result.success(result);
    }

    /**
     * 查询即将过期的药品
     */
    @GetMapping("/near-expiration")
    public Result<List<Medicine>> findNearExpiration() {
        List<Medicine> list = medicineService.findNearExpiration();
        return Result.success(list);
    }

    /**
     * 查询已过期的药品
     */
    @GetMapping("/expired")
    public Result<List<Medicine>> findExpired() {
        List<Medicine> list = medicineService.findExpired();
        return Result.success(list);
    }

    /**
     * 查询库存不足的药品
     */
    @GetMapping("/low-stock")
    public Result<List<Medicine>> findLowStock() {
        List<Medicine> list = medicineService.findLowStock();
        return Result.success(list);
    }

    /**
     * 获取药品分类统计
     */
    @GetMapping("/statistics/category")
    public Result<List<Map<String, Object>>> getCategoryStatistics() {
        List<Map<String, Object>> result = medicineService.getCategoryStatistics();
        return Result.success(result);
    }

    /**
     * 获取库存金额统计
     */
    @GetMapping("/statistics/stock-value")
    public Result<List<Map<String, Object>>> getStockValueStatistics() {
        List<Map<String, Object>> result = medicineService.getStockValueStatistics();
        return Result.success(result);
    }
}