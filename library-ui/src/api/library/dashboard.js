import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStatistics() {
  return request({
    url: '/library/dashboard/statistics',
    method: 'get'
  })
}
