package com.ielia.test.services;

import com.ielia.test.dtos.FooDTO;
import org.springframework.stereotype.Component;

@Component
public interface FooService {
    FooDTO getFoo();
    boolean checkFoo(FooDTO foo);
}
