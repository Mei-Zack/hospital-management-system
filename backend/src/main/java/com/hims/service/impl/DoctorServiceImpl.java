package com.hims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hims.entity.Doctor;
import com.hims.mapper.DoctorMapper;
import com.hims.service.DoctorService;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean save(Doctor doctor) {
        validateScheduleJson(doctor);
        return super.save(doctor);
    }

    @Override
    public boolean updateById(Doctor doctor) {
        validateScheduleJson(doctor);
        return super.updateById(doctor);
    }

    @Override
    public void updateSchedule(Long doctorId, String schedule) {
        // 验证JSON格式
        validateJson(schedule);

        Doctor doctor = this.getById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("医生不存在");
        }
        doctor.setSchedule(schedule);
        this.updateById(doctor);
    }

    @Override
    public void updateStatus(Long doctorId, Integer status) {
        Doctor doctor = this.getById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("医生不存在");
        }
        doctor.setStatus(status);
        this.updateById(doctor);
    }

    /**
     * 验证Doctor对象中的schedule字段是否为有效的JSON
     */
    private void validateScheduleJson(Doctor doctor) {
        if (doctor != null && doctor.getSchedule() != null && !doctor.getSchedule().isEmpty()) {
            validateJson(doctor.getSchedule());
        }
    }

    /**
     * 验证字符串是否为有效的JSON
     */
    private void validateJson(String jsonString) {
        try {
            objectMapper.readTree(jsonString);
        } catch (IOException e) {
            throw new RuntimeException("无效的JSON格式: " + e.getMessage());
        }
    }
}