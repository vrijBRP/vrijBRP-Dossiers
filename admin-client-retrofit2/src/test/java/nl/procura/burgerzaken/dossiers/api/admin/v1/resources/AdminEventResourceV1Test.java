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

package nl.procura.burgerzaken.dossiers.api.admin.v1.resources;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nl.procura.burgerzaken.dossiers.api.admin.v1.client.AdminTestBase;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.api.AdminApiEventsApi;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.ResultPage;
import nl.procura.burgerzaken.dossiers.model.events.EventLog;
import nl.procura.burgerzaken.dossiers.model.events.EventType;
import nl.procura.burgerzaken.dossiers.repositories.events.EventLogRepository;
import nl.procura.burgerzaken.dossiers.service.EventLogService;

import retrofit2.Response;

class AdminEventResourceV1Test extends AdminTestBase {

  @Autowired
  private EventLogService service;

  @Autowired
  private EventLogRepository repository;

  private AdminApiEventsApi api;

  @BeforeEach
  public void setUp() {
    api = getApiClient().getApiClient().createService(AdminApiEventsApi.class);
    repository.deleteAll();
  }

  @Test
  void getEventsMustReturnAllEventsInCorrectOrder() throws IOException {
    // given generated events
    List<EventLog> eventLogs = givenEvents();
    // when
    Response<ResultPage> response = api.getEvents(null, null).execute();
    // then
    assertTrue(response.isSuccessful());
    ResultPage page = response.body();
    assertPage(3, 3, 1, 10, page);
    assertEvents(eventLogs, page.getContent());
  }

  @Test
  void getEventsWithLastIdMustReturnEventsGreaterThanLastId() throws IOException {
    List<EventLog> eventLogs = givenEvents();
    // when
    Response<ResultPage> response = api.getEvents(eventLogs.get(0).getEventId(), null).execute();
    // then
    assertTrue(response.isSuccessful());
    ResultPage page = response.body();
    assertPage(2, 2, 1, 10, page);
    assertEvents(eventLogs.subList(1, eventLogs.size()), page.getContent());
  }

  @Test
  void getEventsWithSizeMustReturnEventsWithPageSize() throws IOException {
    List<EventLog> eventLogs = givenEvents();
    // when
    Response<ResultPage> response = api.getEvents(null, 2).execute();
    // then
    assertTrue(response.isSuccessful());
    ResultPage page = response.body();
    assertPage(2, 3, 2, 2, page);
    assertEvents(eventLogs.subList(0, eventLogs.size() - 1), page.getContent());
  }

  @Test
  void payloadMustBeRetrievedAsJson() throws IOException {
    // given
    service.add(EventType.DOSSIER_UPDATED, "1", "client-id", singletonMap("status", "Created"));
    // when
    Response<ResultPage> response = api.getEvents(null, null).execute();
    // then
    assertTrue(response.isSuccessful());
    String payload = response.body().getContent().get(0).getPayload();
    assertEquals("{\"status\":\"Created\"}", payload);
  }

  private List<EventLog> givenEvents() {
    ArrayList<EventLog> events = new ArrayList<>();
    for (int i = 1; i <= 3; i++) {
      String objectId = String.valueOf(i);
      EventLog event = service.add(EventType.INTRA_MUNICIPAL_RELOCATION_CREATED, objectId, "client-id");
      events.add(event);
    }
    return events;
  }

  private static void assertPage(int elements, int totalElements, int totalPages, int pageSize,
      ResultPage actual) {
    assertEquals(elements, actual.getElements());
    assertEquals(totalElements, actual.getTotalElements());
    assertEquals(totalPages, actual.getTotalPages());
    assertEquals(0, actual.getPageNumber());
    assertEquals(pageSize, actual.getPageSize());
  }

  private static void assertEvents(List<EventLog> expected,
      List<nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.EventLog> actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertEvent(expected.get(i), actual.get(i));
    }
  }

  private static void assertEvent(EventLog expected,
      nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.EventLog actual) {
    assertEquals(expected.getObjectId(), actual.getObjectId());
    assertEquals(expected.getClientId(), actual.getClientId());
  }
}
