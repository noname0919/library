import request from '@/utils/request'

// 发送消息到AI
export function sendMessage(data) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: data
  })
}

// 测试AI服务连接
export function testAIConnection() {
  return request({
    url: '/ai/test',
    method: 'get'
  })
}
