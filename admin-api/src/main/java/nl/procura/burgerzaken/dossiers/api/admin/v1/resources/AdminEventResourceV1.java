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

import static nl.procura.burgerzaken.dossiers.util.Constants.Errors.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiResultPage;
import nl.procura.burgerzaken.dossiers.api.admin.v1.events.AdminApiEventLog;
import nl.procura.burgerzaken.dossiers.model.events.EventLog;
import nl.procura.burgerzaken.dossiers.service.EventLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin API - Events",
    description = "Actions related to events")
@RestController
@RequestMapping("/admin/api/v1/events")
public class AdminEventResourceV1 {

  private final EventLogService service;

  public AdminEventResourceV1(EventLogService service) {
    this.service = service;
  }

  @Operation(
      operationId = "getEvents",
      summary = "Get events",
      description = "Get events ordered by id, use the last one to retrieve new events")
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resources successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<AdminApiResultPage<AdminApiEventLog>> getEvents(@Param("lastId") Long lastId,
      @Param("size") Integer size) {

    Page<EventLog> page = lastId == null ? service.findAll(size) : service.findGreatThan(lastId, size);
    List<AdminApiEventLog> events = new ArrayList<>();
    page.forEach(e -> events.add(AdminApiEventLog.of(e)));

    AdminApiResultPage<AdminApiEventLog> apiPage = AdminApiResultPage.<AdminApiEventLog> builder()
        .elements(page.getNumberOfElements())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageSize(page.getSize())
        .pageNumber(page.getNumber())
        .content(events)
        .build();

    return new ResponseEntity<>(apiPage, HttpStatus.OK);
  }

}
