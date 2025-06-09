package com.hims.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hims.entity.Appointment;

public interface AppointmentService extends IService<Appointment> {
    void createAppointment(Appointment appointment);

    void updateAppointmentStatus(Long appointmentId, String status);

    void updateAppointmentStatus(Long appointmentId, String status, String remark);

    void cancelAppointment(Long appointmentId, Long userId);

    void cancelAppointment(Long appointmentId, String remark);
}