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

package nl.procura.burgerzaken.dossiers.api.external.v1.resources;

import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_2;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.error.ApiError;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.ApiTaskStatus;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.ApiTaskType;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.search.ApiSearchRequest;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.search.ApiSearchResponse;
import nl.procura.burgerzaken.dossiers.service.task.TaskStatus;
import nl.procura.burgerzaken.dossiers.service.task.TaskType;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

@ContextConfiguration(initializers = GbaSource.class)
class TaskResourceV1Test extends BaseResourceTest {

  private static final String BSN_DECLARANT     = BsnUtils.toBsnString(TEST_BSN_2);
  private static final String BSN_MAIN_OCCUPANT = BsnUtils.toBsnString(TEST_BSN_3);
  private static final String URI_SEARCH_TASK   = "/api/v1/tasks/search";

  @Test
  void mustReturnPlannedTasksByMainOccuptantBsn() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("task-dossiers.json"));
    GbaSource.enqueueJsonResponse(getClass().getResource("task-intra-relocation.json"));
    ApiSearchRequest searchRequest = ApiSearchRequest
        .builder()
        .bsns(Collections.singletonList(BSN_MAIN_OCCUPANT))
        .types(Collections.singletonList(ApiTaskType.builder()
            .code(TaskType.RELOCATION_LODGING_CONSENT.getCode())
            .build()))
        .statusses(Collections.singletonList(ApiTaskStatus.builder()
            .code(TaskStatus.PLANNED.getCode())
            .build()))
        .build();

    ApiSearchResponse response = newMockTest()
        .post(URI_SEARCH_TASK, searchRequest)
        .documentation("search_task1")
        .status(status().isOk())
        .toClass(ApiSearchResponse.class);

    assertEquals(1L, response.getResult().getTotalElements());
  }

  @Test
  void mustNotReturnPlannedTasksByDeclarantBsn() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("task-dossiers.json"));
    GbaSource.enqueueJsonResponse(getClass().getResource("task-intra-relocation.json"));
    ApiSearchRequest searchRequest = ApiSearchRequest
        .builder()
        .bsns(Collections.singletonList(BSN_DECLARANT))
        .types(Collections.singletonList(ApiTaskType.builder()
            .code(TaskType.RELOCATION_LODGING_CONSENT.getCode())
            .build()))
        .statusses(Collections.singletonList(ApiTaskStatus.builder()
            .code(TaskStatus.PLANNED.getCode())
            .build()))
        .build();

    ApiSearchResponse response = newMockTest()
        .post(URI_SEARCH_TASK, searchRequest)
        .status(status().isOk())
        .toClass(ApiSearchResponse.class);

    assertEquals(0L, response.getResult().getTotalElements());
  }

  @Test
  void mustNotReturnPlannedTasksByWrongStatus() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("task-dossiers.json"));
    GbaSource.enqueueJsonResponse(getClass().getResource("task-intra-relocation.json"));
    ApiSearchRequest searchRequest = ApiSearchRequest
        .builder()
        .bsns(Collections.singletonList(BSN_MAIN_OCCUPANT))
        .types(Collections.singletonList(ApiTaskType.builder()
            .code(TaskType.RELOCATION_LODGING_CONSENT.getCode())
            .build()))
        .statusses(Collections.singletonList(ApiTaskStatus.builder()
            .code(TaskStatus.DONE.getCode())
            .build()))
        .build();

    ApiSearchResponse response = newMockTest()
        .post(URI_SEARCH_TASK, searchRequest)
        .status(status().isOk())
        .toClass(ApiSearchResponse.class);

    assertEquals(0L, response.getResult().getTotalElements());
  }

  @Test
  void mustNotReturnPlannedTasksByUnknownType() throws Exception {
    ApiSearchRequest searchRequest = ApiSearchRequest
        .builder()
        .bsns(Collections.singletonList(BSN_MAIN_OCCUPANT))
        .types(Collections.singletonList(ApiTaskType.builder()
            .code("Bla")
            .build()))
        .statusses(Collections.singletonList(ApiTaskStatus.builder()
            .code(TaskStatus.DONE.getCode())
            .build()))
        .build();

    ApiError response = newMockTest()
        .post(URI_SEARCH_TASK, searchRequest)
        .status(status().isInternalServerError())
        .toClass(ApiError.class);

    assertEquals("internalError", response.getCode());
    assertEquals("No value with code Bla", response.getCause().getMessage());
  }
}
