package com.yss.authserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yss.authserver.common.Result;
import com.yss.authserver.entity.IotDevice;
import com.yss.authserver.mapper.IotDeviceMapper;
import org.springframework.stereotype.Service;

@Service
public class IotDeviceServiceImpl extends ServiceImpl<IotDeviceMapper, IotDevice> implements IotDeviceService {
}

