package org.openmrs.module.visits.api.util;

import java.util.Map;

import static org.springframework.util.ObjectUtils.nullSafeToString;

public final class TemplateUtils {
    private static final String START_PARENTHESIS = "{{";
    private static final String END_PARENTHESIS = "}}";

    private TemplateUtils() {
    }

    public static String fillTemplate(String template, Map<String, String> params) {
        String result = template;
        for (Map.Entry<String, String> param : params.entrySet()) {
            result = fillTemplate(result, param.getKey(), param.getValue());
        }
        return result;
    }

    static String fillTemplate(String template, String param, String value) {
        return template.replace(START_PARENTHESIS + param + END_PARENTHESIS, nullSafeToString(value));
    }
}
