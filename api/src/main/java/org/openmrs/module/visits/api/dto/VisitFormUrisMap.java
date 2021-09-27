package org.openmrs.module.visits.api.dto;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Object containing a visit form URIs map parsed from JSON (eg. fetched from Global Property)
 * Additionally, a default URIs object is attached to the entity.
 */
public class VisitFormUrisMap {

    public static final String DEFAULT_VISIT_FORM_URIS_KEY = "default";

    private static final Log LOGGER = LogFactory.getLog(VisitFormUrisMap.class);

    private Map<String, VisitFormUris> urisMap;
    private VisitFormUris defaultUris;

    /**
     * Constructor of the visit form URIs map
     *
     * @param visitFormUrisJson a JSON object in key-value map structure passed as a text
     */
    public VisitFormUrisMap(String visitFormUrisJson) {
        urisMap = parseJson(visitFormUrisJson);
        defaultUris = urisMap.remove(DEFAULT_VISIT_FORM_URIS_KEY);
        if (defaultUris == null) {
            defaultUris = createEmptyUris();
        }
    }

    public String getUri(Visit visit) {
        VisitFormUris visitFormUris = getUris(visit);
        boolean isCreate = visit.getNonVoidedEncounters().isEmpty();
        return isCreate
                ? visitFormUris.getCreate(visit)
                : visitFormUris.getEdit(visit);
    }

    private VisitFormUris getUris(Visit visit) {
        String createTemplate = defaultUris.getCreateTemplate();
        String editTemplate = defaultUris.getEditTemplate();

        String visitTypeUuid = visit.getVisitType().getUuid();
        VisitFormUris uris = urisMap.get(visitTypeUuid) != null
                ? urisMap.get(visitTypeUuid)
                : urisMap.get(visit.getVisitType().getName());

        if (uris != null) {
            createTemplate = uris.getCreateTemplate() != null ? uris.getCreateTemplate() : createTemplate;
            editTemplate = uris.getEditTemplate() != null ? uris.getEditTemplate() : editTemplate;
        }
        return new VisitFormUris(createTemplate, editTemplate);
    }

    private Map<String, VisitFormUris> parseJson(String visitFormUrisJson) {
        Type mapType = new TypeToken<Map<String, VisitFormUris>>() { } .getType();
        Map<String, VisitFormUris> result = new HashMap<>();
        if (StringUtils.isNotBlank(visitFormUrisJson)) {
            try {
                result = new Gson().fromJson(visitFormUrisJson, mapType);
            } catch (JsonParseException ex) {
                LOGGER.warn("Cannot parse visit form URIs config. Expected JSON map value. " +
                        "Please check the system configuration.", ex);
            }
        }
        return result;
    }

    private VisitFormUris createEmptyUris() {
        return new VisitFormUris();
    }
}
