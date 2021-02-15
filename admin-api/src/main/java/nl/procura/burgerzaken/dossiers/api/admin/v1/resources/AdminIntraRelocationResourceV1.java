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

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.intra.AdminApiIntraMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.model.relocations.IntraMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.service.ClientService;
import nl.procura.burgerzaken.dossiers.service.IntraRelocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin API - Intra-municipal relocations",
    description = "Actions related to relocations of residents in the same municipality")
@RestController
@RequestMapping("/admin/api/v1/relocations/intra")
public class AdminIntraRelocationResourceV1 {

  private final IntraRelocationService service;
  private final ClientService          clientService;

  public AdminIntraRelocationResourceV1(IntraRelocationService service, ClientService clientService) {
    this.service = service;
    this.clientService = clientService;
  }

  @Operation(
      operationId = "findIntraMunicipalRelocation",
      summary = "Find one dossier",
      description = "Find an intra-municipal relocation based on a dossier-id")
  @GetMapping(
      value = "/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public AdminApiIntraMunicipalRelocation find(@PathVariable String dossierId) {
    return AdminApiIntraMunicipalRelocation.of(service.findByCaseNumber(dossierId));
  }

  @Operation(
      operationId = "addIntraMunicipalRelocation",
      summary = "Add a new dossier",
      description = "Add a new intra-municipal relocation")
  @PostMapping(
      value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<AdminApiIntraMunicipalRelocation> add(
      @Valid @RequestBody AdminApiIntraMunicipalRelocation relocation,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    IntraMunicipalRelocation newDossier = service.add(relocation.withNewId(clientService.getById(jwt.getSubject())));
    relocation.getDossier().setDossierId(newDossier.getDossier().getCaseNumber());
    return new ResponseEntity<>(relocation, HttpStatus.CREATED);
  }
}
