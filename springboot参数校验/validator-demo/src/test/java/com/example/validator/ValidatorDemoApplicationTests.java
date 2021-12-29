package com.example.validator;

import com.example.validator.dto.PersonRequest;
import com.example.validator.validator.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@SpringBootTest
class ValidatorDemoApplicationTests {

    @Autowired
    Validator validator;

    @Region
    private String region;

    @Test
    void validator1() {
        PersonRequest personRequest = PersonRequest.builder().sex("Man22")
                .classId("82938390").build();
        Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);
        // 输出异常信息
        violations.forEach(constraintViolation -> System.out.println(constraintViolation.getMessage()));
    }

    @Test
    void validator2() {
    }
}
