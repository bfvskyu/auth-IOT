import request from '../utils/request'

// 获取图形验证码
export const getCaptcha = () => {
  return request({
    url: '/api/auth/captcha',
    method: 'get'
  })
}

// 登录接口
export const login = (data) => {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data: data
  })
}