package com.ielia.test.dtoinstrumentation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class SimpleTypeSerializer extends StdSerializer<Object> {
    public enum ModificationType {
        CHANGE_SIGN((value, gen) -> {
            try {
                if (value instanceof Integer || value instanceof Long || value instanceof AtomicInteger) {
                    gen.writeRawValue("" + -((Number) value).longValue());
                } else if (value instanceof BigInteger) {
                    gen.writeRawValue(BigInteger.ZERO.subtract((BigInteger) value).toString());
                } else if (value instanceof BigDecimal) {
                    gen.writeRawValue(BigDecimal.ZERO.subtract((BigDecimal) value).toString());
                } else {
                    gen.writeRawValue("" + -((Number) value).doubleValue());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }),
        CHANGE_TYPE((value, gen) -> {
            try {
                if (value instanceof String) {
                    gen.writeRawValue("123.456");
                } else {
                    gen.writeRawValue("\"abcdefghij\"");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }),
        EMPTY((value, gen) -> {
            try {
                if (value instanceof String) {
                    gen.writeRawValue("\"\"");
                } else if (value instanceof Map<?,?>) {
                    gen.writeStartObject();
                    gen.writeEndObject();
                } else {
                    gen.writeStartArray();
                    gen.writeEndArray();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }),
        NULLIFY((value, gen) -> {
            try {
                gen.writeRawValue("null");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }),
        // SUPPRESS(value -> null), // FIXME: Really suppress field
        ZERO((value, gen) -> {
            try {
                gen.writeRawValue("0");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        public final BiConsumer<Object, JsonGenerator> consumer;

        ModificationType(BiConsumer<Object, JsonGenerator> consumer) {
            this.consumer = consumer;
        }
    }

    public static final Map<ModificationType, Predicate<Object>> DEFAULT_CONFIG = Collections.unmodifiableMap(new EnumMap<>(ModificationType.class) {{
        put(ModificationType.CHANGE_SIGN, value -> value instanceof Number);
        put(ModificationType.CHANGE_TYPE, value -> true);
        put(ModificationType.EMPTY, value -> value instanceof String || value instanceof Collection<?> || value instanceof Map<?,?> || (value != null && value.getClass().isArray()));
        put(ModificationType.NULLIFY, value -> true);
        // put(ModificationType.SUPPRESS, value -> false); // FIXME: Make it work
        put(ModificationType.ZERO, value -> value instanceof Number);
    }});

    protected int currentMutationIndex;
    protected int targetIterationNumber;
    protected final Map<ModificationType, Predicate<Object>> config;
    private final Set<Map.Entry<ModificationType, Predicate<Object>>> modificationPredicates;

    protected SimpleTypeSerializer(int iterationNumber) {
        this(iterationNumber, DEFAULT_CONFIG);
    }

    protected SimpleTypeSerializer(int iterationNumber, Map<ModificationType, Predicate<Object>> config) {
        super(Object.class);
        currentMutationIndex = 0;
        targetIterationNumber = iterationNumber;
        this.config = Collections.unmodifiableMap(config); // Is this necessary?
        this.modificationPredicates = Collections.unmodifiableSet(config.entrySet());
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            boolean written = false;
            for (Map.Entry<ModificationType, Predicate<Object>> predicateEntry : this.modificationPredicates) {
                if (predicateEntry.getValue().test(value)) {
                    if (currentMutationIndex == targetIterationNumber) {
                        predicateEntry.getKey().consumer.accept(value, gen);
                        written = true;
                    }
                    ++currentMutationIndex;
                }
            }
            if (!written) {
                if (value instanceof Boolean || value instanceof Number) {
                    gen.writeRawValue(value.toString());
                } else if (value instanceof String) {
                    gen.writeRawValue("\"" + StringEscapeUtils.escapeJson((String) value) + "\"");
                } else if (value instanceof Collection<?>) {
                    gen.writeStartArray();
                    for (Object item : (Collection<?>) value) {
                        gen.writeObject(item);
                    }
                    gen.writeEndArray();
                } else if (value instanceof Map<?, ?>) {
                    gen.writeStartObject();
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                        // FIXME: If map key is not String, then this can get messed up
                        gen.writePOJOField(entry.getKey().toString(), entry.getValue().toString());
                    }
                    gen.writeEndObject();
                } else if (value.getClass().isArray()) {
                    gen.writeStartArray();
                    Object[] array = (Object[]) value;
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0, len = array.length; i < len; ++i) {
                        gen.writeObject(array[i]);
                    }
                    gen.writeEndArray();
                }
                // There should not be anything else. We should not have a non-standard object serialized with this.
            }
        }
    }

    public int getCurrentMutationIndex() {
        return currentMutationIndex;
    }

    public int incIterationNumber() {
        currentMutationIndex = 0;
        return ++targetIterationNumber;
    }
}
