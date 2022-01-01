package com.example.jpa;


import com.example.jpa.dao.CouponTemplateRepository;
import com.example.jpa.entity.CouponTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class JpaDemoApplicationTests {

    @Autowired
    private CouponTemplateRepository couponTemplateRepository;
    
    @Test
    public void saveCouponTemplateTest(){
        for (int i = 1; i <= 30; i++) {
            CouponTemplate couponTemplate = new CouponTemplate();
            couponTemplate.setName("coupon"+i);
            couponTemplate.setDescription("卡券模板描述"+i);
            couponTemplate.setType(1+"");
            couponTemplate.setShopId(100L);
            couponTemplate.setRule("{}");
            couponTemplateRepository.save(couponTemplate);
        }
    }

    @Test
    void findAllByShopIdTest() {
        List<CouponTemplate> couponTemplateList = couponTemplateRepository.findAllByShopId(111L);
        System.out.println(couponTemplateList);
    }

    @Test
    void findAllByIdInTest(){
        Pageable page = PageRequest.of(0,20);
        Page<CouponTemplate> couponTemplatePage = couponTemplateRepository.findAllByIdIn(Arrays.asList(30, 40), page);
        System.out.println(couponTemplatePage.toList());
    }

    @Test
    void countByShopIdAndAvailableTest(){
        Integer count = couponTemplateRepository.countByShopIdAndAvailable(100L, false);
        System.out.println(count);
    }


    @Test
    void makeCouponUnavailableTest(){
        int result = couponTemplateRepository.makeCouponUnavailable(50);
        System.out.println(result);
    }

    @Test
    void exampleQueryTest(){
        CouponTemplate couponTemplate = CouponTemplate.builder().name("coupon20").build();
        List<CouponTemplate> couponTemplateList = couponTemplateRepository.findAll(Example.of(couponTemplate));
        System.out.println(couponTemplateList);
    }
}
