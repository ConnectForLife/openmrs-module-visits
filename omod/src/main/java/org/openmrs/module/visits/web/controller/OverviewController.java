package org.openmrs.module.visits.web.controller;

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
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public PageDTO<OverviewDTO> getVisitsForLocation(@PathVariable("uuid") String locationUuid,
                                                     PageableParams pageableParams,
                                                     @RequestParam(required = false) String query,
                                                     @RequestParam(required = false) String visitStatus,
                                                     @RequestParam(required = false) Long dateFrom,
                                                     @RequestParam(required = false) Long dateTo,
                                                     @RequestParam(required = false) String timePeriod) {
        PagingInfo pagingInfo = pageableParams.getPagingInfo();
        List<Visit> visits = visitService.getVisitsForLocation(locationUuid, pagingInfo, query, visitStatus,
                dateFrom, dateTo, timePeriod);

        return new PageDTO<>(overviewMapper.toDtos(visits), pagingInfo);
    }
}
