package com.ielia.test.services;

import com.ielia.test.dtos.BarDTO;
import com.ielia.test.dtos.FooDTO;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
// import javax.validation.Validation;
// import javax.validation.Validator;
import java.util.Arrays;
import java.util.Collections;

@Service
public class FooServiceImpl implements FooService {
    // protected Validator validator;

    public FooServiceImpl() {
        // validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public FooDTO getFoo() {
        return new FooDTO(
                true,
                false,
                1,
                2,
                "abc",
                "def",
                Arrays.asList(
                        new BarDTO(0.1, 0.2, "a", "b", "c"),
                        new BarDTO(1.1, 1.2, "d", "e", "f")
                ),
                Arrays.asList(
                        new BarDTO(2.1, 2.2, "g", "h", "i"),
                        new BarDTO(3.1, 3.2, "j", "k", "l")
                ),
                Collections.singletonMap("x", new BarDTO(4.1, 4.2, "m", "n", "o")),
                Collections.singletonMap("y", "lala")
        );
    }

    @Override
    public boolean checkFoo(@Valid FooDTO foo) {
        // return validator.validate(foo).size() == 0;
        return false;
    }
}
