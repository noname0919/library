import request from '@/utils/request'

export function randomRecommend(limit) {
  return request({
    url: '/library/recommend/random',
    method: 'get',
    params: { limit }
  })
}

export function hotRecommend(limit) {
  return request({
    url: '/library/recommend/hot',
    method: 'get',
    params: { limit }
  })
}

export function keywordRecommend(limit) {
  return request({
    url: '/library/recommend/keyword',
    method: 'get',
    params: { limit }
  })
}

export function mixedRecommend(limit) {
  return request({
    url: '/library/recommend/all',
    method: 'get',
    params: { limit }
  })
}
