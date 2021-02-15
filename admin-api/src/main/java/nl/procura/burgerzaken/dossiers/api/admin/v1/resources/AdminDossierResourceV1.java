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
import static nl.procura.burgerzaken.dossiers.util.Constants.Errors.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.*;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiDossier;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.search.AdminApiDossierSearchRequest;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.search.AdminApiDossierSearchResponse;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.QDossier;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDatePeriod;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDateTimePeriod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin API - Dossiers",
    description = "Actions related to emigrations of registered residents")
@RestController
@RequestMapping("/admin/api/v1/dossiers")
public class AdminDossierResourceV1 {

  private final DossierService dossierService;

  public AdminDossierResourceV1(DossierService dossierService) {
    this.dossierService = dossierService;
  }

  @Operation(
      operationId = "searchDossiers",
      summary = "Search for dossiers",
      description = "Search for dossiers")
  @PostMapping(
      value = "/search",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ResponseEntity<AdminApiDossierSearchResponse> search(@RequestBody AdminApiDossierSearchRequest request) {

    DossierSearchRequest r = DossierSearchRequest.builder()
        .pageRequest(getPaging(AdminApiResultPaging.ofDefault(request.getPaging())))
        .startDatePeriod(getDatePeriod(request.getStartDatePeriod()))
        .entryDateTimePeriod(getDateTimePeriod(request.getEntryDateTimePeriod()))
        .bsns(toBsns(request.getBsns()))
        .statusses(request.getStatusses())
        .types(request.getTypes())
        .dossierIds(request.getDossierIds())
        .build();

    Page<Dossier> page = dossierService.find(r);
    List<AdminApiDossier> apiDossiers = new ArrayList<>();
    page.forEach(d -> apiDossiers.add(AdminApiDossier.of(d)));

    AdminApiResultPage<AdminApiDossier> apiPage = AdminApiResultPage.<AdminApiDossier> builder()
        .elements(page.getNumberOfElements())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageSize(page.getSize())
        .pageNumber(page.getNumber())
        .content(apiDossiers)
        .build();

    return new ResponseEntity<>(AdminApiDossierSearchResponse.builder()
        .result(apiPage).build(), HttpStatus.OK);
  }

  private PageRequest getPaging(AdminApiResultPaging paging) {
    String dateTimeAdded = QDossier.dossier.dateAdded.getMetadata().getName();
    String dateStart = QDossier.dossier.dateStart.getMetadata().getName();
    Sort.Order defaultOrder = new Sort.Order(Sort.Direction.DESC, dateTimeAdded);

    List<Sort.Order> orders = new ArrayList<>();
    List<AdminApiResultSortType> sortTypes = paging.getSort();
    if (sortTypes == null) {
      orders.add(defaultOrder);
    } else {
      for (AdminApiResultSortType sortType : sortTypes) {
        switch (sortType) {
          case ENTRY_DATE_TIME_DESC:
            orders.add(defaultOrder);
            break;
          case ENTRY_DATE_TIME_ASC:
            orders.add(new Sort.Order(Sort.Direction.ASC, dateTimeAdded));
            break;
          case START_DATE_DESC:
            orders.add(new Sort.Order(Sort.Direction.DESC, dateStart));
            break;
          case START_DATE_ASC:
            orders.add(new Sort.Order(Sort.Direction.ASC, dateStart));
            break;
        }
      }
    }

    return PageRequest.of(paging.getPageNumber(), paging.getPageSize(), Sort.by(orders));
  }

  @Operation(
      operationId = "deleteDossier",
      summary = "Delete a dossier",
      description = "Delete a dossier by dossier-id")
  @DeleteMapping(
      value = "/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully deleted"),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ResponseEntity<Void> deleteDossier(@PathVariable String dossierId,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    dossierService.delete(jwt.getSubject(), dossierId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private static LocalDatePeriod getDatePeriod(AdminApiDatePeriod period) {
    return period == null ? null : period.toLocalDatePeriod();
  }

  private static LocalDateTimePeriod getDateTimePeriod(AdminApiDateTimePeriod period) {
    return period == null ? null : period.toLocalDateTimePeriod();
  }

  private List<Long> toBsns(List<String> bsns) {
    return bsns == null ? null : bsns.stream().map(Long::parseLong).collect(Collectors.toList());
  }
}
