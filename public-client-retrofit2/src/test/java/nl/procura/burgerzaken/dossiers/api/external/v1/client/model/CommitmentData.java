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
import static nl.procura.burgerzaken.gba.numbers.Bsn.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

public final class CommitmentData {

  public static final String DOSSIER_ID   = "commitment-1234";
  public static final String BSN_PARTNER1 = String.valueOf(TEST_BSN_1);
  public static final String BSN_PARTNER2 = String.valueOf(TEST_BSN_5);
  public static final String BSN_WITNESS  = String.valueOf(TEST_BSN_6);

  private CommitmentData() {
  }

  public static Commitment createNew() {
    return new Commitment()
        .dossier(new Dossier()
            .referenceIds(singletonList(new DossierReferenceId()
                .id(DOSSIER_ID)
                .description("ZAAKSYSTEEM")))
            .type(new DossierType().code("1").description("a"))
            .status(new DossierStatus().code("a").description("a"))
            .startDate(LocalDate.of(2019, Month.JANUARY, 1))
            .entryDateTime(LocalDateTime.now().withNano(0)))
        .partner1(new CommitmentPartner()
            .bsn(BSN_PARTNER1)
            .nameAfterCommitment(new CommitmentNameUse()
                .nameUseType(CommitmentNameUse.NameUseTypeEnum.V)
                .lastname("Vries")
                .prefix("de")
                .title("B"))
            .contactInformation(new ContactInformation()
                .email("partner1@procura.nl")
                .telephoneNumber("31654322345")))
        .partner2(new CommitmentPartner()
            .bsn(BSN_PARTNER2)
            .nameAfterCommitment(new CommitmentNameUse()
                .nameUseType(CommitmentNameUse.NameUseTypeEnum.N)
                .lastname("Jansen")
                .prefix(null)
                .title("JV"))
            .contactInformation(new ContactInformation()
                .email("burgerzaken@procura.nl")
                .telephoneNumber("0887708100")))
        .planning(new CommitmentPlanning()
            .commitmentDateTime(LocalDateTime.of(2020, 6, 7, 8, 9, 10))
            .commitmentType(CommitmentPlanning.CommitmentTypeEnum.MARRIAGE)
            .intentionDate(LocalDate.of(2020, 4, 1))
            .remarks("Nice Wedding!"))
        .location(new CommitmentLocation()
            .name("Het Generaalshuis (Theater aan het Vrijthof)")
            //.addAliasesItem("generaalshuis")
            .addOptionsItem(new CommitmentLocationOption()
                .name("Taalceremonie")
                .description("Taalceremonie")
                .type(CommitmentLocationOption.TypeEnum.TEXT)
                .value("Spaans")
                .addAliasesItem("taal_ceremonie"))
            .addOptionsItem(new CommitmentLocationOption()
                .name("Max. aantal personen")
                .description("Max. aantal personen (1 - 250")
                .type(CommitmentLocationOption.TypeEnum.NUMBER)
                .value("75")
                .addAliasesItem("max_personen")
                .addAliasesItem("max.aantal.personen"))
            .addOptionsItem(new CommitmentLocationOption()
                .name("Trouwboekje/Partnerschapsboekje")
                .description("Trouwboekje/Partnerschapsboekje")
                .type(CommitmentLocationOption.TypeEnum.BOOLEAN)
                .value("true")
                .addAliasesItem("trouwboekje")))
        .officials(new CommitmentOfficials()
            .addPreferencesItem(new CommitmentOfficial()
                .addAliasesItem("marco")))
        .witnesses(new CommitmentWitnesses()
            .numberOfMunicipalWitnesses(2)
            .addChosenItem(new CommitmentWitness()
                .bsn(null)
                .firstname("Firstname 1")
                .lastname("Lastname 1")
                .birthdate(2004_12_10)
                .remarks("Witness 1"))
            .addChosenItem(new CommitmentWitness()
                .bsn(BSN_WITNESS)
                .remarks("Witness 2")));
  }
}
