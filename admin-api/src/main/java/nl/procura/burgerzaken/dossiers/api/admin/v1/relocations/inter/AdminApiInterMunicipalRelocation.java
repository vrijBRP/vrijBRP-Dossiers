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

package nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.inter;

import static java.util.stream.Collectors.toList;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.INTER_MUNICIPAL_RELOCATION;

import java.util.List;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiDossier;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.base.*;
import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.relocations.InterMunicipalRelocation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "InterMunicipalRelocation")
public class AdminApiInterMunicipalRelocation {

  @Schema(required = true)
  private AdminApiDossier dossier;

  @Schema(required = true)
  private AdminApiDeclarant declarant;

  @Schema(required = true)
  private AdminApiMunicipalAddress newAddress;

  @Schema(required = true)
  private AdminApiMunicipality previousMunicipality;

  private List<AdminApiInterMunicipalRelocationPerson> relocators;

  public static AdminApiInterMunicipalRelocation of(InterMunicipalRelocation relocation) {
    return AdminApiInterMunicipalRelocation.builder()
        .dossier(AdminApiDossier.of(relocation.getDossier()))
        .declarant(relocation.getDeclarant()
            .map(AdminApiDeclarant::of)
            .orElse(null))
        .newAddress(AdminApiMunicipalAddress.builder()
            .street(relocation.getStreet())
            .houseNumber(relocation.getHouseNumber())
            .houseLetter(relocation.getHouseNumberLetter())
            .houseNumberAddition(relocation.getHouseNumberAddition())
            .postalCode(relocation.getPostalCode())
            .residence(relocation.getResidence())
            .municipality(AdminApiMunicipality.builder()
                .code(relocation.getMunicipality())
                .build())
            .addressFunction(AdminApiAddressFunctionType.valueOfCode(relocation.getAddressFunction()))
            .numberOfResidents(relocation.getResidentsCount())
            .destinationCurrentResidents(relocation.getDestCurrResidents())
            .liveIn(AdminApiLiveIn.builder()
                .liveInApplicable(relocation.getLiveIn())
                .consent(AdminApiLiveInConsentType.valueOfCode(relocation.getConsent()))
                .consenter(relocation.getConsenter()
                    .map(AdminApiConsenter::of)
                    .orElse(null))
                .build())
            .mainOccupant(relocation.getMainOccupant()
                .map(AdminApiMainOccupant::of)
                .orElse(null))
            .build())
        .relocators(relocation.getRelocators().stream()
            .map(AdminApiInterMunicipalRelocationPerson::of)
            .collect(toList()))
        .previousMunicipality(AdminApiMunicipality.builder()
            .code(relocation.getPreviousMunicipality())
            .build())
        .build();
  }

  public InterMunicipalRelocation withNewId(Client client) {
    Dossier newDossier = this.dossier.withNewId(INTER_MUNICIPAL_RELOCATION, client);
    InterMunicipalRelocation relocation = new InterMunicipalRelocation(newDossier);
    relocation.setDeclarant(declarant.toPerson(newDossier));
    relocation.setStreet(newAddress.getStreet());
    relocation.setHouseNumber(newAddress.getHouseNumber());
    relocation.setHouseNumberLetter(newAddress.getHouseLetter());
    relocation.setHouseNumberAddition(newAddress.getHouseNumberAddition());
    relocation.setPostalCode(newAddress.getPostalCode());
    relocation.setResidence(newAddress.getResidence());
    relocation.setMunicipality(newAddress.getMunicipality().getCode());
    relocation.setAddressFunction(newAddress.getAddressFunction().getCode());
    relocation.setResidentsCount(newAddress.getNumberOfResidents());
    relocation.setDestCurrResidents(newAddress.getDestinationCurrentResidents());
    relocation.setLiveIn(newAddress.getLiveIn().getLiveInApplicable());
    relocation.setConsent(newAddress.getLiveIn().getConsent().getValue());
    relocation.setConsenter(newAddress.getLiveIn().getConsenter().toPerson(newDossier));
    relocation.setMainOccupant(newAddress.getMainOccupant().toPerson(newDossier));
    relocators.forEach(r -> relocation.addRelocator(r.toRelocator(newDossier)));
    relocation.setPreviousMunicipality(previousMunicipality.getCode());
    return relocation;
  }
}
