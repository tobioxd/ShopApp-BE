package com.shopapp.services.interfaces;

import com.shopapp.dtos.OrderDTO;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.models.Order;

import java.util.List;

public interface IOrderService {

    Order createOrder(OrderDTO orderDTO) throws DataNotFoundException;

    Order getOrderById(Long id) throws DataNotFoundException;

    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;

    void deleteOrder(Long id) throws DataNotFoundException;

    List<Order> getAllOrders(Long userId);
    
}
