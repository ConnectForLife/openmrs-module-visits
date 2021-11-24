package org.openmrs.module.visits.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.HttpURLConnection;
import org.openmrs.Visit;
import org.openmrs.module.visits.api.dto.OverviewDTO;
import org.openmrs.module.visits.dto.PageDTO;
import org.openmrs.module.visits.api.mapper.OverviewMapper;
import org.openmrs.module.visits.api.service.VisitService;
import org.openmrs.module.visits.domain.PagingInfo;
import org.openmrs.module.visits.web.model.PageableParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Exposes the endpoints related to Visit Overview
 */
@Api(
    value = "Visit Overview",
    tags = {"REST API for visit overview"}
)
@Controller
@RequestMapping("/visits/overview")
public class OverviewController extends BaseRestController {

    @Autowired
    @Qualifier("visits.visitService")
    private VisitService visitService;

    @Autowired
    @Qualifier("visits.overviewMapper")
    private OverviewMapper overviewMapper;

    /**
     * Fetches the page of the visits for given location
     *
     * @param locationUuid uuid of the location
     * @param pageableParams parameters representing expected page shape
     * @param query optional query containing phrase filtering the visits by patient's identifier or name,
     * eg. phrase 'jo' may result in returning the visits for patient 'John'
     * @param visitStatus used for filtering visits by visit status
     * @param dateFrom used for filtering visits where planned date of visit is greater or equals than dateFrom
     * @param dateTo used for filtering visit where planned date of visit is less or equals than dateTo
     * @param timePeriod used for filtering visits depending on value from
     * {@link org.openmrs.module.visits.api.model.TimePeriod}. If value is not provided, default value = TODAY is used.
     *
     * @return a page containing visit overview details
     */
    @ApiOperation(
        value = "Get visits for location",
        notes = "Get visits for location",
        response = OverviewDTO.class
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "visits for location retrieved"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                message = "Visits for location not retrieved"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_NOT_FOUND,
                message = "Location not found"
            )
        }
    )
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public PageDTO<OverviewDTO> getVisitsForLocation(
        @ApiParam(name = "uuid", value = "uuid", required = true)
        @PathVariable("uuid") String locationUuid,
        PageableParams pageableParams,
        @ApiParam(name = "query", value = "query")
        @RequestParam(required = false) String query,
        @ApiParam(name = "visitStatus", value = "visitStatus")
        @RequestParam(required = false) String visitStatus,
        @ApiParam(name = "dateFrom", value = "dateFrom")
        @RequestParam(required = false) Long dateFrom,
        @ApiParam(name = "dateTo", value = "dateTo")
        @RequestParam(required = false) Long dateTo,
        @ApiParam(name = "timePeriod", value = "timePeriod")
        @RequestParam(required = false) String timePeriod) {

        PagingInfo pagingInfo = pageableParams.getPagingInfo();
        List<Visit> visits = visitService.getVisitsForLocation(locationUuid, pagingInfo, query, visitStatus,
                dateFrom, dateTo, timePeriod);

        return new PageDTO<>(overviewMapper.toDtos(visits), pagingInfo);
    }
}
