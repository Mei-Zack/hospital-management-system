package com.hims.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hims.entity.Doctor;

public interface DoctorService extends IService<Doctor> {
    void updateSchedule(Long doctorId, String schedule);

    void updateStatus(Long doctorId, Integer status);
}