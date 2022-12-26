package com.ielia.test;

import com.ielia.test.dtoinstrumentation.DTOInstrumentator;
import com.ielia.test.dtos.BarDTO;
import com.ielia.test.dtos.FooDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public class AppTest extends AbstractTestNGSpringContextTests {
    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMVC;

    @BeforeClass
    public void setup() {
        mockMVC = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    public FooDTO buildFoo() {
        BarDTO barDTO1 = new BarDTO(0.1, 0.2, "Bye World", "a", null);
        BarDTO barDTO2 = new BarDTO(1.1, 1.2, "Hi World", "b", "c");
        BarDTO barDTO3 = new BarDTO(2.1, 2.2, "Hello World", null, "d");
        BarDTO barDTO4 = new BarDTO(3.1, 3.2, "Hola Mundo", "e", "f");
        BarDTO barDTO5 = new BarDTO(4.1, 4.2, "Ciao Mondo", "g", "h");
        FooDTO fooDTO = new FooDTO();
        fooDTO.setBooleanField1(true);
        fooDTO.setBooleanField2(false);
        fooDTO.setIntField1(42);
        fooDTO.setIntField2(7);
        fooDTO.setStringField1("Hello World");
        fooDTO.setStringField2("Hi World");
        fooDTO.setBarDTOs1(Arrays.asList(barDTO1, barDTO2));
        fooDTO.setBarDTOs2(Arrays.asList(barDTO3, barDTO4));
        fooDTO.setStuff1(new HashMap<>() {{
            put("bool", false);
            put("int", 1);
            put("string", "abc");
            put("bar", barDTO5);
            put("foo", new FooDTO(false, true, 100, 200, "lala", "lolo", null, null, null, null));
        }});
        fooDTO.setStuff2(Collections.emptyMap());
        return fooDTO;
    }

    @DataProvider(name = "error-cases")
    public Iterator<Object[]> dataProvider() {
        return new DTOInstrumentator().getErrorCombinations(buildFoo()).map(s -> new Object[]{s}).iterator();
    }

    @Test
    public void testGet() throws Exception {
        mockMVC.perform(get("/foos/any")).andExpect(status().isOk());
    }

    @Test(dataProvider = "error-cases")
    public void testPost(String json) throws Exception {
        logger.info("SENT:                 {}", json);
        mockMVC.perform(
                post("/foos/check")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(400));
    }
}
