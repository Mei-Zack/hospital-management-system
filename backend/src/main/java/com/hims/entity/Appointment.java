package com.hims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointments")
public class Appointment extends BaseEntity {

    private Long patientId;

    private Long doctorId;

    private LocalDateTime appointmentTime;

    private String status;

    private String description;

    private String remark;
}