package com.healthy.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthy.diet.entity.OrderDetail;
import com.healthy.diet.entity.Orders;

import java.util.List;

public interface OrdersService extends IService<Orders> {

    public void submit(Orders orders);

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId);
}
