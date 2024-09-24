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
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.procura.burgerzaken.dossiers.api.external.v1.birth.ApiBirth;
import nl.procura.burgerzaken.dossiers.api.external.v1.birth.ApiUnbornAcknowledgement;
import nl.procura.burgerzaken.dossiers.api.external.v1.birth.info.ApiFamilySituationInfoResponse;
import nl.procura.burgerzaken.dossiers.api.external.v1.birth.info.ApiNameSelectionInfoResponse;
import nl.procura.burgerzaken.dossiers.model.birth.Birth;
import nl.procura.burgerzaken.dossiers.service.AcknowledgementService;
import nl.procura.burgerzaken.dossiers.service.BirthService;
import nl.procura.burgerzaken.dossiers.service.ClientService;
import nl.procura.burgerzaken.gba.numbers.Bsn;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Birth",
    description = "Actions related to births")
@RestController
@RequestMapping("/api/v1/births")
@RequiredArgsConstructor
public class BirthResourceV1 {

  private final BirthService birthService;
  private final ClientService          clientService;
  private final AcknowledgementService acknowledgementService;

  @Operation(
      operationId = "findBirth",
      summary = "Find one dossier",
      description = "Find an birth dossier based on a dossier-id")
  @GetMapping(
      value = "/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ApiBirth find(@PathVariable String dossierId) {
    return ApiBirth.of(birthService.findByCaseNumber(dossierId));
  }

  @Operation(
      operationId = "addBirth",
      summary = "Add a new dossier",
      description = "Add a new birth dossier")
  @PostMapping(
      value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiBirth> add(
      @Valid @RequestBody ApiBirth birth,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    Birth newDossier = birthService.add(birth.createNew(clientService.getById(jwt.getSubject())));
    return new ResponseEntity<>(ApiBirth.of(newDossier), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "getNameSelection",
      summary = "Get information about joint children",
      description = "Get information about joint children for nameselection")
  @Parameters({
      @Parameter(name = "bsnMother", description = "BSN of the mother"),
      @Parameter(name = "bsnFatherOrDuoMother", description = "BSN of the father or duomother")
  })
  @GetMapping(
      value = "/info/name-selection",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ApiNameSelectionInfoResponse getNameSelection(
      @RequestParam("bsnMother") String bsnMother,
      @RequestParam("bsnFatherOrDuoMother") String bsnFatherDuoMother) {
    return ApiNameSelectionInfoResponse
        .of(birthService.getNameSelectionInfo(new Bsn(bsnMother), new Bsn(bsnFatherDuoMother)));
  }

  @Operation(
      operationId = "getFamilySituation",
      summary = "Returns the family situation",
      description = "Returns the family situation at the date of birth")
  @Parameters({
      @Parameter(name = "birthDate", description = "Birthdate of the child(ren)"),
      @Parameter(name = "bsnMother", description = "BSN of the mother")
  })
  @GetMapping(
      value = "/info/family-situation",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ApiFamilySituationInfoResponse getFamilySituation(
      @RequestParam("birthDate") @DateTimeFormat(iso = DATE) LocalDate birthDate,
      @RequestParam("bsnMother") String bsnMother) {
    return ApiFamilySituationInfoResponse.of(birthService.getFamilySituationInfo(birthDate, new Bsn(bsnMother)));
  }

  @Operation(
      summary = "Returns acknowledgement by mother's BSN",
      description = "Returns acknowledgement by mother's BSN")
  @Parameters(@Parameter(name = "bsnMother", required = true, description = "BSN of the mother"))
  @GetMapping(
      value = "/info/acknowledgement",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ResponseEntity<ApiUnbornAcknowledgement> getAcknowledgement(@RequestParam("bsnMother") String bsnMother) {
    return acknowledgementService.findUnborn(new Bsn(bsnMother))
        .map(ApiUnbornAcknowledgement::of)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
