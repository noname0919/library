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

// 获取图书分类借阅统计
export function getCategoryBorrowStats(days) {
  return request({
    url: '/library/dashboard/category-borrow-stats',
    method: 'get',
    params: { days }
  })
}

// 获取TOP10热门图书
export function getTopBorrowedBooks(days) {
  return request({
    url: '/library/dashboard/top-borrowed-books',
    method: 'get',
    params: { days }
  })
}

// 获取TOP10活跃读者
export function getTopActiveReaders(days) {
  return request({
    url: '/library/dashboard/top-active-readers',
    method: 'get',
    params: { days }
  })
}

// 获取库存不足预警
export function getStockWarning() {
  return request({
    url: '/library/dashboard/stock-warning',
    method: 'get'
  })
}

// 导出今日借阅记录
export function exportTodayBorrow() {
  return request({
    url: '/library/dashboard/export-today-borrow',
    method: 'post',
    responseType: 'blob'
  })
}

// 导出今日归还记录
export function exportTodayReturn() {
  return request({
    url: '/library/dashboard/export-today-return',
    method: 'post',
    responseType: 'blob'
  })
}

// 导出逾期未还记录
export function exportOverdue() {
  return request({
    url: '/library/dashboard/export-overdue',
    method: 'post',
    responseType: 'blob'
  })
}

// 导出图书分类占比
export function exportCategoryStats() {
  return request({
    url: '/library/dashboard/export-category-stats',
    method: 'post',
    responseType: 'blob'
  })
}
