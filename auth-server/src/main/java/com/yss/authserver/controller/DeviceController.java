package com.yss.authserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yss.authserver.common.Result;
import com.yss.authserver.entity.IotDevice;
import com.yss.authserver.service.IotDeviceService;
import jakarta.annotation.Resource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/device")
public class DeviceController {
    @Resource
    private IotDeviceService iotDeviceService;
    /**
     * 1. 新增设备
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody IotDevice iotDevice){
        boolean save = iotDeviceService.save(iotDevice);
        return save ? Result.success("设备添加成功") : Result.error("设备添加失败");
    }
    @PutMapping("/update")
    public Result<String> update(@RequestBody IotDevice iotDevice){
        boolean update = iotDeviceService.updateById(iotDevice);
        return update ? Result.success("设备修改成功") : Result.error("设备修改失败");
    }
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestBody IotDevice iotDevice){
        boolean delete = iotDeviceService.removeById(iotDevice.getId());
        return delete ? Result.success("设备删除成功") : Result.error("设备删除失败");
    }
    @GetMapping("/page")
    public Result<Page<IotDevice>> getDevicePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "3") Integer pageSize,
            String deviceName
    ){
        // 1. 开启分页对象 (查第几页，每页几条)
        Page<IotDevice> page = new Page<>(pageNum, pageSize);
        //2. 构造查询条件
        LambdaQueryWrapper<IotDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(deviceName),IotDevice::getDeviceName, deviceName);
        queryWrapper.orderByDesc(IotDevice::getCreateTime);
        // 3. 执行查询
        Page<IotDevice> pageResult = iotDeviceService.page(page, queryWrapper);
        return Result.success(pageResult);
    }
}
