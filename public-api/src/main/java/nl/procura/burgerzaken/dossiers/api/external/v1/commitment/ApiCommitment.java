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

package nl.procura.burgerzaken.dossiers.api.external.v1.commitment;

import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.COMMITMENT;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.commitment.*;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Commitment")
public class ApiCommitment {

  public static final int MAX_NUMBER_OFFICIALS = 2;

  @Valid
  @Schema(name = "dossier", required = true)
  @NotNull(message = "dossier is mandatory")
  private ApiDossier dossier;

  @Valid
  @Schema(name = "partner1", required = true)
  @NotNull(message = "partner1 is mandatory")
  private ApiPartner partner1;

  @Valid
  @Schema(name = "partner2", required = true)
  @NotNull(message = "partner2 is mandatory")
  private ApiPartner partner2;

  @Valid
  @Schema(name = "planning", required = true)
  @NotNull(message = "planning is mandatory")
  private ApiPlanning planning;

  @Valid
  @Schema(name = "location")
  private ApiLocation location;

  @Valid
  @Schema(name = "officials")
  private ApiOfficials officials;

  @Valid
  @Schema(name = "witnesses")
  private ApiWitnesses witnesses;

  public static ApiCommitment of(Commitment commitment) {
    return ApiCommitment.builder()
        .dossier(ApiDossier.of(commitment.getDossier()))
        .partner1(ApiPartner.of(commitment.getPartner1()))
        .partner2(ApiPartner.of(commitment.getPartner2()))
        .planning(ApiPlanning.of(commitment.getPlanning()))
        .location(ApiLocation.of(commitment.getLocation()))
        .officials(ApiOfficials.of(commitment.getOfficials()))
        .witnesses(ApiWitnesses.of(commitment.getWitnesses()))
        .build();
  }

  public Commitment createNew(Client client) {
    Dossier newDossier = this.dossier.createNew(COMMITMENT, client);
    Commitment commitment = new Commitment(newDossier);
    commitment.setPartner1(partner1.toPartner());
    commitment.setPartner2(partner2.toPartner());

    if (planning != null) {
      commitment.setPlanning(new CommitmentPlanning()
          .setCommitmentType(planning.getCommitmentType().getType())
          .setCommitmentDate(planning.getCommitmentDateTime().toLocalDate())
          .setCommitmentTime(planning.getCommitmentDateTime().toLocalTime())
          .setIntentionDate(planning.getIntentionDate())
          .setRemarks(planning.getRemarks()));
    }

    if (location != null) {
      if (StringUtils.isBlank(location.getName()) && isEmpty(location.getAliases())) {
        throw new IllegalArgumentException("Name or alias of the location is required");
      }
      commitment.setLocation(new CommitmentLocation()
          .setName(location.getName())
          .setAliases(location.getAliases()));
      if (!isEmpty(location.getOptions())) {
        commitment.getLocation().setOptions(location.getOptions().stream()
            .map(option -> {
              if (StringUtils.isBlank(option.getName()) && isEmpty(option.getAliases())) {
                throw new IllegalArgumentException("Name or alias of the location option is required");
              }
              return new CommitmentLocationOption()
                  .setName(option.getName())
                  .setValue(option.getValue())
                  .setDescription(option.getDescription())
                  .setType(option.getType().getType())
                  .setAliases(option.getAliases());
            })
            .collect(Collectors.toList()));
      }
    }

    if (officials != null && officials.getPreferences() != null) {
      if (officials.getPreferences().size() > MAX_NUMBER_OFFICIALS) {
        throw new IllegalArgumentException("The maximum of preferred officials is " + MAX_NUMBER_OFFICIALS);
      }

      commitment.setOfficials(new CommitmentOfficials()
          .setPreferedOfficials(officials.getPreferences().stream()
              .map(p -> {
                if (StringUtils.isBlank(p.getName()) && isEmpty(p.getAliases())) {
                  throw new IllegalArgumentException("Name or alias of the official is required");
                }
                CommitmentOfficial official = new CommitmentOfficial()
                    .setName(p.getName())
                    .setAliases(p.getAliases());
                ApiContactInformation ci = p.getContactInformation();
                if (ci != null) {
                  official.setEmail(ci.getEmail());
                  official.setTelephoneNumber(ci.getTelephoneNumber());
                }
                return official;
              })
              .collect(Collectors.toList())));
    }

    if (witnesses != null) {
      commitment.setWitnesses(new CommitmentWitnesses()
          .setNumberOfMunicipalWitnesses(this.witnesses.getNumberOfMunicipalWitnesses()));
      if (!isEmpty(this.witnesses.getChosen())) {
        commitment.getWitnesses()
            .setChosenWitnesses(this.witnesses.getChosen().stream()
                .map(p -> {
                  if (StringUtils.isBlank(p.getBsn())) {
                    if (StringUtils.isBlank(p.getFirstname())) {
                      throw new IllegalArgumentException("First name of the witness is required");
                    }
                    if (StringUtils.isBlank(p.getLastname())) {
                      throw new IllegalArgumentException("Last name of the witness is required");
                    }
                    if (p.getBirthdate() == null) {
                      throw new IllegalArgumentException("Birthdate of the witness is required");
                    }
                  }

                  return new CommitmentWitness()
                      .setBsn(p.getBsn())
                      .setFirstname(p.getFirstname())
                      .setLastname(p.getLastname())
                      .setPrefix(p.getPrefix())
                      .setBirthdate(p.getBirthdate())
                      .setRemarks(p.getRemarks());
                })
                .collect(Collectors.toList()));
      }
    }
    return commitment;
  }
}
