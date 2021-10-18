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

package nl.procura.burgerzaken.dossiers.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.model.error.ErrorLog;
import nl.procura.burgerzaken.dossiers.model.request.RequestLog;
import nl.procura.burgerzaken.dossiers.model.request.RequestVariables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoggingService {

  private static ThreadLocal<RequestVariables> requestVariables = new ThreadLocal<>();

  private ObjectMapper objectMapper;

  public LoggingService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public ThreadLocal<RequestVariables> getRequestVariables() {
    if (requestVariables.get() == null) {
      requestVariables.set(new RequestVariables());
    }
    return requestVariables;
  }

  public void logRequest(RequestLog logMessage) {
    try {
      String request = logMessage.getRequest();
      String response = logMessage.getResponse();
      logMessage.setRequest(null);
      logMessage.setResponse(null);

      StringBuilder info = new StringBuilder();
      logMessage(logMessage, info);
      logRequest(request, info);
      logResponse(response, info);

      log.info(info.toString());
    } catch (JsonProcessingException e) {
      log.info(logMessage.toString());
    }
  }

  private void logMessage(RequestLog logMessage, StringBuilder info) throws JsonProcessingException {
    info.append("\n\n");
    info.append("NEW MESSAGE\n");
    info.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logMessage));
    info.append("\n\n");
  }

  private void logRequest(String request, StringBuilder info) throws JsonProcessingException {
    if (StringUtils.isNotBlank(request)) {
      info.append("REQUEST\n");
      info.append(prettify(request));
    } else {
      info.append("EMPTY REQUEST");
    }
    info.append("\n\n");
  }

  private void logResponse(String response, StringBuilder info) throws JsonProcessingException {
    if (StringUtils.isNotBlank(response)) {
      info.append("RESPONSE\n");
      info.append(prettify(response));
    } else {
      info.append("EMPTY RESPONSE");
    }
    info.append("\n\n");
  }

  private String prettify(String request) throws JsonProcessingException {
    if (StringUtils.isNotBlank(request)) {
      return objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(objectMapper.readValue(request, Object.class));
    }
    return "";
  }

  public void logException(ErrorLog error, Exception exception) {
    log.info("ErrorMessage :: " + error.toString());
    log.info("Exception :: " + exception.toString(), exception);
  }
}
