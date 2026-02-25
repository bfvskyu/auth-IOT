package com.yss.authserver.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("iot_device")
public class IotDevice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceName;

    private String deviceSn;

    private String type;

    private Integer status; // 0离线，1在线

    private String location;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
