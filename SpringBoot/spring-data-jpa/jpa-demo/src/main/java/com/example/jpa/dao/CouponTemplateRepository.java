package com.example.jpa.dao;

import com.example.jpa.entity.CouponTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Integer> {

    List<CouponTemplate> findAllByShopId(Long shopId);

    Page<CouponTemplate> findAllByIdIn(List<Integer> Id, Pageable page);

    Integer countByShopIdAndAvailable(Long shopId, Boolean available);

    //一般应该在service层添加@Transactional，这里添加只是为了不报错。
    @Transactional
    @Modifying
    @Query("update CouponTemplate c set c.available = 0 where c.id = :id")
    int makeCouponUnavailable(@Param("id") Integer id);
}