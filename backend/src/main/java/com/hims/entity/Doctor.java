package com.hims.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doctors")
public class Doctor extends BaseEntity {

    private Long userId;

    private String department;

    private String title;

    private String specialty;

    private String introduction;

    private String schedule;

    private Integer status;
}