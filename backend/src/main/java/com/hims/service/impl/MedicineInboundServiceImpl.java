package com.hims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hims.entity.MedicineInbound;
import com.hims.mapper.MedicineInboundMapper;
import com.hims.service.MedicineInboundService;
import com.hims.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MedicineInboundServiceImpl extends ServiceImpl<MedicineInboundMapper, MedicineInbound>
        implements MedicineInboundService {

    @Autowired
    private MedicineService medicineService;

    @Override
    public IPage<MedicineInbound> page(Page<MedicineInbound> page, Long medicineId, String batchNumber,
            LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<MedicineInbound> queryWrapper = new LambdaQueryWrapper<>();

        if (medicineId != null) {
            queryWrapper.eq(MedicineInbound::getMedicineId, medicineId);
        }

        if (batchNumber != null && !batchNumber.isEmpty()) {
            queryWrapper.eq(MedicineInbound::getBatchNumber, batchNumber);
        }

        if (startDate != null && endDate != null) {
            queryWrapper.between(MedicineInbound::getInboundDate, startDate, endDate);
        } else if (startDate != null) {
            queryWrapper.ge(MedicineInbound::getInboundDate, startDate);
        } else if (endDate != null) {
            queryWrapper.le(MedicineInbound::getInboundDate, endDate);
        }

        queryWrapper.orderByDesc(MedicineInbound::getInboundDate);

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean add(MedicineInbound inbound) {
        // 计算总金额
        if (inbound.getPurchasePrice() != null && inbound.getQuantity() != null) {
            BigDecimal totalAmount = inbound.getPurchasePrice().multiply(new BigDecimal(inbound.getQuantity()));
            inbound.setTotalAmount(totalAmount);
        }

        // 设置默认状态
        if (inbound.getStatus() == null) {
            inbound.setStatus(1); // 正常状态
        }

        // 设置入库日期
        if (inbound.getInboundDate() == null) {
            inbound.setInboundDate(LocalDate.now());
        }

        boolean result = this.save(inbound);

        // 更新药品库存
        if (result && inbound.getMedicineId() != null && inbound.getQuantity() != null) {
            medicineService.updateStock(inbound.getMedicineId(), inbound.getQuantity());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean cancel(Long id) {
        MedicineInbound inbound = this.getById(id);
        if (inbound == null || inbound.getStatus() == 0) {
            return false;
        }

        // 更新状态为已撤销
        inbound.setStatus(0);
        boolean result = this.updateById(inbound);

        // 更新药品库存
        if (result && inbound.getMedicineId() != null && inbound.getQuantity() != null) {
            medicineService.updateStock(inbound.getMedicineId(), -inbound.getQuantity());
        }

        return result;
    }

    @Override
    public BigDecimal sumTotalAmountByDateRange(LocalDate startDate, LocalDate endDate) {
        return baseMapper.sumTotalAmountByDateRange(startDate, endDate);
    }

    @Override
    public Integer countByDateRange(LocalDate startDate, LocalDate endDate) {
        return baseMapper.countByDateRange(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> statisticsByDate(LocalDate startDate, LocalDate endDate) {
        return baseMapper.statisticsByDate(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> statisticsByCategory(LocalDate startDate, LocalDate endDate) {
        return baseMapper.statisticsByCategory(startDate, endDate);
    }
}