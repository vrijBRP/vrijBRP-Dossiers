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

import static java.util.Collections.singletonList;
import static nl.procura.burgerzaken.dossiers.util.BsnUtils.toBsnString;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_3;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_4;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_6;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_7;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_8;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_9;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.PersonList;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiTitlePredicateType;
import nl.procura.burgerzaken.dossiers.api.external.v1.birth.info.ApiFamilySituationInfoResponse;
import nl.procura.burgerzaken.dossiers.api.external.v1.birth.info.ApiNameSelectionInfoResponse;
import nl.procura.burgerzaken.dossiers.model.base.NameSelection;
import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;
import nl.procura.burgerzaken.dossiers.model.namechoice.NameChoice;
import nl.procura.burgerzaken.dossiers.service.NameChoiceService;
import nl.procura.burgerzaken.dossiers.service.ProcuraWsService;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;

import lombok.SneakyThrows;

@ContextConfiguration(initializers = GbaSource.class)
class BirthInfoResourceV1Test extends BaseResourceTest {

  private static final String URI_NAME_SELECTION   = "/api/v1/births/info/name-selection";
  private static final String URI_FAMILY_SITUATION = "/api/v1/births/info/family-situation";

  private static final long MAX_LEGAL_TERM = 306L;

  private static final String MOTHER_BSN    = toBsnString(TEST_BSN_3);
  private static final String PARTNER1_BSN  = toBsnString(TEST_BSN_6);
  private static final String PARTNER2_BSN  = toBsnString(TEST_BSN_4);
  private static final String PARTNER3_BSN  = toBsnString(TEST_BSN_6);
  private static final String NEIGHBOUR_BSN = toBsnString(TEST_BSN_7);
  private static final String CHILD_1_BSN   = toBsnString(TEST_BSN_8);
  private static final String CHILD_2_BSN   = toBsnString(TEST_BSN_9);
  private static final String CHILD_3_BSN   = toBsnString(TEST_BSN_3);

  public static final String PARAM_BIRTH_DATE               = "birthDate";
  public static final String PARAM_BSN_MOTHER               = "bsnMother";
  public static final String PARAM_BSN_FATHER_OR_DUO_MOTHER = "bsnFatherOrDuoMother";

  @MockBean
  private ProcuraWsService procuraWsService;

  @MockBean
  private NameChoiceService nameChoiceService;

  @Test
  public void mustMatchChildrenWithPartner1ByName() throws IOException {
    setupData(MOTHER_BSN, getMother());
    setupData(PARTNER1_BSN, getPartner1());

    ApiNameSelectionInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BSN_FATHER_OR_DUO_MOTHER, PARTNER1_BSN)
        .get(URI_NAME_SELECTION)
        .status(status().isOk())
        .toClass(ApiNameSelectionInfoResponse.class);

    assertEquals(true, response.getJointChildren());
    assertEquals("Klaasen", response.getNameSelection().getLastname());
    assertNull(response.getNameSelection().getPrefix());
    assertEquals(ApiTitlePredicateType.B, response.getNameSelection().getTitlePredicate());
  }

  @Test
  @SneakyThrows
  public void mustMatchChildrenWithPartner2ByBsn() {
    setupData(MOTHER_BSN, getMother());
    setupData(PARTNER2_BSN, getPartner2());

    ApiNameSelectionInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BSN_FATHER_OR_DUO_MOTHER, PARTNER2_BSN)
        .get(URI_NAME_SELECTION)
        .status(status().isOk())
        .documentation("birth_info_name_selection")
        .toClass(ApiNameSelectionInfoResponse.class);

    assertEquals(true, response.getJointChildren());
    assertEquals("Vries", response.getNameSelection().getLastname());
    assertEquals("de", response.getNameSelection().getPrefix());
    assertNull(response.getNameSelection().getTitlePredicate());
  }

  @Test
  @SneakyThrows
  public void mustNotMatchStillbornChildWithPartner3() {
    setupData(MOTHER_BSN, getMother());
    setupData(PARTNER3_BSN, getPartner3());

    ApiNameSelectionInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BSN_FATHER_OR_DUO_MOTHER, PARTNER3_BSN)
        .get(URI_NAME_SELECTION)
        .status(status().isOk())
        .toClass(ApiNameSelectionInfoResponse.class);

    assertEquals(false, response.getJointChildren());
    assertNull(response.getNameSelection());
  }

  @Test
  @SneakyThrows
  public void mustNotMatchChildrenWithNeighbour() {
    setupData(MOTHER_BSN, getMother());
    setupData(NEIGHBOUR_BSN, getNeighbour());
    // no name choice found
    given(nameChoiceService.findByMotherAndFather(new Bsn(MOTHER_BSN), new Bsn(NEIGHBOUR_BSN)))
        .willReturn(Optional.empty());

    ApiNameSelectionInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BSN_FATHER_OR_DUO_MOTHER, NEIGHBOUR_BSN)
        .get(URI_NAME_SELECTION)
        .status(status().isOk())
        .toClass(ApiNameSelectionInfoResponse.class);

    assertEquals(false, response.getJointChildren());
    assertNull(response.getNameSelection());
  }

  @Test
  @SneakyThrows
  public void mustReturnNamechoiceWithCase() {
    setupData(MOTHER_BSN, getMother());
    setupData(NEIGHBOUR_BSN, getNeighbour());
    // Returns namechoice
    NameChoice nameChoice = new NameChoice();
    nameChoice.setNameSelection(new NameSelection("Vries", "de", TitlePredicateType.B));
    given(nameChoiceService.findByMotherAndFather(new Bsn(MOTHER_BSN), new Bsn(NEIGHBOUR_BSN)))
        .willReturn(Optional.of(nameChoice));

    ApiNameSelectionInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BSN_FATHER_OR_DUO_MOTHER, NEIGHBOUR_BSN)
        .get(URI_NAME_SELECTION)
        .status(status().isOk())
        .toClass(ApiNameSelectionInfoResponse.class);

    assertEquals(false, response.getJointChildren());
    assertEquals("Vries", response.getNameSelection().getLastname());
    assertEquals("de", response.getNameSelection().getPrefix());
    assertEquals(ApiTitlePredicateType.B, response.getNameSelection().getTitlePredicate());
  }

  @Test
  @SneakyThrows
  public void mustNotFindMarriageWithPartners() {
    setupData(MOTHER_BSN, getMother());

    ApiFamilySituationInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BIRTH_DATE, LocalDate.of(2009, 1, 1))
        .get(URI_FAMILY_SITUATION)
        .status(status().isOk())
        .toClass(ApiFamilySituationInfoResponse.class);

    assertEquals(false, response.getBornInPartnership());
    assertEquals(false, response.getLegalDeceasedPeriodRuleApplicable());
    assertNull(response.getPartner());
  }

  @Test
  @SneakyThrows
  public void mustFindMarriageWithPartner1() {
    setupData(MOTHER_BSN, getMother());

    ApiFamilySituationInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BIRTH_DATE, LocalDate.of(2011, 1, 1))
        .get(URI_FAMILY_SITUATION)
        .documentation("birth_info_family_situation")
        .status(status().isOk())
        .toClass(ApiFamilySituationInfoResponse.class);

    assertEquals(true, response.getBornInPartnership());
    assertEquals(false, response.getLegalDeceasedPeriodRuleApplicable());
    assertEquals(PARTNER1_BSN, response.getPartner().getBsn());
  }

  @Test
  @SneakyThrows
  public void mustFindMarriageWithPartner2InMarriage() {
    setupData(MOTHER_BSN, getMother());

    ApiFamilySituationInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BIRTH_DATE, LocalDate.of(2015, 1, 1))
        .get(URI_FAMILY_SITUATION)
        .status(status().isOk())
        .toClass(ApiFamilySituationInfoResponse.class);

    assertEquals(true, response.getBornInPartnership());
    assertEquals(false, response.getLegalDeceasedPeriodRuleApplicable());
    assertEquals(PARTNER2_BSN, response.getPartner().getBsn());
  }

  @Test
  @SneakyThrows
  public void mustFindMarriageWithPartner2InLegalPeriod() {
    setupData(MOTHER_BSN, getMother());

    ApiFamilySituationInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BIRTH_DATE, LocalDate.of(2018, 1, 1).plusDays(MAX_LEGAL_TERM))
        .get(URI_FAMILY_SITUATION)
        .status(status().isOk())
        .toClass(ApiFamilySituationInfoResponse.class);

    assertEquals(true, response.getBornInPartnership());
    assertEquals(true, response.getLegalDeceasedPeriodRuleApplicable());
    assertEquals(PARTNER2_BSN, response.getPartner().getBsn());
  }

  @Test
  @SneakyThrows
  public void mustNotFindMarriageWithPartner2OutsideLegalPeriod() {
    setupData(MOTHER_BSN, getMother());

    ApiFamilySituationInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BIRTH_DATE, LocalDate.of(2018, 1, 1).plusDays(MAX_LEGAL_TERM + 1))
        .get(URI_FAMILY_SITUATION)
        .status(status().isOk())
        .toClass(ApiFamilySituationInfoResponse.class);

    assertEquals(false, response.getBornInPartnership());
    assertEquals(false, response.getLegalDeceasedPeriodRuleApplicable());
    assertNull(response.getPartner());
  }

  @Test
  @SneakyThrows
  public void mustFindMarriageWithPartner3() {
    setupData(MOTHER_BSN, getMother());

    ApiFamilySituationInfoResponse response = newMockTest()
        .queryParam(PARAM_BSN_MOTHER, MOTHER_BSN)
        .queryParam(PARAM_BIRTH_DATE, LocalDate.now())
        .get(URI_FAMILY_SITUATION)
        .status(status().isOk())
        .toClass(ApiFamilySituationInfoResponse.class);

    assertEquals(true, response.getBornInPartnership());
    assertEquals(false, response.getLegalDeceasedPeriodRuleApplicable());
    assertEquals(PARTNER3_BSN, response.getPartner().getBsn());
  }

  private void setupData(String bsn, GbaWsPersonList pl) {
    given(procuraWsService.get(Long.parseLong(bsn))).willReturn(singletonList(pl));
  }

  public GbaWsPersonList getMother() {

    return new PL()
        .person(MOTHER_BSN)
        .child(1, CHILD_1_BSN, 2001_01_01, "Vries", "Piet", "de", "", "M", false)
        .child(2, CHILD_2_BSN, 2002_01_01, "Vries", "Truus", "de", "", "V", false)
        .child(3, CHILD_3_BSN, 2002_01_01, "Bakker", "Wim", "", "", "M", true)
        .child(4, "", 2004_01_01, "Klaasen", "Jan", "", "B", "M", false)
        .marriage(1, PARTNER1_BSN, 2010_01_01, 2013_01_01, "S")
        .marriage(2, PARTNER2_BSN, 2014_01_01, 2018_01_01, "O")
        .marriage(3, PARTNER3_BSN, 2019_01_01, -1, "")
        .builder()
        .build();
  }

  public GbaWsPersonList getPartner1() {

    return new PL()
        .person(PARTNER1_BSN)
        .child(1, "", 2004_01_01, "Klaasen", "Jan", "", "B", "M", false)
        .builder()
        .build();
  }

  public GbaWsPersonList getPartner2() {

    return new PL()
        .person(PARTNER2_BSN)
        .child(1, CHILD_3_BSN, 2001_01_01, "Jansen", "Jan", "", "", "M", false)
        .child(2, CHILD_2_BSN, 2002_01_01, "Vries", "Truus", "de", "", "V", false)
        .builder()
        .build();
  }

  public GbaWsPersonList getPartner3() {

    return new PL()
        .person(PARTNER3_BSN)
        .child(1, CHILD_3_BSN, 2002_01_01, "Bakker", "Wim", "", "", "M", true)
        .builder()
        .build();
  }

  public GbaWsPersonList getNeighbour() {

    return new PL()
        .person(NEIGHBOUR_BSN)
        .child(1, "", 2005_01_01, "Jansen", "Joost", "", "", "M", false)
        .builder()
        .build();
  }

  static class PL {

    private final PersonList.PersonListBuilder builder = PersonList.builder();

    private PL person(String bsn) {
      builder.addCat(GBACat.PERSOON)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.BSN, bsn);
      return this;
    }

    private PL child(int set, String bsn, int birthdate,
        String lastname, String firstname, String prefix,
        String title, String gender, boolean stillborn) {
      builder.getOrAddCat(GBACat.KINDEREN)
          .addSet(set)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.BSN, bsn)
          .addElem(GBAElem.GEBOORTEDATUM, birthdate)
          .addElem(GBAElem.GESLACHTSNAAM, lastname)
          .addElem(GBAElem.VOORNAMEN, firstname)
          .addElem(GBAElem.VOORV_GESLACHTSNAAM, prefix)
          .addElem(GBAElem.TITEL_PREDIKAAT, title)
          .addElem(GBAElem.GESLACHTSAAND, gender)
          .addElem(GBAElem.REG_BETREKK, stillborn ? "L" : "");
      return this;
    }

    public PL marriage(int set, String bsn, Integer marriageDate, Integer endDate, String reason) {
      PersonList.RecordBuilder rec = builder.getOrAddCat(GBACat.HUW_GPS)
          .addSet(set)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.BSN, bsn)
          .addElem(GBAElem.DATUM_VERBINTENIS, marriageDate);

      if (endDate >= 0) {
        rec.addElem(GBAElem.DATUM_ONTBINDING, endDate);
        rec.addElem(GBAElem.REDEN_ONTBINDING, reason);
      }
      return this;
    }

    public PersonList.PersonListBuilder builder() {
      return builder;
    }

    public GbaWsPersonList build() {
      return builder.build();
    }
  }
}
