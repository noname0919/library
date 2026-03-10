import request from '@/utils/request'

// 查询借阅记录列表（借阅中+逾期）
export function listBorrow(query) {
  return request({
    url: '/library/borrow/borrowing',
    method: 'get',
    params: query
  })
}

// 查询归还记录列表（已归还）
export function listReturn(query) {
  return request({
    url: '/library/borrow/returned',
    method: 'get',
    params: query
  })
}

// 查询所有借阅记录（包括已归还）
export function listAllBorrow(query) {
  return request({
    url: '/library/borrow/list',
    method: 'get',
    params: query
  })
}

// 查询借阅记录详细
export function getBorrow(id) {
  return request({
    url: '/library/borrow/' + id,
    method: 'get'
  })
}

// 新增借阅记录（借书）
export function addBorrow(data) {
  return request({
    url: '/library/borrow',
    method: 'post',
    data: data
  })
}

// 续借图书
export function renewBorrow(id) {
  return request({
    url: '/library/borrow/renew/' + id,
    method: 'put'
  })
}

// 归还图书
export function returnBorrow(id) {
  return request({
    url: '/library/borrow/return/' + id,
    method: 'put'
  })
}

// 删除借阅记录
export function delBorrow(id) {
  return request({
    url: '/library/borrow/' + id,
    method: 'delete'
  })
}
