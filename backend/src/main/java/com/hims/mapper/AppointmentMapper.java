package com.hims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hims.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}