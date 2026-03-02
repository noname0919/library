import request from '@/utils/request'

// 查询图书基本信息列表
export function listBook(query) {
  return request({
    url: '/library/book/list',
    method: 'get',
    params: query
  })
}

// 查询图书基本信息详细
export function getBook(id) {
  return request({
    url: '/library/book/' + id,
    method: 'get'
  })
}

// 新增图书基本信息
export function addBook(data) {
  return request({
    url: '/library/book',
    method: 'post',
    data: data
  })
}

// 修改图书基本信息
export function updateBook(data) {
  return request({
    url: '/library/book',
    method: 'put',
    data: data
  })
}

// 删除图书基本信息
export function delBook(id) {
  return request({
    url: '/library/book/' + id,
    method: 'delete'
  })
}

// 借书
export function borrowBook(bookId) {
  return request({
    url: '/library/book/borrow/' + bookId,
    method: 'post'
  })
}

// 还书
export function returnBook(recordId) {
  return request({
    url: '/library/book/return/' + recordId,
    method: 'put'
  })
}
