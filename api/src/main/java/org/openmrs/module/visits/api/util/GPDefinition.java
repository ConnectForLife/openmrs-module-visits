package org.openmrs.module.visits.api.util;

public class GPDefinition {

    private final String key;
    private final String defaultValue;
    private final String description;

    public GPDefinition(String key, String defaultValue, String description) {
        this(key, defaultValue, description, false);
    }

    public GPDefinition(String key, String defaultValue, String description, boolean addDefaultValueToDesc) {
        this.key = key;
        this.defaultValue = defaultValue;
        if (addDefaultValueToDesc) {
            this.description = description + String.format(" Default value: '%s'", defaultValue);
        } else {
            this.description = description;
        }
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }
}
