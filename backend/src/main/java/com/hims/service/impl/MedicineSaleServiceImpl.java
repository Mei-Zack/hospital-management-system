package com.hims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hims.entity.MedicineSale;
import com.hims.mapper.MedicineSaleMapper;
import com.hims.service.MedicineSaleService;
import com.hims.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MedicineSaleServiceImpl extends ServiceImpl<MedicineSaleMapper, MedicineSale>
        implements MedicineSaleService {

    @Autowired
    private MedicineService medicineService;

    @Override
    public IPage<MedicineSale> page(Page<MedicineSale> page, Long medicineId, String saleNumber, String patientName,
            LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<MedicineSale> queryWrapper = new LambdaQueryWrapper<>();

        if (medicineId != null) {
            queryWrapper.eq(MedicineSale::getMedicineId, medicineId);
        }

        if (saleNumber != null && !saleNumber.isEmpty()) {
            queryWrapper.eq(MedicineSale::getSaleNumber, saleNumber);
        }

        if (patientName != null && !patientName.isEmpty()) {
            queryWrapper.like(MedicineSale::getPatientName, patientName);
        }

        if (startTime != null && endTime != null) {
            queryWrapper.between(MedicineSale::getSaleTime, startTime, endTime);
        } else if (startTime != null) {
            queryWrapper.ge(MedicineSale::getSaleTime, startTime);
        } else if (endTime != null) {
            queryWrapper.le(MedicineSale::getSaleTime, endTime);
        }

        queryWrapper.orderByDesc(MedicineSale::getSaleTime);

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean add(MedicineSale sale) {
        // 生成销售单号
        if (sale.getSaleNumber() == null || sale.getSaleNumber().isEmpty()) {
            sale.setSaleNumber(generateSaleNumber());
        }

        // 计算总金额
        if (sale.getSalePrice() != null && sale.getQuantity() != null) {
            BigDecimal totalAmount = sale.getSalePrice().multiply(new BigDecimal(sale.getQuantity()));
            sale.setTotalAmount(totalAmount);
        }

        // 设置默认状态
        if (sale.getStatus() == null) {
            sale.setStatus(1); // 正常状态
        }

        // 设置销售时间
        if (sale.getSaleTime() == null) {
            sale.setSaleTime(LocalDateTime.now());
        }

        boolean result = this.save(sale);

        // 更新药品库存
        if (result && sale.getMedicineId() != null && sale.getQuantity() != null) {
            medicineService.updateStock(sale.getMedicineId(), -sale.getQuantity());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean refund(Long id) {
        MedicineSale sale = this.getById(id);
        if (sale == null || sale.getStatus() == 0) {
            return false;
        }

        // 更新状态为已退货
        sale.setStatus(0);
        boolean result = this.updateById(sale);

        // 更新药品库存
        if (result && sale.getMedicineId() != null && sale.getQuantity() != null) {
            medicineService.updateStock(sale.getMedicineId(), sale.getQuantity());
        }

        return result;
    }

    @Override
    public BigDecimal sumTotalAmountByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.sumTotalAmountByTimeRange(startTime, endTime);
    }

    @Override
    public Integer countByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.countByTimeRange(startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> statisticsByDate(LocalDate startDate, LocalDate endDate) {
        return baseMapper.statisticsByDate(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> statisticsByCategory(LocalDate startDate, LocalDate endDate) {
        return baseMapper.statisticsByCategory(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> findTopSellingMedicines(LocalDate startDate, LocalDate endDate) {
        return baseMapper.findTopSellingMedicines(startDate, endDate);
    }

    /**
     * 生成销售单号
     * 
     * @return 销售单号
     */
    private String generateSaleNumber() {
        // 生成格式：SALE + 年月日 + 6位随机数
        String date = LocalDate.now().toString().replace("-", "");
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "SALE" + date + random;
    }
}