package com.example.validator.service;

import com.example.validator.dto.PersonRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class PersonService {

    public void validatePersonRequest(@Valid PersonRequest personRequest) {
        // do something
    }

}
