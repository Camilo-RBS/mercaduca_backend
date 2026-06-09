package com.mercaduca.orders.mapper;

import com.mercaduca.orders.dto.OrderDTOs;
import com.mercaduca.orders.entity.Order;
import com.mercaduca.orders.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "buyerName",
            expression = "java(order.getBuyer().getFirstName() + \" \" + order.getBuyer().getLastName())")
    OrderDTOs.OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "subtotal",
            expression = "java(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    OrderDTOs.OrderItemResponse toItemResponse(OrderItem item);
}