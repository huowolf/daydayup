package com.example.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon_template", indexes = {
        @Index(name = "idx_shop_id", columnList = "shop_id")
})
public class CouponTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "available", nullable = false)
    private Boolean available = false;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "description", nullable = false, length = 256)
    private String description;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "shop_id")
    private Long shopId;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private Date createdTime;

    @Column(name = "rule", nullable = false, length = 2000)
    private String rule;

}