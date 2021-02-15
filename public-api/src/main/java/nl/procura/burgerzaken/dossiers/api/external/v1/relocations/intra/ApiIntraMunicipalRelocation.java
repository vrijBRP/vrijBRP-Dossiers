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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.intra;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiLiveInConsentType.NOT_APPLICABLE;
import static nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiLiveInConsentType.PENDING;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.INTRA_MUNICIPAL_RELOCATION;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.*;
import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.model.relocations.IntraMunicipalRelocation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "IntraMunicipalRelocation")
public class ApiIntraMunicipalRelocation {

  @Valid
  @Schema(required = true)
  @NotNull(message = "dossier is mandatory")
  private ApiDossier dossier;

  @Valid
  @Schema(required = true)
  @NotNull(message = "declarant is mandatory")
  private ApiDeclarant declarant;

  @Valid
  @Schema(required = true)
  @NotNull(message = "newAddress is mandatory")
  private ApiMunicipalAddress newAddress;

  @Valid
  @Schema(required = true)
  @NotNull(message = "relocators are mandatory")
  @Size(min = 1, message = "list of relocators may not be empty")
  private List<ApiIntraMunicipalRelocationPerson> relocators;

  public static ApiIntraMunicipalRelocation of(IntraMunicipalRelocation relocation) {
    return ApiIntraMunicipalRelocation.builder()
        .dossier(ApiDossier.of(relocation.getDossier()))
        .declarant(relocation.getDeclarant()
            .map(ApiDeclarant::of)
            .orElse(null))
        .newAddress(ApiMunicipalAddress.builder()
            .street(relocation.getStreet())
            .houseNumber(relocation.getHouseNumber())
            .houseLetter(relocation.getHouseNumberLetter())
            .houseNumberAddition(relocation.getHouseNumberAddition())
            .postalCode(relocation.getPostalCode())
            .residence(relocation.getResidence())
            .municipality(ApiMunicipality.builder()
                .code(relocation.getMunicipality())
                .build())
            .addressFunction(ApiAddressFunctionType.valueOfCode(relocation.getAddressFunction()))
            .numberOfResidents(relocation.getResidentsCount())
            .destinationCurrentResidents(relocation.getDestCurrResidents())
            .liveIn(ApiLiveIn.builder()
                .liveInApplicable(relocation.getLiveIn())
                .consent(ApiLiveInConsentType.valueOfCode(relocation.getConsent()))
                .consenter(relocation.getConsenter()
                    .map(ApiConsenter::of)
                    .orElse(null))
                .build())
            .mainOccupant(relocation.getMainOccupant()
                .map(ApiMainOccupant::of)
                .orElse(null))
            .build())
        .relocators(relocation.getRelocators().stream()
            .map(ApiIntraMunicipalRelocationPerson::of)
            .collect(toList()))
        .build();
  }

  public IntraMunicipalRelocation withNewId(Client client) {
    Dossier newDossier = this.dossier.createNew(INTRA_MUNICIPAL_RELOCATION, client);
    IntraMunicipalRelocation relocation = new IntraMunicipalRelocation(newDossier);
    relocation.setDeclarant(declarant.toPerson(newDossier));
    relocation.setStreet(ofNullable(newAddress.getStreet()).orElse(""));
    relocation.setPostalCode(newAddress.getPostalCode());
    relocation.setHouseNumber(newAddress.getHouseNumber());
    relocation.setHouseNumberLetter(ofNullable(newAddress.getHouseLetter()).orElse(""));
    relocation.setHouseNumberAddition(ofNullable(newAddress.getHouseNumberAddition()).orElse(""));
    relocation.setResidence(ofNullable(newAddress.getResidence()).orElse(""));

    if (newAddress.getMunicipality() != null) {
      relocation.setMunicipality(newAddress.getMunicipality().getCode());
    }
    relocation.setAddressFunction(newAddress.getAddressFunction().getCode());
    relocation.setResidentsCount(newAddress.getNumberOfResidents());
    relocation.setDestCurrResidents(ofNullable(newAddress.getDestinationCurrentResidents()).orElse(""));

    ApiLiveIn liveIn = newAddress.getLiveIn();
    relocation.setLiveIn(liveIn.getLiveInApplicable());
    relocation.setConsent(NOT_APPLICABLE.getCode());

    if (liveIn.getLiveInApplicable()) {
      relocation.setConsent(ofNullable(liveIn.getConsent()).orElse(PENDING).getCode());

      if (liveIn.getConsenter() != null) {
        relocation.setConsenter(liveIn.getConsenter().toPerson(newDossier));
      }
      relocation.setMainOccupant(ofNullable(newAddress.getMainOccupant())
          .orElseThrow(() -> new ApiException(ApiErrorType.BAD_REQUEST,
              "mainOccupant is mandatory if liveInApplicable is true"))
          .toPerson(newDossier));
    }

    relocators.forEach(r -> relocation.addRelocator(r.toRelocator(newDossier)));
    return relocation;
  }
}
