<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">智联设备管理系统</h2>
      </template>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" size="large" clearable />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>

        <el-form-item prop="code">
          <div class="captcha-container">
            <el-input v-model="loginForm.code" placeholder="验证码" size="large" @keyup.enter="handleLogin" />
            <img v-if="captchaImg" :src="captchaImg" class="captcha-img" @click="fetchCaptcha" title="点击刷新" />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCaptcha, login } from '../api/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
const router = useRouter()
const userStore = useUserStore()
// 表单数据
const loginForm = reactive({
  username: 'admin', // 默认填入咱们数据库里的账号，方便测试
  password: '123456',
  code: '',
  uuid: ''
})

const captchaImg = ref('')
const loading = ref(false)
const loginFormRef = ref(null)

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 获取/刷新验证码
const fetchCaptcha = async () => {
  try {
    const data = await getCaptcha()
    // 把后端返回的 uuid 存入表单准备提交
    loginForm.uuid = data.uuid 
    // 把后端返回的 Base64 字符串拼装成图片 src 格式
    captchaImg.value = data.image
  } catch (error) {
    console.error("获取验证码失败", error)
  }
}

// 点击登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  // 校验表单是否都填了
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const token = await login(loginForm)
        userStore.setToken(token) // 第一步：把拿到的 Token 装进 Pinia 口袋
        ElMessage.success('登录成功！即将进入系统...')
        router.push('/home') // 第二步：命令路由跳转到首页！
        
      } catch (error) {
        // 如果密码或验证码错误，刷新一下验证码，这是标准的安全体验
        fetchCaptcha()
        loginForm.code = '' // 清空填错的验证码
      } finally {
        loading.value = false
      }
    }
  })
}

// 页面一加载，就自动去获取一次验证码
onMounted(() => {
  fetchCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: white; /* 简单的灰白色背景 */
}
.login-card {
  width: 400px;
  border-radius: 8px;
}
.login-title {
  text-align: center;
  margin: 0;
  color: #333;
}
.captcha-container {
  display: flex;
  width: 100%;
  gap: 10px; /* 输入框和图片之间的间距 */
}
.captcha-img {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}
.login-btn {
  width: 100%;
}
</style>