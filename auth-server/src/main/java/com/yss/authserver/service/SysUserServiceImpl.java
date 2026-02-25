package com.yss.authserver.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yss.authserver.common.Result;
import com.yss.authserver.dto.LoginDTO;
import com.yss.authserver.entity.SysUser;
import com.yss.authserver.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysUserMapper sysUserMapper;
    @Override
    public Result<String> login(LoginDTO loginDTO) {
        // ================= 1. 校验验证码 (Redis 阶段) =================
        // 拼接出刚才存答案用的那个保密柜专属标签 (Key)
        String redisKey = "captcha:uuid:" + loginDTO.getUuid();
        // 去 Redis 里取答案
        String redisCode = stringRedisTemplate.opsForValue().get(redisKey);
        // 忽略大小写比对验证码
        if (redisCode == null) {
            return Result.error("验证码已过期，请重新获取");
        }
        if (!redisCode.equalsIgnoreCase(loginDTO.getCode())) {
            return Result.error("验证码错误");
        }
        // 验证码只要比对成功，必须立刻从 Redis 中销毁，防止被恶意重复利用！
        stringRedisTemplate.delete(redisKey);
        // ================= 2. 校验账号密码 (MyBatis-Plus 阶段) =================
        // 构造条件：查出 username 等于前端传来的 username 的那条数据
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if (sysUser == null) {
            return Result.error("账号不存在");
        }
        if (!sysUser.getPassword().equals(loginDTO.getPassword())) {
            return Result.error("密码错误");
        }
        // ================= 3. 签发 Token (Sa-Token 阶段) =================
        // 密码全对！告诉 Sa-Token 这个用户 (id) 登录成功了。
        // 就这一行代码，Sa-Token 会自动帮你生成 Token，并存入 Redis 中管理起来。
        StpUtil.login(sysUser.getId());
        // ================= 4. 返回结果 =================
        // 获取刚刚生成的 Token 字符串
        String token = StpUtil.getTokenValue();
        // 把 Token 装进快递盒发给前端
        return Result.success(token);
    }
}
