package com.mercaduca.disputes.entity;
import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.orders.entity.Order;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "disputes")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Dispute extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "buyer_id", nullable = false) private User buyer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_admin_id") private User assignedAdmin;
    @Column(nullable = false, length = 100) private String reason;
    @Column(columnDefinition = "TEXT", nullable = false) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, columnDefinition = "varchar(30)") @Builder.Default private DisputeStatus status = DisputeStatus.OPEN;
    @Column(name = "admin_notes", columnDefinition = "TEXT") private String adminNotes;
    @Column(name = "resolution", columnDefinition = "TEXT") private String resolution;
    @Column(name = "seller_response", columnDefinition = "TEXT") private String sellerResponse;
    @Column(name = "seller_proposed_solution", columnDefinition = "TEXT") private String sellerProposedSolution;
    public enum DisputeStatus { OPEN, UNDER_REVIEW, RESOLVED_BUYER, RESOLVED_SELLER, CLOSED, ARCHIVED }
}
