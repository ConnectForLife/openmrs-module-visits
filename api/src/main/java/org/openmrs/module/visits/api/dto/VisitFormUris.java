package org.openmrs.module.visits.api.dto;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.module.visits.api.util.TemplateUtils;
import org.openmrs.module.visits.api.util.UriUtils;
import org.openmrs.module.visits.api.util.VisitsConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.visits.api.util.ConfigConstants.ENCOUNTER_UUID_PARAM;
import static org.openmrs.module.visits.api.util.ConfigConstants.PATIENT_UUID_PARAM;
import static org.openmrs.module.visits.api.util.ConfigConstants.VISIT_UUID_PARAM;

/**
 * Object containing a visit form URIs for creating and editing
 */
public class VisitFormUris {
    private static final Log LOGGER = LogFactory.getLog(VisitFormUris.class);
    private static final int ONE = 1;

    @SerializedName(value = VisitsConstants.CREATE_URI_NAME)
    private String createTemplate;

    @SerializedName(value = VisitsConstants.EDIT_URI_NAME)
    private String editTemplate;

    public VisitFormUris() {
    }

    /**
     * Constructor of the visit form uris object
     *
     * @param createTemplate URI of a template for creating a visit form
     * @param editTemplate URI of a template for editing a visit form
     */
    public VisitFormUris(String createTemplate, String editTemplate) {
        this.createTemplate = createTemplate;
        this.editTemplate = editTemplate;
    }

    public String getCreateTemplate() {
        return createTemplate;
    }

    public String getEditTemplate() {
        return editTemplate;
    }

    public String getCreate(Visit visit) {
        if (createTemplate == null) {
            return null;
        }
        String uri = fromTemplateToUri(visit, createTemplate);
        return validateUri(uri);
    }

    public String getEdit(Visit visit) {
        if (editTemplate == null) {
            return null;
        }
        String uri = fromTemplateToUri(visit, editTemplate);
        return validateUri(uri);
    }

    private String validateUri(String uri) {
        String result = uri;
        if (!UriUtils.isUriValid(result)) {
            LOGGER.warn(String.format(
                    "The URI '%s' is invalid (please check the system configuration). Empty URI will be used.", result));
            result = null;
        }
        return result;
    }

    private String fromTemplateToUri(Visit visit, String template) {
        return TemplateUtils.fillTemplate(template, prepareTemplateParams(visit));
    }

    private Map<String, String> prepareTemplateParams(Visit visit) {
        HashMap<String, String> params = new HashMap<>();
        params.put(PATIENT_UUID_PARAM, visit.getPatient().getUuid());
        params.put(VISIT_UUID_PARAM, visit.getUuid());

        Encounter encounter = getEncounter(visit);
        if (encounter != null) {
            params.put(ENCOUNTER_UUID_PARAM, encounter.getUuid());
        }
        return params;
    }

    private Encounter getEncounter(Visit visit) {
        List<Encounter> encounters = visit.getNonVoidedEncounters();
        Encounter encounter = null;
        if (!encounters.isEmpty()) {
            encounter = encounters.get(0);
        }
        if (encounters.size() > ONE) {
            LOGGER.warn(String.format(
                    "The CFL visits module assumes that a visit has at most 1 Encounter, but %d found for Visit ID: %d. " +
                            "Using Encounter ID: %d", encounters.size(), visit.getId(), encounter.getId()));
        }
        return encounter;
    }
}
