package com.example.validator;

import com.example.validator.dto.PersonRequest;
import com.example.validator.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

@SpringBootTest
public class PersonServiceTest {
    @Autowired
    private PersonService service;

    @Test
    public void should_throw_exception_when_person_request_is_not_valid() {
        try {
            PersonRequest personRequest = PersonRequest.builder().sex("Man22")
                    .classId("82938390").build();
            service.validatePersonRequest(personRequest);
        } catch (ConstraintViolationException e) {
            // 输出异常信息
            e.getConstraintViolations().forEach(constraintViolation -> System.out.println(constraintViolation.getMessage()));
        }
    }
}
