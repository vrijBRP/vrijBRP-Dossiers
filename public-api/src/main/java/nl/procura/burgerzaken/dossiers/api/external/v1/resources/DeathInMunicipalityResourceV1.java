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

import nl.procura.burgerzaken.dossiers.api.external.v1.deaths.ApiDeathInMunicipality;
import nl.procura.burgerzaken.dossiers.api.external.v1.deaths.ApiDiscoveredBody;
import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.deaths.DeathInMunicipality;
import nl.procura.burgerzaken.dossiers.model.deaths.DiscoveredBody;
import nl.procura.burgerzaken.dossiers.service.ClientService;
import nl.procura.burgerzaken.dossiers.service.DeathInMunicipalityService;
import nl.procura.burgerzaken.dossiers.service.DiscoveredBodyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Deaths",
    description = "Actions related to deaths")
@RestController
@RequestMapping("/api/v1/deaths")
public class DeathInMunicipalityResourceV1 {

  private final DeathInMunicipalityService deathInMunicipalityService;
  private DiscoveredBodyService            discoveredBodyService;
  private final ClientService              clientService;

  public DeathInMunicipalityResourceV1(
      DeathInMunicipalityService deathInMunicipalityService,
      DiscoveredBodyService discoveredBodyService,
      ClientService clientService) {
    this.deathInMunicipalityService = deathInMunicipalityService;
    this.discoveredBodyService = discoveredBodyService;
    this.clientService = clientService;
  }

  @Operation(
      operationId = "findDeathInMunicipality",
      summary = "Find one dossier",
      description = "Find a death in municipality dossier based on a dossier-id")
  @GetMapping(
      value = "/in-municipality/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ApiDeathInMunicipality findDeathInMunicipality(@PathVariable String dossierId) {
    return ApiDeathInMunicipality.of(deathInMunicipalityService.findByCaseNumber(dossierId));
  }

  @Operation(
      operationId = "addDeathInMunicipality",
      summary = "Add a new dossier",
      description = "Add a new 'death in municipality' dossier")
  @PostMapping(
      value = "/in-municipality",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiDeathInMunicipality> addDeathInMunicipality(
      @Valid @RequestBody ApiDeathInMunicipality death,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    Client client = clientService.getById(jwt.getSubject());
    DeathInMunicipality newDossier = deathInMunicipalityService.add(death.createNew(client));
    return new ResponseEntity<>(ApiDeathInMunicipality.of(newDossier), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "findDiscoveredBody",
      summary = "Find one dossier",
      description = "Find a 'discovered body' dossier based on a dossier-id")
  @GetMapping(
      value = "/discovered-body/{dossierId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  })
  public ApiDiscoveredBody findDiscoveredBody(@PathVariable String dossierId) {
    return ApiDiscoveredBody.of(discoveredBodyService.findByCaseNumber(dossierId));
  }

  @Operation(
      operationId = "addDiscoveredBody",
      summary = "Add a new dossier",
      description = "Add a new 'discovered body' dossier")
  @PostMapping(
      value = "/discovered-body",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Resource successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiDiscoveredBody> addDiscoveredBody(
      @Valid @RequestBody ApiDiscoveredBody discoveredBody,
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    Client client = clientService.getById(jwt.getSubject());
    DiscoveredBody newDossier = discoveredBodyService.add(discoveredBody.createNew(client));
    return new ResponseEntity<>(ApiDiscoveredBody.of(newDossier), HttpStatus.CREATED);
  }
}
