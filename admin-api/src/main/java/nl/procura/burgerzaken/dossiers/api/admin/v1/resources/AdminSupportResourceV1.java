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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.procura.burgerzaken.dossiers.api.admin.v1.support.AdminApiInfo;
import nl.procura.burgerzaken.dossiers.model.support.Version;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin API - Support",
    description = "Support actions for admin")
@RestController
@RequestMapping("/admin/api/v1/support")
public class AdminSupportResourceV1 {

  @Autowired
  Version version;

  @Operation(
      operationId = "getInfo",
      summary = "Return service information",
      description = "Return service information")
  @GetMapping(
      value = "/info",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
  })
  public ResponseEntity<AdminApiInfo> getInfo() {
    AdminApiInfo info = new AdminApiInfo();
    info.setVersion(version.version());
    info.setBuildTime(version.time());
    return new ResponseEntity<>(info, HttpStatus.OK);
  }
}
