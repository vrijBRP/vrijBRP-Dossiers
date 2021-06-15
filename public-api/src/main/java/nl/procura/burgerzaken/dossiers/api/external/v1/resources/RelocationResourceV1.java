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

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.info.relatives.ApiRelativesResponse;
import nl.procura.burgerzaken.dossiers.service.RelocationInfoService;
import nl.procura.burgerzaken.gba.numbers.Bsn;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Relocations",
    description = "Generic actions related to relocations")
@RestController
@RequestMapping("/api/v1/relocations")
public class RelocationResourceV1 {

  private RelocationInfoService service;

  public RelocationResourceV1(RelocationInfoService service) {
    this.service = service;
  }

  @Operation(
      operationId = "findRelocationRelatives",
      summary = "Get summary of relatives",
      description = "Find the relatives of registered person")
  @GetMapping(
      value = "/info/relatives/{bsn}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ApiRelativesResponse find(@Valid @PathVariable String bsn) {
    return ApiRelativesResponse.of(service.getRelativeInfo(new Bsn(bsn)));
  }
}
