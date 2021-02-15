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

package nl.procura.burgerzaken.dossiers.api.external.v1.deaths;

import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.DISCOVERED_BODY;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.deaths.DiscoveredBody;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "DiscoveredBody")
public class ApiDiscoveredBody {

  @Valid
  @Schema(name = "dossier", required = true)
  @NotNull(message = "dossier is mandatory")
  private ApiDossier dossier;

  @Valid
  @Schema(name = "deceased", required = true)
  @NotNull(message = "deceased is mandatory")
  private ApiDeceased deceased;

  @Valid
  @Schema(name = "writtenDeclarantType")
  @NotNull(message = "writtenDeclarantType is mandatory")
  private ApiWrittenDeclarantType writtenDeclarantType;

  @Schema(name = "deathByNaturalCauses")
  @NotNull(message = "deathByNaturalCauses is mandatory")
  private Boolean deathByNaturalCauses;

  @Valid
  @Schema(name = "municipality")
  @NotNull(message = "municipality is mandatory")
  private ApiMunicipality municipality;

  @Schema(name = "dateOfDeath", required = true)
  @NotNull(message = "dateOfDeath is mandatory")
  private LocalDate dateOfFinding;

  @Schema(name = "timeOfDeath", pattern = "HH:mm")
  @Pattern(regexp = "[0-9]{1,2}:[0-9]{2}", message = "time must match HH:mm")
  @NotNull(message = "time is mandatory")
  private String timeOfFinding;

  @Schema(name = "explanation")
  private String explanation;

  @Valid
  @Schema(name = "funeralServices")
  @NotNull(message = "funeralServices is mandatory")
  private ApiFuneralServices funeralServices;

  @Valid
  @Schema(name = "correspondence")
  @NotNull(message = "correspondence is mandatory")
  private ApiCorrespondence correspondence;

  @Valid
  @Schema(name = "extracts")
  @NotNull(message = "extracts is mandatory")
  private List<ApiExtract> extracts;

  public static ApiDiscoveredBody of(DiscoveredBody discoveredBody) {
    return ApiDiscoveredBody.builder()
        .dossier(ApiDossier.of(discoveredBody.getDossier()))
        .writtenDeclarantType(ApiWrittenDeclarantType.valueOfType(discoveredBody.getWrittenDeclarantType()))
        .deathByNaturalCauses(discoveredBody.getDeathByNaturalCauses())
        .municipality(ApiMunicipality.of(discoveredBody.getMunicipality()))
        .dateOfFinding(discoveredBody.getDateOfFinding())
        .timeOfFinding(discoveredBody.getTimeOfFinding())
        .explanation(discoveredBody.getExplanation())
        .funeralServices(ApiFuneralServices.of(discoveredBody.getFuneralServices()))
        .deceased(ApiDeceased.of(discoveredBody.getDeceased()))
        .correspondence(ApiCorrespondence.of(discoveredBody.getCorrespondence()))
        .extracts(discoveredBody.getExtracts().stream().map(ApiExtract::of).collect(Collectors.toList()))
        .build();
  }

  public DiscoveredBody createNew(Client client) {
    Dossier newDossier = this.dossier.createNew(DISCOVERED_BODY, client);
    return new DiscoveredBody(newDossier)
        .setWrittenDeclarantType(getWrittenDeclarantType().getType())
        .setDeathByNaturalCauses(getDeathByNaturalCauses())
        .setMunicipality(getMunicipality().toTableValue())
        .setDateOfFinding(getDateOfFinding())
        .setTimeOfFinding(getTimeOfFinding())
        .setExplanation(getExplanation())
        .setFuneralServices(getFuneralServices().toFuneralServices())
        .setDeceased(getDeceased().toDeceased())
        .setCorrespondence(getCorrespondence().toCorrespondence())
        .setExtracts(getExtracts().stream().map(ApiExtract::toExtract).collect(Collectors.toList()));
  }
}
