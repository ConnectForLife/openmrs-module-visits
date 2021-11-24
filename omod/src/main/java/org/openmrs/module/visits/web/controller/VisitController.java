package org.openmrs.module.visits.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.HttpURLConnection;
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
@Api(
    value = "Visit Details",
    tags = {"REST API for managing Visit information(create,update and get details)"})
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
    @ApiOperation(
        value = "Get visit times",
        notes = "Get visit times",
        response = List.class
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "On successful retrieval of visit times"),
            @ApiResponse(
                code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                message = "Failure to get visit times")
        }
    )
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
    @ApiOperation(
        value = "Get visit statuses",
        notes = "Get visit statuses",
        response = List.class
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "On successful retrieval of visit statuses"),
            @ApiResponse(
                code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                message = "Failure to retrieve visit statuses")
        }
    )
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
    @ApiOperation(
        value = "Get visit details of a patient",
        notes = "Get visit details of a patient",
        response = VisitDetailsDTO.class
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "Successful retrieval of visit details of a patient"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_BAD_REQUEST,
                message = "Patient with the given uuid not found"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                message = "Failure to retrieve patient visits"
            )
        }
    )
    @RequestMapping(value = "/patient/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public PageDTO<VisitDetailsDTO> getVisitsForPatient(
        @ApiParam(name = "uuid", value="uuid", required = true )
        @PathVariable("uuid") String patientUuid, PageableParams pageableParams) {
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
    @ApiOperation(
        value = "Update details of a visit",
        notes = "Update details of a visit"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "Visit details successfully updated"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_BAD_REQUEST,
                message = "Error in visit details to be updated"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                message = "Visit details not updated"
            )
        }
    )
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateVisit(
        @ApiParam(name = "uuid", value = "uuid", required = true)
        @PathVariable("uuid") String visitUuid,
        @ApiParam(name = "visit", value = "visit", required = true)
        @RequestBody VisitDTO visit) {
        visitService.updateVisit(visitUuid, visit);
    }

    /**
     * Creates a visit
     *
     * @param visitDTO DTO object containing the visit data, must not contain uuid nor status
     */
    @ApiOperation(
        value = "Create a new visit",
        notes = "Create a new visit"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "Visit created successfully"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_BAD_REQUEST,
                message = "Error in visit details"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                message = "Visit not created"
            )
        }
    )
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createVisit(
        @ApiParam(name = "visitDTO", value = "Visit Dto", required = true)
        @RequestBody VisitDTO visitDTO) {
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
    @ApiOperation(
        value = "Get visit with uuid",
        notes = "Get visit with uuid",
        response = VisitDetailsDTO.class
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "Visit details fetched"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_BAD_REQUEST,
                message = "Visit not found"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                message = "Error in fetching visit details"
            )
        }
    )
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public VisitDetailsDTO getVisit(
        @ApiParam(name = "uuid", value = "uuid", required = true)
        @PathVariable("uuid") String visitUuid) {
        Visit visit = visitService.getByUuid(visitUuid);
        if (visit == null) {
            throw new IllegalArgumentException(String.format("Visit with the uuid (%s) doesn't exits", visitUuid));
        }
        return visitMapper.toDtoWithDetails(visit);
    }
}
