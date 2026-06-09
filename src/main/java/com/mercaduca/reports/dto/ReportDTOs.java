package com.mercaduca.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReportDTOs {

    @Data
    @Builder
    public static class DashboardReport {
        private long totalUsers;
        private long totalSellers;
        private long totalProducts;
        private long totalOrders;
        private long pendingOrders;
        private BigDecimal totalRevenue;
        private List<TopProductReport> topSellingProducts;
        private List<TopProductReport> topViewedProducts;
        private List<CategorySalesReport> salesByCategory;
    }

    @Data
    @Builder
    public static class TopProductReport {
        private Long productId;
        private String productTitle;
        private Long sellerId;
        private String sellerName;
        private Integer totalSold;
        private Long totalViews;
        private Double averageRating;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class SellerReport {
        private Long sellerId;
        private String storeName;
        private BigDecimal totalRevenue;
        private Long totalOrders;
        private Integer activeProducts;
        private Integer totalProductsSold;
        private Double averageRating;
        private Integer totalReviews;
        private Long totalViews;
        private List<TopProductReport> topProducts;
        private List<RecentOrderSummary> recentOrders;
    }

    @Data
    @Builder
    public static class RecentOrderSummary {
        private Long orderId;
        private String orderNumber;
        private String buyerName;
        private BigDecimal total;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class CategorySalesReport {
        private Long categoryId;
        private String categoryName;
        private Integer totalSold;
        private BigDecimal totalRevenue;
    }
}
