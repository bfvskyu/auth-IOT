package com.yss.authserver.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username; // 账号
    private String password; // 密码
    private String code;     // 用户输入的验证码答案
    private String uuid;     // 前端传回来的凭证号
}
