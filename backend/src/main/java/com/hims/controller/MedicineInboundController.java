package com.hims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hims.common.Result;
import com.hims.entity.MedicineInbound;
import com.hims.service.MedicineInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicine-inbounds")
public class MedicineInboundController {

    @Autowired
    private MedicineInboundService medicineInboundService;

    /**
     * 分页查询入库记录
     */
    @GetMapping("/page")
    public Result<IPage<MedicineInbound>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) String batchNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Page<MedicineInbound> page = new Page<>(current, size);
        IPage<MedicineInbound> result = medicineInboundService.page(page, medicineId, batchNumber, startDate, endDate);
        return Result.success(result);
    }

    /**
     * 获取入库记录详情
     */
    @GetMapping("/{id}")
    public Result<MedicineInbound> getById(@PathVariable Long id) {
        MedicineInbound inbound = medicineInboundService.getById(id);
        return Result.success(inbound);
    }

    /**
     * 添加入库记录
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Boolean> add(@RequestBody MedicineInbound inbound) {
        boolean result = medicineInboundService.add(inbound);
        return Result.success(result);
    }

    /**
     * 取消入库记录
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Boolean> cancel(@PathVariable Long id) {
        boolean result = medicineInboundService.cancel(id);
        return Result.success(result);
    }

    /**
     * 统计指定日期范围内的入库总金额
     */
    @GetMapping("/statistics/total-amount")
    public Result<BigDecimal> sumTotalAmountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal result = medicineInboundService.sumTotalAmountByDateRange(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 统计指定日期范围内的入库数量
     */
    @GetMapping("/statistics/count")
    public Result<Integer> countByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Integer result = medicineInboundService.countByDateRange(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 按日期统计入库金额
     */
    @GetMapping("/statistics/by-date")
    public Result<List<Map<String, Object>>> statisticsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> result = medicineInboundService.statisticsByDate(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 按药品分类统计入库金额
     */
    @GetMapping("/statistics/by-category")
    public Result<List<Map<String, Object>>> statisticsByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> result = medicineInboundService.statisticsByCategory(startDate, endDate);
        return Result.success(result);
    }
}