package org.openmrs.module.visits.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class JsonUtil {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json",
            StandardCharsets.UTF_8);

    public static String json(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private JsonUtil() {
    }
}
