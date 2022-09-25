//统计菜品数量
const numDish = (ids) => {
    return $axios({
        url: '/Home/numdish',
        method: 'get',
        params: {ids}
    })
}

//统计今日销售额
const  sales= (ids) => {
    return $axios({
        url: '/Home/sales',
        method: 'get',
        params: { ids }
    })
}

//统计今天订单量
const numorders = (ids) => {
    return $axios({
        url: '/Home/numorders',
        method: 'get',
        params: { ids }
    })
}