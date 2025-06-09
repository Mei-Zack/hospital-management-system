package com.hims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hims.common.Result;
import com.hims.entity.Appointment;
import com.hims.service.AppointmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @CrossOrigin
    @GetMapping("/page")
    public Result<Page<Appointment>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String status) {

        Page<Appointment> page = new Page<>(current, size);
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();

        if (patientId != null) {
            wrapper.eq(Appointment::getPatientId, patientId);
        }
        if (doctorId != null) {
            wrapper.eq(Appointment::getDoctorId, doctorId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Appointment::getStatus, status);
        }

        wrapper.orderByDesc(Appointment::getCreatedTime);

        return Result.success(appointmentService.page(page, wrapper));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Appointment appointment) {
        appointmentService.createAppointment(appointment);
        return Result.success();
    }

    @CrossOrigin
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remark) {

        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }

        // 更新状态
        appointment.setStatus(status);
        // 如果有备注，更新备注
        if (remark != null && !remark.isEmpty()) {
            appointment.setRemark(remark);
        }

        appointmentService.updateById(appointment);
        return Result.success();
    }

    @CrossOrigin
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String remark) {

        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }

        // 更新状态为取消
        appointment.setStatus("CANCELLED");
        // 如果有备注，更新备注
        if (remark != null && !remark.isEmpty()) {
            appointment.setRemark(remark);
        }

        appointmentService.updateById(appointment);
        return Result.success();
    }
}