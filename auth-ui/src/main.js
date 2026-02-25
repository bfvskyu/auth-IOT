import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// 引入 Element Plus 和它的样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 【极其关键的新增】：引入 Pinia
import { createPinia } from 'pinia'

const app = createApp(App)
const pinia = createPinia() // 创建 Pinia 实例

app.use(pinia) // 必须把它挂载到 app 上！
app.use(router)
app.use(ElementPlus)

app.mount('#app')