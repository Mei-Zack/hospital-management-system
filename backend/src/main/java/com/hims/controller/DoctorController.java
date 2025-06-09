package com.hims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hims.common.Result;
import com.hims.entity.Doctor;
import com.hims.service.DoctorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/page")
    public Result<Page<Doctor>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String department) {

        Page<Doctor> page = new Page<>(current, size);
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();

        if (department != null && !department.isEmpty()) {
            wrapper.eq(Doctor::getDepartment, department);
        }

        return Result.success(doctorService.page(page, wrapper));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> save(@RequestBody Doctor doctor) {
        doctorService.save(doctor);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Void> update(@RequestBody Doctor doctor) {
        doctorService.updateById(doctor);
        return Result.success();
    }

    @PutMapping("/{id}/schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Void> updateSchedule(@PathVariable Long id, @RequestBody String schedule) {
        doctorService.updateSchedule(id, schedule);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        doctorService.updateStatus(id, status);
        return Result.success();
    }
}