import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建一个 axios 实例
const request = axios.create({
  baseURL: '', // 因为我们在 vite 里配置了代理，这里留空即可
  timeout: 5000 // 请求超时时间：5秒
})

// 响应拦截器（帮你自动剥开后端那层 Result 包装盒）
request.interceptors.response.use(
  response => {
    const res = response.data
    // 如果后端的 code 是 200，说明成功，直接返回里面的 data
    if (res.code === 200) {
      return res.data
    } else {
      // 如果后端返回 500（比如验证码错误），直接用 Element Plus 弹窗报错！
      ElMessage.error(res.message || '系统错误')
      return Promise.reject(new Error(res.message || 'Error'))
    }
  },
  error => {
    ElMessage.error('网络连接异常')
    return Promise.reject(error)
  }
)

export default request