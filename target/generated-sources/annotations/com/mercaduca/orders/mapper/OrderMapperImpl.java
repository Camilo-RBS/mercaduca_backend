package com.mercaduca.orders.mapper;

import com.mercaduca.orders.dto.OrderDTOs;
import com.mercaduca.orders.entity.Order;
import com.mercaduca.orders.entity.OrderItem;
import com.mercaduca.products.entity.Product;
import com.mercaduca.users.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-08T19:19:15-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDTOs.OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDTOs.OrderResponse orderResponse = new OrderDTOs.OrderResponse();

        orderResponse.setBuyerId( orderBuyerId( order ) );
        orderResponse.setId( order.getId() );
        orderResponse.setOrderNumber( order.getOrderNumber() );
        orderResponse.setItems( orderItemListToOrderItemResponseList( order.getItems() ) );
        orderResponse.setStatus( order.getStatus() );
        orderResponse.setSubtotal( order.getSubtotal() );
        orderResponse.setDiscountAmount( order.getDiscountAmount() );
        orderResponse.setShippingCost( order.getShippingCost() );
        orderResponse.setTotal( order.getTotal() );
        orderResponse.setPaymentMethod( order.getPaymentMethod() );
        orderResponse.setPaymentId( order.getPaymentId() );
        orderResponse.setShippingProvider( order.getShippingProvider() );
        orderResponse.setTrackingNumber( order.getTrackingNumber() );
        orderResponse.setShippingAddress( order.getShippingAddress() );
        orderResponse.setShippingCity( order.getShippingCity() );
        orderResponse.setShippingCountry( order.getShippingCountry() );
        orderResponse.setCouponCode( order.getCouponCode() );
        orderResponse.setNotes( order.getNotes() );
        orderResponse.setCreatedAt( order.getCreatedAt() );
        orderResponse.setUpdatedAt( order.getUpdatedAt() );

        orderResponse.setBuyerName( order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName() );

        return orderResponse;
    }

    @Override
    public OrderDTOs.OrderItemResponse toItemResponse(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        OrderDTOs.OrderItemResponse orderItemResponse = new OrderDTOs.OrderItemResponse();

        orderItemResponse.setProductId( itemProductId( item ) );
        orderItemResponse.setId( item.getId() );
        orderItemResponse.setProductTitle( item.getProductTitle() );
        orderItemResponse.setProductImage( item.getProductImage() );
        orderItemResponse.setUnitPrice( item.getUnitPrice() );
        orderItemResponse.setQuantity( item.getQuantity() );
        orderItemResponse.setSellerId( item.getSellerId() );
        orderItemResponse.setSellerName( item.getSellerName() );

        orderItemResponse.setSubtotal( item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())) );

        return orderItemResponse;
    }

    private Long orderBuyerId(Order order) {
        if ( order == null ) {
            return null;
        }
        User buyer = order.getBuyer();
        if ( buyer == null ) {
            return null;
        }
        Long id = buyer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<OrderDTOs.OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderDTOs.OrderItemResponse> list1 = new ArrayList<OrderDTOs.OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toItemResponse( orderItem ) );
        }

        return list1;
    }

    private Long itemProductId(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Product product = orderItem.getProduct();
        if ( product == null ) {
            return null;
        }
        Long id = product.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
