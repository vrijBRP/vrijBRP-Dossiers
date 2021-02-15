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

package nl.procura.burgerzaken.dossiers.api.external.v1.client.model;

import static java.util.Collections.singletonList;
import static nl.procura.burgerzaken.dossiers.api.external.v1.client.model.IntraMunicipalRelocationPerson.DeclarationTypeEnum.PARTNER;
import static nl.procura.burgerzaken.dossiers.api.external.v1.client.model.IntraMunicipalRelocationPerson.DeclarationTypeEnum.REGISTERED;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_1;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_5;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

public final class IntraMunicipalRelocationData {

  public static final String DOSSIER_ID        = "relocation-1234";
  public static final String CONSENTER_BSN     = "999990639";
  public static final String MAIN_OCCUPANT_BSN = "999993653";

  private IntraMunicipalRelocationData() {
  }

  public static IntraMunicipalRelocation createNew() {
    ContactInformation ci = new ContactInformation();
    ci.email("burgerzaken@procura.nl")
        .telephoneNumber("12345");

    return new IntraMunicipalRelocation()
        .dossier(new Dossier()
            .referenceIds(singletonList(new DossierReferenceId()
                .id(DOSSIER_ID)
                .description("ZAAKSYSTEEM")))
            .type(new DossierType().code("1").description("a"))
            .status(new DossierStatus().code("a").description("a"))
            .startDate(LocalDate.of(2019, Month.JANUARY, 1))
            .entryDateTime(LocalDateTime.now().withNano(0)))
        .declarant(new RelocationDeclarant()
            .bsn(String.valueOf(TEST_BSN_1))
            .contactInformation(ci))
        .newAddress(new RelocationMunicipalAddress()
            .street("Dorpstraat")
            .houseNumber(1)
            .houseLetter("A")
            .houseNumberAddition("4")
            .postalCode("1234AA")
            .residence("De Rijp")
            .municipality(new Municipality()
                .code("4567")
                .description("Alkmaar"))
            .addressFunction(RelocationMunicipalAddress.AddressFunctionEnum.LIVING_ADDRESS)
            .numberOfResidents(2)
            .destinationCurrentResidents("Geen idee")
            .liveIn(new RelocationLiveIn()
                .liveInApplicable(true)
                .consent(RelocationLiveIn.ConsentEnum.PENDING)
                .consenter(new RelocationConsenter()
                    .bsn(CONSENTER_BSN)
                    .contactInformation(ci)))
            .mainOccupant(new RelocationMainOccupant()
                .bsn(MAIN_OCCUPANT_BSN)
                .contactInformation(ci)))
        .relocators(Arrays.asList(
            new IntraMunicipalRelocationPerson()
                .bsn(String.valueOf(TEST_BSN_1))
                .contactInformation(ci)
                .declarationType(REGISTERED),
            new IntraMunicipalRelocationPerson()
                .bsn(String.valueOf(TEST_BSN_5))
                .contactInformation(ci)
                .declarationType(PARTNER)));
  }

  public static RelocationConsent createConsent() {
    RelocationConsenter consenter = new RelocationConsenter();
    consenter.setBsn(String.valueOf(TEST_BSN_1));

    RelocationConsent consent = new RelocationConsent();
    consent.setConsenter(consenter);
    consent.setDossierId(DOSSIER_ID);
    consent.setConsent(RelocationConsent.ConsentEnum.APPROVED);

    return consent;
  }
}
