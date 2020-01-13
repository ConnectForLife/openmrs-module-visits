package org.openmrs.module.visits.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.visits.api.dto.ErrorResponseDTO;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.api.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.openmrs.module.visits.api.model.ErrorMessageEnum.ERR_BAD_PARAM;
import static org.openmrs.module.visits.api.model.ErrorMessageEnum.ERR_SYSTEM;

/**
 * Base Rest Controller
 * All controllers in this module extend this for easy error handling
 */
public abstract class BaseRestController {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * Exception handler for bad request - Http status code of 400
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponseDTO handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage(), e);
        return new ErrorResponseDTO(new ErrorMessage(ERR_BAD_PARAM.getCode(), e.getMessage()));
    }

    /**
     * Exception handler for anything not covered above - Http status code of 500
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponseDTO handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return new ErrorResponseDTO(new ErrorMessage(ERR_SYSTEM.getCode(), e.getMessage()));
    }

    /**
     * Exception handler for bad request - Http status code of 400
     *
     * @param ex the exception throw
     * @return a error response
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponseDTO handleValidationException(ValidationException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getErrorResponse();
    }

    protected Log getLogger() {
        return logger;
    }
}
