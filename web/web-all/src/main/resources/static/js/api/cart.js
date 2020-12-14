var cart = {

    api_name: '/api/cart',

  // 添加购物车 购物车的数量变化
  addToCart(skuId, skuNum) {
    return request({
      //  url: this.api_name + '/addToCart/' + skuId + '/' + skuNum,以下是暂时写的是暂时的到时候写了网关需要
      url: 'http://api.gmall.com:8201/api/cart' + '/addToCart/' + skuId + '/' + skuNum,
      method: 'post'
    })
  },

  // 我的购物车 根据我们的用户id查询我们的购物车的数量
  cartList() {
    return request({
      //this.api_name + '/cartList'
      url: 'http://api.gmall.com:8201/api/cart/cartList',//这个以后得修改回来this.api_name + '/cartList',
      method: 'get'
    })
  },

  // 更新选中状态
  checkCart(skuId, isChecked) {
    return request({
      //url: this.api_name + '/checkCart/' + skuId + '/' + isChecked,
      url:'http://api.gmall.com:8201/api/cart'+'/checkCart/' + skuId + '/' + isChecked,
      method: 'get'
    })
  },

// 刪除
  deleteCart(skuId) {
    return request({
      url: this.api_name + '/deleteCart/' + skuId,
      method: 'delete'
    })
  }
}
