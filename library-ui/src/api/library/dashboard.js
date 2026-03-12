import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStatistics() {
  return request({
    url: '/library/dashboard/statistics',
    method: 'get'
  })
}

// 获取借阅趋势数据
export function getBorrowTrend(days) {
  return request({
    url: '/library/dashboard/borrow-trend',
    method: 'get',
    params: { days }
  })
}
