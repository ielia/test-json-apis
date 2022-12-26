package com.ielia.test.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ielia.test.dtos.FooDTO;
import com.ielia.test.services.FooService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/foos")
@RestController
@Validated
public class FooController {
    private static final Logger logger = LoggerFactory.getLogger(FooController.class);

    @Autowired
    protected FooService fooService;

    @GetMapping("/any")
    public ResponseEntity<FooDTO> getFoo() {
        return new ResponseEntity<>(fooService.getFoo(), HttpStatus.OK);
    }

    @PostMapping("/check")
    public ResponseEntity<Void> checkFoo(@Valid @RequestBody FooDTO foo) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        logger.info("RECEIVED: {}", mapper.writeValueAsString(foo));
        boolean response = fooService.checkFoo(foo);
        return new ResponseEntity<>(null, response ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
