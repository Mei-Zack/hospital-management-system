package com.hims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hims.common.Result;
import com.hims.entity.MedicineSale;
import com.hims.service.MedicineSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicine-sales")
public class MedicineSaleController {

    @Autowired
    private MedicineSaleService medicineSaleService;

    /**
     * 分页查询销售记录
     */
    @GetMapping("/page")
    public Result<IPage<MedicineSale>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) String saleNumber,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // 转换日期为日期时间
        LocalDateTime startTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Page<MedicineSale> page = new Page<>(current, size);
        IPage<MedicineSale> result = medicineSaleService.page(page, medicineId, saleNumber, patientName, startTime,
                endTime);
        return Result.success(result);
    }

    /**
     * 获取销售记录详情
     */
    @GetMapping("/{id}")
    public Result<MedicineSale> getById(@PathVariable Long id) {
        MedicineSale sale = medicineSaleService.getById(id);
        return Result.success(sale);
    }

    /**
     * 添加销售记录
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Boolean> add(@RequestBody MedicineSale sale) {
        boolean result = medicineSaleService.add(sale);
        return Result.success(result);
    }

    /**
     * 退货
     */
    @PutMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Boolean> refund(@PathVariable Long id) {
        boolean result = medicineSaleService.refund(id);
        return Result.success(result);
    }

    /**
     * 统计指定日期范围内的销售总金额
     */
    @GetMapping("/statistics/total-amount")
    public Result<BigDecimal> sumTotalAmountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        BigDecimal result = medicineSaleService.sumTotalAmountByTimeRange(startTime, endTime);
        return Result.success(result);
    }

    /**
     * 统计指定日期范围内的销售数量
     */
    @GetMapping("/statistics/count")
    public Result<Integer> countByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        Integer result = medicineSaleService.countByTimeRange(startTime, endTime);
        return Result.success(result);
    }

    /**
     * 按日期统计销售金额
     */
    @GetMapping("/statistics/by-date")
    public Result<List<Map<String, Object>>> statisticsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> result = medicineSaleService.statisticsByDate(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 按药品分类统计销售金额
     */
    @GetMapping("/statistics/by-category")
    public Result<List<Map<String, Object>>> statisticsByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> result = medicineSaleService.statisticsByCategory(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 统计销售量前十的药品
     */
    @GetMapping("/statistics/top-selling")
    public Result<List<Map<String, Object>>> findTopSellingMedicines(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> result = medicineSaleService.findTopSellingMedicines(startDate, endDate);
        return Result.success(result);
    }
}