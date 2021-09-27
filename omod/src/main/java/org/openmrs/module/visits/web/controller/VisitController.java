package org.openmrs.module.visits.web.controller;

import org.openmrs.Visit;
import org.openmrs.module.visits.dto.PageDTO;
import org.openmrs.module.visits.api.dto.VisitDTO;
import org.openmrs.module.visits.api.dto.VisitDetailsDTO;
import org.openmrs.module.visits.api.mapper.VisitMapper;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.web.model.PageableParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Exposes the endpoints related to managing the visits
 */
@Controller
@RequestMapping("/visits")
public class VisitController extends BaseRestController {

    @Autowired
    @Qualifier("visits.visitService")
    private VisitService visitService;

    @Autowired
    @Qualifier("visits.visitMapper")
    private VisitMapper visitMapper;

    @Autowired
    @Qualifier("visits.configService")
    private ConfigService configService;

    /**
     * Fetches the available visit times (eg. morning, afternoon, evening)
     *
     * @return list of string values of visit times
     */
    @RequestMapping(value = "/times", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVisitTimes() {
        return configService.getVisitTimes();
    }

    /**
     * Fetches the available visit statuses (eg. SCHEDULED)
     *
     * @return list of string values of visit times
     */
    @RequestMapping(value = "/statuses", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVisitStatuses() {
        return configService.getVisitStatuses();
    }

    /**
     * Fetches a page of the visit details related to a given patient
     *
     * @param patientUuid uuid of the patient
     * @param pageableParams parameters representing expected page shape
     * @return a page containing visit details
     */
    @RequestMapping(value = "/patient/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public PageDTO<VisitDetailsDTO> getVisitsForPatient(@PathVariable("uuid") String patientUuid,
                                                            PageableParams pageableParams) {
        PagingInfo pagingInfo = pageableParams.getPagingInfo();
        List<Visit> visits = visitService.getVisitsForPatient(patientUuid, pagingInfo);
        return new PageDTO<>(visitMapper.toDtosWithDetails(visits), pagingInfo);
    }

    /**
     * Updates a visit with the visit details
     *
     * @param visitUuid uuid representing the visit
     * @param visit DTO object containing the visit details
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateVisit(@PathVariable("uuid") String visitUuid, @RequestBody VisitDTO visit) {
        visitService.updateVisit(visitUuid, visit);
    }

    /**
     * Creates a visit
     *
     * @param visitDTO DTO object containing the visit data, must not contain uuid nor status
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createVisit(@RequestBody VisitDTO visitDTO) {
        if (visitDTO.getUuid() != null) {
            throw new IllegalArgumentException("New visit cannot already have a uuid");
        }

        if (visitDTO.getStatus() != null) {
            throw new IllegalArgumentException("Status cannot be set before creation. (Default one will be set)");
        }

        visitService.createVisit(visitDTO);
    }

    /**
     * Fetches a single visit
     *
     * @param visitUuid uuid of the visit
     * @return a DTO object containing the single visit details
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public VisitDetailsDTO getVisit(@PathVariable("uuid") String visitUuid) {
        Visit visit = visitService.getByUuid(visitUuid);
        if (visit == null) {
            throw new IllegalArgumentException(String.format("Visit with the uuid (%s) doesn't exits", visitUuid));
        }
        return visitMapper.toDtoWithDetails(visit);
    }
}
