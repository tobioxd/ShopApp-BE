package com.shopapp.controllers;

import com.shopapp.dtos.*;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.models.OrderDetail;
import com.shopapp.responses.OrderDetailResponse;
import com.shopapp.services.OrderDetailService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor

public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    
    @PostMapping("")
    @Operation(summary = "Create order detail")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> createOrderDetail(
            @Valid  @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Order Detail")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") Long id) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
        //return ResponseEntity.ok(orderDetail);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get All Order Detail by OrderId")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable("orderId") Long orderId
    ) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails
                .stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();
        return ResponseEntity.ok(orderDetailResponses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update OrderDetail")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable("id") Long id,
            @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok().body(orderDetail);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delet Order Detail")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @PathVariable("id") Long id) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok().body("Delete Order detail with id : "+id+" successfully");
        //return ResponseEntity.noContent().build();
    }
}
