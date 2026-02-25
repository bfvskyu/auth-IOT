package com.yss.authserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yss.authserver.common.Result;
import com.yss.authserver.dto.LoginDTO;
import com.yss.authserver.entity.SysUser;
import org.springframework.stereotype.Service;

public interface SysUserService extends IService<SysUser> {
    Result<String> login(LoginDTO loginDTO);
}
