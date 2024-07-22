package com.shopapp.services;

import com.shopapp.dtos.OrderDetailDTO;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.models.Order;
import com.shopapp.models.OrderDetail;
import com.shopapp.models.Product;
import com.shopapp.repositories.OrderDetailRepository;
import com.shopapp.repositories.OrderRepository;
import com.shopapp.repositories.ProductRepository;
import com.shopapp.services.interfaces.IOrderDetailService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service

public class OrderDetailService implements IOrderDetailService {
        
        private final OrderRepository orderRepository;
        private final OrderDetailRepository orderDetailRepository;
        private final ProductRepository productRepository;

        @Override
        public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
                // tìm xem orderId có tồn tại ko
                Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Cannot find Order with id : " + orderDetailDTO.getOrderId()));
                // Tìm Product theo id
                Product product = productRepository.findById(orderDetailDTO.getProductId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Cannot find product with id: " + orderDetailDTO.getProductId()));
                OrderDetail orderDetail = OrderDetail.builder()
                                .order(order)
                                .product(product)
                                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                                .price(orderDetailDTO.getPrice())
                                .totalMoney(orderDetailDTO.getTotalMoney())
                                .build();
                // lưu vào db
                return orderDetailRepository.save(orderDetail);
        }

        @Override
        public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
                return orderDetailRepository.findById(id)
                                .orElseThrow(() -> new DataNotFoundException("Cannot find OrderDetail with id: " + id));
        }

        @Override
        public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO)
                        throws DataNotFoundException {
                // Check if orderdetail is exists
                OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Cannot find order detail with id: " + id));
                Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));
                Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Cannot find product with id: " + orderDetailDTO.getProductId()));
                existingOrderDetail.setPrice(orderDetailDTO.getPrice());
                existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
                existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
                existingOrderDetail.setOrder(existingOrder);
                existingOrderDetail.setProduct(existingProduct);
                return orderDetailRepository.save(existingOrderDetail);
        }

        @Override
        public void deleteById(Long id) {
                orderDetailRepository.deleteById(id);
        }

        @Override
        public List<OrderDetail> findByOrderId(Long orderId) {
                return orderDetailRepository.findByOrderId(orderId);
        }
}
