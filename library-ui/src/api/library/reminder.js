import request from '@/utils/request'

// 获取用户借阅提醒信息
export function getReminderInfo() {
  return request({
    url: '/library/reminder/info',
    method: 'get'
  })
}
