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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMessage;
import nl.procura.burgerzaken.dossiers.api.external.v1.commitment.ApiCommitment;
import nl.procura.burgerzaken.dossiers.model.commitment.Commitment;
import nl.procura.burgerzaken.dossiers.service.ClientService;
import nl.procura.burgerzaken.dossiers.service.CommitmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Commitment",
    description = "Actions related to commitment")
@RestController
@RequestMapping("/api/v1/commitments")
public class CommitmentResourceV1 {

  private final CommitmentService service;
  private final ClientService     clientService;

  public CommitmentResourceV1(CommitmentService service, ClientService clientService) {
    this.service = service;
    this.clientService = clientService;
  }

  @Operation(
      operationId = "findCommitment",
      summary = "Find one dossier",
      description = "Find a commitment dossier based on a dossier-id")
  @GetMapping(
      value = "/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ApiCommitment find(@PathVariable String dossierId) {
    return ApiCommitment.of(service.findByCaseNumber(dossierId));
  }

  @Operation(
      operationId = "addCommitment",
      summary = "Add a new dossier",
      description = "Add a new commitment dossier")
  @PostMapping(
      value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiCommitment> add(
      @Valid @RequestBody ApiCommitment commitment,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    Commitment newDossier = service.add(commitment.createNew(clientService.getById(jwt.getSubject())));
    return new ResponseEntity<>(ApiCommitment.of(newDossier), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateCommitment",
      summary = "Update a new dossier",
      description = "Update an existing commitment dossier")
  @PostMapping(
      value = "/{dossierId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully updated"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiCommitment> update(
      @PathVariable String dossierId,
      @Valid @RequestBody ApiCommitment commitment,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    commitment.getDossier().setDossierId(dossierId);
    Commitment newDossier = service.update(commitment.createNew(clientService.getById(jwt.getSubject())));
    return new ResponseEntity<>(ApiCommitment.of(newDossier), HttpStatus.OK);
  }

  @Operation(
      operationId = "cancelCommitment",
      summary = "Cancel a commitment",
      description = "Cancel a commitment with optional message")
  // not using DELETE as DELETE body has no defined semantics, see rfc7231
  @PostMapping(
      value = "/{dossierId}/cancel",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Resource successfully cancelled"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<?> cancelCommitment(@PathVariable String dossierId,
      @Valid @RequestBody(required = false) ApiMessage message) {
    if (message == null) {
      service.cancel(dossierId);
    } else {
      service.cancel(dossierId, message.getMessage());
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
