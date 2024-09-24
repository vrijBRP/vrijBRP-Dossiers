/*
 * Copyright 2023 - 2024 Procura B.V.
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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.procura.burgerzaken.dossiers.api.external.v1.dataimport.ApiDataImport;
import nl.procura.burgerzaken.dossiers.model.dataimport.DataImport;
import nl.procura.burgerzaken.dossiers.service.dataimport.DataImportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dataimport",
    description = "Action related to importing data")
@RestController
@RequestMapping("/api/v1/dataimport")
public class DataImportResourceV1 {

  private final DataImportService service;

  public DataImportResourceV1(DataImportService dataImportService) {
    this.service = dataImportService;
  }

  @Operation(
      operationId = "addDataimport",
      summary = "Import data",
      description = "Import data")
  @PostMapping(
      value = "/add",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiDataImport> addDataImport(@Valid @RequestBody ApiDataImport apiDataImport) {
    DataImport dataImport = service.add(apiDataImport.createNew());
    return new ResponseEntity<>(ApiDataImport.of(dataImport), HttpStatus.CREATED);
  }
}
