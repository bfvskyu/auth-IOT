import request from '../utils/request'

// 1. 分页查询设备
// 注意：因为是 GET 请求，参数必须放在 params 里，axios 会自动帮你拼到 URL 后面 (?pageNum=1&pageSize=10)
export const getDevicePage = (params) => {
  return request({
    url: '/api/device/page',
    method: 'get',
    params: params 
  })
}

// 2. 新增设备
export const addDevice = (data) => {
  return request({
    url: '/api/device/add',
    method: 'post',
    data: data
  })
}

// 3. 修改设备
export const updateDevice = (data) => {
  return request({
    url: '/api/device/update',
    method: 'put',
    data: data
  })
}

// 4. 删除设备
// 注意：后端删除接口用的是 @RequestBody，所以这里必须用 data 传参
export const deleteDevice = (data) => {
  return request({
    url: '/api/device/delete',
    method: 'delete',
    data: data
  })
}