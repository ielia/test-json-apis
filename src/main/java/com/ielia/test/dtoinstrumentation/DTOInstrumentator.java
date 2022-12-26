package com.ielia.test.dtoinstrumentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class DTOInstrumentator {
    // public static final String SEPARATOR = "\\.";

    public DTOInstrumentator() {
    }

    public <T> Stream<String> getErrorCombinations(T dto) { // , String jsonPath, Predicate... predicates) {
        // TODO: Check dates
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        SimpleTypeSerializer serializer = new SimpleTypeSerializer(0);
        module.addSerializer(Boolean.class, serializer);
        module.addSerializer(Collection.class, serializer);
        module.addSerializer(Map.class, serializer);
        module.addSerializer(Number.class, serializer);
        module.addSerializer(String.class, serializer);
        mapper.registerModule(module);
        try {
            String json = mapper.writeValueAsString(dto);
            return Stream.iterate(
                json,
                x -> serializer.getCurrentMutationIndex() > serializer.incIterationNumber(),
                x -> {
                    try {
                        return mapper.writeValueAsString(dto);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error while serializing DTO into JSON", exception);
        }
        // JsonNode root = mapper.valueToTree(dto);
        // return Stream.of(root);
        // return Stream.of(JsonPath.parse(root).read(jsonPath, predicates));
    }

    /*
    public static <T> T setFieldByFullPath(T dto, String fullPath, Object value) {
        Object current = dto;
        Stack<String> path = (Stack<String>) Arrays.asList(fullPath.split(SEPARATOR));
        String last = path.pop();
        for (String fieldName : path) {
            if ()
            try {
                if (current instanceof )
                current.getClass().getDeclaredMethod(getter(fieldName));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        if ()
        current = value;
        return dto;
        throw new RuntimeException("Not implemented yet.");
    }

    protected static String getter(String field) {
        return "get" + fieldSuffix(field);
    }

    protected static String setter(String field) {
        return "set" + fieldSuffix(field);
    }

    protected static String fieldSuffix(String field) {
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }
    */
}
