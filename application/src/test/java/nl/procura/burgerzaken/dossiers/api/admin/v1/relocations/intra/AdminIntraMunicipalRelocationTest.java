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

package nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.intra;

import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiDossier;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiDossierStatus;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiReferenceId;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminDossierType;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.base.*;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;

public final class AdminIntraMunicipalRelocationTest {

  private AdminIntraMunicipalRelocationTest() {
  }

  public static AdminApiIntraMunicipalRelocation newRelocation(Set<AdminApiReferenceId> referenceIds) {
    AdminApiContactInformation contactInfo = AdminApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("12345")
        .build();

    return AdminApiIntraMunicipalRelocation.builder()
        .dossier(AdminApiDossier.builder()
            .referenceIds(referenceIds)
            .type(AdminDossierType.builder()
                .code("B1234")
                .description("Binnengemeentelijke verhuizing").build())
            .status(AdminApiDossierStatus.of(DossierStatus.CREATED))
            .startDate(LocalDate.of(2019, Month.JANUARY, 1))
            .entryDateTime(LocalDateTime.of(2019, Month.JANUARY,
                1, 10, 11, 12))
            .build())
        .declarant(AdminApiDeclarant.builder()
            .bsn("999993653")
            .contactInformation(contactInfo)
            .build())
        .newAddress(AdminApiMunicipalAddress.builder()
            .street("Dorpstraat")
            .houseNumber(1)
            .houseLetter("A")
            .houseNumberAddition("4")
            .postalCode("1234AA")
            .residence("Koedijk")
            .municipality(AdminApiMunicipality.builder()
                .code("4567")
                .description("Alkmaar")
                .build())
            .addressFunction(AdminApiAddressFunctionType.LIVING_ADDRESS)
            .numberOfResidents(2)
            .destinationCurrentResidents("Don't know")
            .liveIn(AdminApiLiveIn.builder()
                .liveInApplicable(true)
                .consent(AdminApiLiveInConsentType.PENDING)
                .consenter(AdminApiConsenter.builder()
                    .bsn("999990639")
                    .contactInformation(contactInfo)
                    .build())
                .build())
            .mainOccupant(AdminApiMainOccupant.builder()
                .bsn("999993653")
                .contactInformation(contactInfo)
                .build())
            .build())
        .relocators(singletonList(AdminApiIntraMunicipalRelocationPerson.builder()
            .declarationType(AdminApiDeclarationType.REGISTERED)
            .bsn("999993653")
            .contactInformation(contactInfo)
            .build()))
        .build();
  }
}
