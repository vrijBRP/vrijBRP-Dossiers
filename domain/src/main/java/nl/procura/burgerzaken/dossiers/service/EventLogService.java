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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;

import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.model.events.EventLog;
import nl.procura.burgerzaken.dossiers.model.events.EventType;
import nl.procura.burgerzaken.dossiers.model.events.QEventLog;
import nl.procura.burgerzaken.dossiers.repositories.events.EventLogRepository;

@Service
public class EventLogService {

  private static final int DEFAULT_SIZE = 10;

  private final EventLogRepository eventLog;
  private final ObjectMapper       mapper;

  public EventLogService(EventLogRepository eventLog, ObjectMapper mapper) {
    this.eventLog = eventLog;
    this.mapper = mapper;
  }

  public EventLog add(EventType type, String objectId, String clientId) {
    return eventLog.save(new EventLog(type, objectId, clientId));
  }

  public void add(EventType type, String objectId, String clientId, Object payload) {
    EventLog log = new EventLog(type, objectId, clientId);
    try {
      log.setPayload(mapper.writeValueAsString(payload));
    } catch (JsonProcessingException e) {
      throw new ApiException(ApiErrorType.INTERNAL_ERROR, e);
    }
    eventLog.save(log);
  }

  @Transactional
  public Page<EventLog> findGreatThan(Long lastId, Integer size) {
    BooleanExpression greaterThanId = QEventLog.eventLog.eventId.gt(lastId);
    return eventLog.findAll(greaterThanId, pageRequest(size));
  }

  @Transactional
  public Page<EventLog> findAll(Integer size) {
    return eventLog.findAll(pageRequest(size));
  }

  private static PageRequest pageRequest(Integer size) {
    String eventIdName = QEventLog.eventLog.eventId.getMetadata().getName();
    return PageRequest.of(0, getSize(size), Sort.by(Sort.Order.asc(eventIdName)));
  }

  private static int getSize(Integer size) {
    return size == null ? DEFAULT_SIZE : size;
  }

}
