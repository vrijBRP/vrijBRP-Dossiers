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
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_1;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_5;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

public final class BirthData {

  public static final String DOSSIER_ID    = "birth-1234";
  public static final String BSN_DECLARANT = String.valueOf(TEST_BSN_1);

  private BirthData() {
  }

  public static Birth createNew() {
    ContactInformation ci = new ContactInformation();
    ci.email("burgerzaken@procura.nl")
        .telephoneNumber("12345");

    return new Birth()
        .dossier(new Dossier()
            .referenceIds(singletonList(new DossierReferenceId()
                .id(DOSSIER_ID)
                .description("ZAAKSYSTEEM")))
            .type(new DossierType().code("1").description("a"))
            .status(new DossierStatus().code("a").description("a"))
            .startDate(LocalDate.of(2019, Month.JANUARY, 1))
            .entryDateTime(LocalDateTime.now().withNano(0)))
        .declarant(new BirthDeclarant()
            .bsn(BSN_DECLARANT)
            .contactInformation(ci))
        .mother(new BirthMother()
            .bsn(String.valueOf(TEST_BSN_5))
            .contactInformation(ci))
        .fatherDuoMother(new BirthFatherDuoMother()
            .bsn(String.valueOf(TEST_BSN_1))
            .contactInformation(ci))
        .nameSelection(
            new BirthNameSelection()
                .lastname("Vries")
                .prefix("de")
                .titlePredicate(BirthNameSelection.TitlePredicateEnum.B))
        .children(Arrays.asList(
            new BirthChild()
                .firstname("John")
                .birthDateTime(LocalDateTime.of(2020, Month.MAY, 8, 11, 12))
                .gender(BirthChild.GenderEnum.MAN),
            new BirthChild()
                .firstname("Mary")
                .birthDateTime(LocalDateTime.of(2020, Month.MAY, 7, 15, 23))
                .gender(BirthChild.GenderEnum.WOMAN)));
  }
}
