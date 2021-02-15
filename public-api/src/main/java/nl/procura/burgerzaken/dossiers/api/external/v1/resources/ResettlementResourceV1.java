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

import static nl.procura.burgerzaken.dossiers.util.Constants.Errors.BAD_REQUEST;
import static nl.procura.burgerzaken.dossiers.util.Constants.Errors.NOT_FOUND;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.resettlement.ApiResettlement;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Hidden
@RestController
@RequestMapping("/api/v1/relocations/resettlement")
@Tag(name = "Resettlements", description = "Actions related to resettlements of previous registered residents")
public class ResettlementResourceV1 {

  @Operation(
      operationId = "findResettlement",
      summary = "Find one dossier",
      description = "Find a resettlement based on a dossier-id")
  @GetMapping(
      value = "/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ApiResettlement find(@PathVariable String dossierId) {
    ApiResettlement relocation = ApiResettlement.builder().build();
    ApiDossier dossier = ApiDossier.builder().build();
    relocation.setDossier(dossier);
    return relocation;
  }

  @Operation(
      operationId = "addResettlement",
      summary = "Add a new dossier",
      description = "Add a new resettlement")
  @PostMapping(
      value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity add(@Valid @RequestBody ApiResettlement relocation) {
    return new ResponseEntity(relocation, HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateResettlement",
      summary = "Update one dossier",
      description = "Update a resettlement by dossier-id")
  @PutMapping(
      value = "/{dossierId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully updated"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ResponseEntity update(@Valid @PathVariable String dossierId, ApiResettlement relocation) {
    return new ResponseEntity(relocation, HttpStatus.OK);
  }

}
