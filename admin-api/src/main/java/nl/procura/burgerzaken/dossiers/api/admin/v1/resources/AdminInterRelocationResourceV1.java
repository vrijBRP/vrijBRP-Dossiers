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

import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.inter.AdminApiInterMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.model.relocations.InterMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.service.ClientService;
import nl.procura.burgerzaken.dossiers.service.InterRelocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin API - Inter-municipal relocations",
    description = "Actions related to relocations of residents from another municipality")
@RestController
@RequestMapping("/admin/api/v1/relocations/inter")
public class AdminInterRelocationResourceV1 {

  private final InterRelocationService service;
  private final ClientService          clientService;

  public AdminInterRelocationResourceV1(InterRelocationService service, ClientService clientService) {
    this.service = service;
    this.clientService = clientService;
  }

  @Operation(
      operationId = "findInterMunicipalRelocation",
      summary = "Find one dossier",
      description = "Find an inter-municipal relocation based on a dossier-id")
  @GetMapping(
      value = "/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public AdminApiInterMunicipalRelocation findInterMunicipalRelocation(@PathVariable String dossierId) {
    return AdminApiInterMunicipalRelocation.of(service.findByCaseNumber(dossierId));
  }

  @Operation(
      operationId = "addInterMunicipalRelocation",
      summary = "Add a new dossier",
      description = "Add a new inter-municipal relocation")
  @PostMapping(
      value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ResponseEntity<AdminApiInterMunicipalRelocation> addInterMunicipalRelocation(
      @Valid @RequestBody AdminApiInterMunicipalRelocation relocation,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    InterMunicipalRelocation newDossier = service.add(relocation.withNewId(clientService.getById(jwt.getSubject())));
    relocation.getDossier().setDossierId(newDossier.getDossier().getCaseNumber());
    return new ResponseEntity<>(relocation, HttpStatus.CREATED);
  }
}
