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

import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.DEATH_IN_MUNICIPALITY;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.deaths.DeathInMunicipality;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "DeathInMunicipality")
public class ApiDeathInMunicipality {

  @Valid
  @Schema(name = "dossier", required = true)
  @NotNull(message = "dossier is mandatory")
  private ApiDossier dossier;

  @Valid
  @Schema(name = "declarant", required = true)
  @NotNull(message = "declarant is mandatory")
  private ApiDeclarant declarant;

  @Valid
  @Schema(name = "deceased", required = true)
  @NotNull(message = "deceased is mandatory")
  private ApiDeceased deceased;

  @Schema(name = "deathByNaturalCauses")
  @NotNull(message = "deathByNaturalCauses is mandatory")
  private Boolean deathByNaturalCauses;

  @Valid
  @Schema(name = "municipality")
  @NotNull(message = "municipality is mandatory")
  private ApiMunicipality municipality;

  @Schema(name = "dateOfDeath", required = true)
  @NotNull(message = "dateOfDeath is mandatory")
  private LocalDate dateOfDeath;

  @Schema(name = "timeOfDeath", pattern = "HH:mm")
  @Pattern(regexp = "[0-9]{1,2}:[0-9]{2}", message = "time must match HH:mm")
  private String timeOfDeath;

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

  public static ApiDeathInMunicipality of(DeathInMunicipality death) {
    return ApiDeathInMunicipality.builder()
        .dossier(ApiDossier.of(death.getDossier()))
        .deathByNaturalCauses(death.getDeathByNaturalCauses())
        .municipality(ApiMunicipality.of(death.getMunicipality()))
        .dateOfDeath(death.getDateOfDeath())
        .timeOfDeath(death.getTimeOfDeath())
        .declarant(ApiDeclarant.of(death.getDeclarant()))
        .funeralServices(ApiFuneralServices.of(death.getFuneralServices()))
        .deceased(ApiDeceased.of(death.getDeceased()))
        .correspondence(ApiCorrespondence.of(death.getCorrespondence()))
        .extracts(death.getExtracts().stream().map(ApiExtract::of).collect(Collectors.toList()))
        .build();
  }

  public DeathInMunicipality createNew(Client client) {
    Dossier newDossier = this.dossier.createNew(DEATH_IN_MUNICIPALITY, client);
    return new DeathInMunicipality(newDossier)
        .setDeathByNaturalCauses(getDeathByNaturalCauses())
        .setMunicipality(getMunicipality().toTableValue())
        .setDateOfDeath(getDateOfDeath())
        .setTimeOfDeath(getTimeOfDeath())
        .setDeclarant(getDeclarant().toPerson())
        .setFuneralServices(getFuneralServices().toFuneralServices())
        .setDeceased(getDeceased().toDeceased())
        .setCorrespondence(getCorrespondence().toCorrespondence())
        .setExtracts(getExtracts().stream().map(ApiExtract::toExtract).collect(Collectors.toList()));
  }
}
