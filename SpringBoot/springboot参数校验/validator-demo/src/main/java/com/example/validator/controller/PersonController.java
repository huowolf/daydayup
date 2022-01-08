package com.example.validator.controller;

import com.example.validator.dto.PersonRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/person")
@Validated
public class PersonController {

    @PostMapping
    public ResponseEntity<PersonRequest> save(@RequestBody @Valid PersonRequest personRequest) {
        return ResponseEntity.ok().body(personRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Integer> getPersonByID(@Valid @PathVariable("id") @Max(value = 5, message = "超过 id 的范围了") Integer id) {
        return ResponseEntity.ok().body(id);
    }

    @PutMapping
    public ResponseEntity<String> getPersonByName(@Valid @RequestParam("name") @Size(max = 6, message = "超过 name 的范围了") String name) {
        return ResponseEntity.ok().body(name);
    }
}
