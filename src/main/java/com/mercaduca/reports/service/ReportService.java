package com.mercaduca.reports.service;

import com.mercaduca.common.enums.OrderStatus;
import com.mercaduca.common.enums.ProductStatus;
import com.mercaduca.orders.repository.OrderRepository;
import com.mercaduca.products.entity.Product;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.reports.dto.ReportDTOs;
import com.mercaduca.users.repository.SellerProfileRepository;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Transactional(readOnly = true)
    public ReportDTOs.DashboardReport getDashboard() {
        List<ReportDTOs.TopProductReport> topSelling = productRepository
                .findTopSelling(PageRequest.of(0, 10)).stream().map(p ->
                    ReportDTOs.TopProductReport.builder()
                        .productId(p.getId()).productTitle(p.getTitle())
                        .sellerId(p.getSeller().getId())
                        .sellerName(p.getSeller().getFirstName() + " " + p.getSeller().getLastName())
                        .totalSold(p.getTotalSold()).totalViews(p.getViewCount())
                        .averageRating(p.getAverageRating()).build()).toList();

        List<ReportDTOs.TopProductReport> topViewed = productRepository
                .findTopViewed(PageRequest.of(0, 10)).stream().map(p ->
                    ReportDTOs.TopProductReport.builder()
                        .productId(p.getId()).productTitle(p.getTitle())
                        .totalViews(p.getViewCount()).totalSold(p.getTotalSold()).build()).toList();

        BigDecimal totalRevenue = orderRepository.getTotalPlatformRevenue();
        return ReportDTOs.DashboardReport.builder()
                .totalUsers(userRepository.count())
                .totalSellers(sellerProfileRepository.count())
                .totalProducts(productRepository.count())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .topSellingProducts(topSelling)
                .topViewedProducts(topViewed)
                .build();
    }

    @Transactional(readOnly = true)
    public ReportDTOs.SellerReport getSellerReport(Long sellerId) {
        BigDecimal revenue = orderRepository.getTotalRevenueForSeller(sellerId);
        long totalOrders = orderRepository.findOrdersBySellerId(sellerId, PageRequest.of(0, 1)).getTotalElements();

        List<Product> activeProducts = productRepository.findBySellerIdAndStatus(sellerId, ProductStatus.ACTIVE);
        int totalProductsSold = activeProducts.stream()
                .mapToInt(p -> p.getTotalSold() != null ? p.getTotalSold() : 0).sum();
        long totalViews = activeProducts.stream()
                .mapToLong(p -> p.getViewCount() != null ? p.getViewCount() : 0L).sum();

        double avgRating = sellerProfileRepository.findByUserId(sellerId)
                .map(sp -> sp.getAverageRating() != null ? sp.getAverageRating() : 0.0)
                .orElse(0.0);

        int totalReviews = sellerProfileRepository.findByUserId(sellerId)
                .map(sp -> sp.getTotalReviews() != null ? sp.getTotalReviews() : 0)
                .orElse(0);

        String storeName = sellerProfileRepository.findByUserId(sellerId)
                .map(sp -> sp.getStoreName()).orElse("");

        // Top 5 productos por ventas
        List<ReportDTOs.TopProductReport> topProducts = activeProducts.stream()
                .sorted((a, b) -> Integer.compare(
                        b.getTotalSold() != null ? b.getTotalSold() : 0,
                        a.getTotalSold() != null ? a.getTotalSold() : 0))
                .limit(5)
                .map(p -> {
                    BigDecimal productRevenue = p.getPrice() != null && p.getTotalSold() != null
                            ? p.getPrice().multiply(BigDecimal.valueOf(p.getTotalSold())) : BigDecimal.ZERO;
                    return ReportDTOs.TopProductReport.builder()
                            .productId(p.getId()).productTitle(p.getTitle())
                            .totalSold(p.getTotalSold() != null ? p.getTotalSold() : 0)
                            .totalViews(p.getViewCount() != null ? p.getViewCount() : 0L)
                            .averageRating(p.getAverageRating())
                            .revenue(productRevenue)
                            .build();
                }).toList();

        // Órdenes recientes (últimas 5)
        List<ReportDTOs.RecentOrderSummary> recentOrders = orderRepository
                .findOrdersBySellerId(sellerId, PageRequest.of(0, 5))
                .getContent().stream()
                .map(o -> ReportDTOs.RecentOrderSummary.builder()
                        .orderId(o.getId()).orderNumber(o.getOrderNumber())
                        .buyerName(o.getBuyer().getFirstName() + " " + o.getBuyer().getLastName())
                        .total(o.getTotal()).status(o.getStatus().name())
                        .createdAt(o.getCreatedAt()).build())
                .toList();

        return ReportDTOs.SellerReport.builder()
                .sellerId(sellerId).storeName(storeName)
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .totalOrders(totalOrders)
                .activeProducts(activeProducts.size())
                .totalProductsSold(totalProductsSold)
                .averageRating(avgRating)
                .totalReviews(totalReviews)
                .totalViews(totalViews)
                .topProducts(topProducts)
                .recentOrders(recentOrders)
                .build();
    }
}
