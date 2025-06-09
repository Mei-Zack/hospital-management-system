package com.hims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hims.entity.Appointment;
import com.hims.mapper.AppointmentMapper;
import com.hims.service.AppointmentService;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Override
    public void createAppointment(Appointment appointment) {
        // 设置初始状态
        appointment.setStatus("PENDING");
        this.save(appointment);
    }

    @Override
    public void updateAppointmentStatus(Long appointmentId, String status, String remark) {
        Appointment appointment = this.getById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        appointment.setStatus(status);
        if (remark != null && !remark.isEmpty()) {
            appointment.setRemark(remark);
        }
        this.updateById(appointment);
    }

    @Override
    public void updateAppointmentStatus(Long appointmentId, String status) {
        // 调用带备注的方法，传入null作为备注
        updateAppointmentStatus(appointmentId, status, null);
    }

    @Override
    public void cancelAppointment(Long appointmentId, String remark) {
        Appointment appointment = this.getById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        // 只有待确认和已确认的预约可以取消
        if (!"PENDING".equals(appointment.getStatus()) && !"CONFIRMED".equals(appointment.getStatus())) {
            throw new RuntimeException("当前状态无法取消预约");
        }

        appointment.setStatus("CANCELLED");
        if (remark != null && !remark.isEmpty()) {
            appointment.setRemark(remark);
        }
        this.updateById(appointment);
    }

    @Override
    public void cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = this.getById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        
        // 检查是否是预约本人或医生
        if (!appointment.getPatientId().equals(userId) && !appointment.getDoctorId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }
        
        // 只有待确认和已确认的预约可以取消
        if (!"PENDING".equals(appointment.getStatus()) && !"CONFIRMED".equals(appointment.getStatus())) {
            throw new RuntimeException("当前状态无法取消预约");
        }
        
        appointment.setStatus("CANCELLED");
        this.updateById(appointment);
    }
}