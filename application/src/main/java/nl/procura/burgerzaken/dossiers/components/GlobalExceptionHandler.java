/*
 * Copyright 2021 - 2022 Procura B.V.
 *
 * In licentie gegeven krachtens de EUPL, versie 1.2
 * U mag dit werk niet gebruiken, behalve onder de voorwaarden van de licentie.
 * U kunt een kopie van de licentie vinden op:
 *
 *   https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md
 *
 * Deze bevat zowel de Nederlandse als de Engelse tekst
 *
 * Tenzij dit op grond van toepasselijk recht vereist is of schriftelijk
 * is overeengekomen, wordt software krachtens deze licentie verspreid
 * "zoals deze is", ZONDER ENIGE GARANTIES OF VOORWAARDEN, noch expliciet
 * noch impliciet.
 * Zie de licentie voor de specifieke bepalingen voor toestemmingen en
 * beperkingen op grond van de licentie.
 */

package nl.procura.burgerzaken.dossiers.components;

import static nl.procura.burgerzaken.dossiers.model.error.ApiErrorType.INTERNAL_ERROR;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.api.external.v1.error.ApiError;
import nl.procura.burgerzaken.dossiers.api.external.v1.error.ApiErrorCause;
import nl.procura.burgerzaken.dossiers.api.external.v1.error.ApiErrorHttpStatus;
import nl.procura.burgerzaken.dossiers.api.external.v1.error.ApiErrorRequest;
import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.model.error.ErrorLog;
import nl.procura.burgerzaken.dossiers.service.LoggingService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  final ObjectMapper   objectMapper;
  final LoggingService loggingService;

  public GlobalExceptionHandler(ObjectMapper objectMapper, LoggingService loggingService) {
    this.objectMapper = objectMapper;
    this.loggingService = loggingService;
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
    return logAndApiErrorResponse(ex, createApiError(request, INTERNAL_ERROR, ex));
  }

  @ResponseBody
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex,
      WebRequest request) {
    return logAndApiErrorResponse(ex, createApiError(request, INTERNAL_ERROR, ex)
        .setCause(getConstraintViolationCause(ex)));
  }

  private ApiErrorCause getConstraintViolationCause(ConstraintViolationException ex) {
    return ex.getConstraintViolations().stream().findFirst()
        .map(error -> {
          String fieldName = (MessageFormat.format("{0}.{1}",
              error.getRootBeanClass().getSimpleName(),
              error.getPropertyPath())).toLowerCase();
          return new ApiErrorCause(MessageFormat.format(error.getMessage(), fieldName));
        }).orElse(null);
  }

  @ResponseBody
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiError> handleApiException(ApiException ex, WebRequest request) {
    return logAndApiErrorResponse(ex, createApiError(request, ex.getError(), ex));
  }

  /**
   * Overriding the default spring boot handling.
   * We also want to log the errors that spring boot already handles, like bad request errors
   */
  @NonNull
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      @NonNull Exception ex,
      @Nullable Object body,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatus status,
      @NonNull WebRequest request) {
    ApiError apiError = createApiError(request, INTERNAL_ERROR, status, ex);
    handleMethodArgumentNotValidException(ex, apiError);
    loggingService.logException(getInternalError(apiError), ex);
    return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getRequest().getHttpStatus().getCode()));
  }

  /**
   * Overrides the handling of incorrect calls to operations
   */
  private void handleMethodArgumentNotValidException(Exception ex, ApiError apiError) {
    if (ex instanceof MethodArgumentNotValidException) {
      MethodArgumentNotValidException mex = (MethodArgumentNotValidException) ex;
      apiError.setCause(mex.getBindingResult().getFieldErrors().stream().findFirst()
          .map(error -> new ApiErrorCause(error.getDefaultMessage())).orElse(null));
    }
  }

  private ResponseEntity<ApiError> logAndApiErrorResponse(Exception ex, ApiError apiError) {
    loggingService.logException(getInternalError(apiError), ex);
    return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getRequest().getHttpStatus().getCode()));
  }

  private ApiError createApiError(WebRequest request, ApiErrorType error, Exception exception) {
    return createApiError(request, error, error.getHttpStatus(), exception);
  }

  private ApiError createApiError(WebRequest request,
      ApiErrorType error,
      HttpStatus httpStatus,
      Exception exception) {

    String requestId = loggingService.getRequestVariables().get().getRequestId();
    ApiError apiError = new ApiError();
    apiError.setCode(error.getCode());
    apiError.setDescription(error.getDescription());
    apiError.setTimestamp(LocalDateTime.now());
    apiError.setRequest(getApiErrorRequest(request, requestId, httpStatus));
    apiError.setCause(getCause(exception));
    return apiError;
  }

  private ApiErrorRequest getApiErrorRequest(WebRequest request, String requestId,
      HttpStatus httpStatus) {

    ApiErrorHttpStatus apiErrorHttpStatus = new ApiErrorHttpStatus();
    apiErrorHttpStatus.setCode(httpStatus.value());
    apiErrorHttpStatus.setDescription(httpStatus.getReasonPhrase());

    ApiErrorRequest errorRequest = new ApiErrorRequest();
    errorRequest.setHttpStatus(apiErrorHttpStatus);
    errorRequest.setId(requestId);
    errorRequest.setDetails(request.getDescription(false));
    return errorRequest;
  }

  private ApiErrorCause getCause(Throwable exception) {
    ApiErrorCause errorCause = new ApiErrorCause(exception.getMessage());
    if (exception.getCause() != null) {
      errorCause.setCause(getCause(exception.getCause()));
    }
    return errorCause;
  }

  private ErrorLog getInternalError(ApiError error) {
    ErrorLog errorLog = new ErrorLog();
    errorLog.setTimestamp(error.getTimestamp());
    errorLog.setErrorId(error.getId());
    errorLog.setErrorCode(error.getCode());
    errorLog.setErrorDescr(error.getDescription());
    errorLog.setCauses(getCause(error));
    ApiErrorRequest request = error.getRequest();

    if (request != null) {
      errorLog.setRequestId(request.getId());
      errorLog.setRequestDetails(request.getDetails());

      if (request.getHttpStatus() != null) {
        errorLog.setRequestHttpStatusCode(request.getHttpStatus().getCode());
        errorLog.setRequestHttpStatusDescr(request.getHttpStatus().getDescription());
      }
    }

    return errorLog;
  }

  private String getCause(ApiError error) {
    String out = "";
    if (error.getCause() != null) {
      try {
        out = objectMapper.writeValueAsString(error.getCause());
      } catch (JsonProcessingException e) {
        logger.error(e.getMessage(), e);
        out = "{\"message\":\"JsonProcessingException check log\"}";
      }
    }
    return out;
  }
}
