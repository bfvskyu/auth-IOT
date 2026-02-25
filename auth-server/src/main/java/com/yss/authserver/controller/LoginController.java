package com.yss.authserver.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.yss.authserver.common.Result;
import com.yss.authserver.dto.LoginDTO;
import com.yss.authserver.entity.SysUser;
import com.yss.authserver.mapper.SysUserMapper;
import com.yss.authserver.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    // 注入 Redis 模板
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 注入 MyBatis-Plus 的 Mapper 用于查库
    @Autowired
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserService sysUserService;
    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        // 1. 使用 Hutool 生成线段干扰的验证码 (宽120, 高40, 4个字符, 2条干扰线)
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 2);
        // 获取验证码的文本答案 (比如 "abcd")
        String code = captcha.getCode();
        System.out.println("【作弊器】真实的验证码答案是：" + code);
        // 获取验证码图片的 Base64 编码 (前端可以直接把它当成图片的 src 渲染)
        String imageBase64Data = captcha.getImageBase64Data();
        // 2. 生成一个唯一的 UUID 作为 Redis 的 Key
        String uuid = IdUtil.simpleUUID();
        // 加个前缀好区分
        String redisKey = "captcha:uuid:" + uuid;
        // 3. 将答案存入 Redis！(这就是你接触 Redis 的第一行代码)
        // 参数依次为：Key, Value, 过期时间, 时间单位
        stringRedisTemplate.opsForValue().set(redisKey, code, 60, TimeUnit.MINUTES);
        // 4. 将 UUID 和 图片打包返回给前端
        Map<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("image", imageBase64Data);
        return Result.success(map);
    }
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        return sysUserService.login(loginDTO);
    }
}
