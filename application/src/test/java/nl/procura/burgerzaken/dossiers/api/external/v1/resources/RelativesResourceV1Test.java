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
import static nl.procura.burgerzaken.dossiers.api.external.v1.relatives.ApiObstructionType.*;
import static nl.procura.burgerzaken.dossiers.api.external.v1.relatives.ApiRelationshipType.*;
import static nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiDeclarationType.AUTHORITY_HOLDER;
import static nl.procura.burgerzaken.gba.numbers.Bsn.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.PersonList;
import nl.procura.burgerzaken.dossiers.api.external.v1.relatives.ApiObstructionType;
import nl.procura.burgerzaken.dossiers.api.external.v1.relatives.ApiRelationshipType;
import nl.procura.burgerzaken.dossiers.api.external.v1.relatives.ApiRelative;
import nl.procura.burgerzaken.dossiers.api.external.v1.relatives.ApiRelativesResponse;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiDeclarationType;
import nl.procura.burgerzaken.dossiers.service.ProcuraWsService;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;

@ContextConfiguration(initializers = GbaSource.class)
class RelativesResourceV1Test extends BaseResourceTest {

  private static final String DECLARATOR_BSN                 = BsnUtils.toBsnString(TEST_BSN_4);
  private static final String PARTNER_BSN                    = BsnUtils.toBsnString(TEST_BSN_7);
  private static final String EX_PARTNER_BSN                 = BsnUtils.toBsnString(TEST_BSN_10);
  private static final String OTHER_MUN_PARENT_BSN           = BsnUtils.toBsnString(TEST_BSN_910);
  private static final String DECEASED_PARENT_BSN            = BsnUtils.toBsnString(TEST_BSN_8);
  private static final String CHILD_BSN                      = BsnUtils.toBsnString(TEST_BSN_9);
  private static final String CHILD_WITH_WRONG_BIRTHDATE_BSN = BsnUtils.toBsnString(TEST_BSN_3);
  private static final String ADULT_CHILD_BSN                = BsnUtils.toBsnString(TEST_BSN_1);
  private static final String OTHER_ADDRESS_CHILD_BSN        = BsnUtils.toBsnString(TEST_BSN_2);
  private static final String PERSON_NOT_FOUND_BSN           = BsnUtils.toBsnString(TEST_BSN_6);

  @MockBean
  private ProcuraWsService service;

  @Test
  public void canGetRelatives() {
    setupData(DECLARATOR_BSN, getCompleteRegistered());
    setupData(PARTNER_BSN, getPartner());
    setupData(CHILD_BSN, getUnderageChild());
    setupData(ADULT_CHILD_BSN, getAdultChild());
    setupData(CHILD_WITH_WRONG_BIRTHDATE_BSN, getChildWithIncompleteBirthdate());
    setupData(OTHER_ADDRESS_CHILD_BSN, getOtherAddressChild());
    setupData(OTHER_MUN_PARENT_BSN, getParentInOtherMunipality());
    setupData(DECEASED_PARENT_BSN, getDeceasedParent());

    ApiRelativesResponse resp = getApiRelatives("relatives_1");
    ApiRelative registered = getRelativeByBsn(resp, DECLARATOR_BSN);
    ApiRelative partner = getRelativeByBsn(resp, PARTNER_BSN);
    ApiRelative parentInOtherMun = getRelativeByBsn(resp, OTHER_MUN_PARENT_BSN);
    ApiRelative deceasedParent = getRelativeByBsn(resp, DECEASED_PARENT_BSN);
    ApiRelative childUnder16 = getRelativeByBsn(resp, CHILD_BSN);
    ApiRelative adultChild = getRelativeByBsn(resp, ADULT_CHILD_BSN);
    ApiRelative childWithWrongBirthDate = getRelativeByBsn(resp, CHILD_WITH_WRONG_BIRTHDATE_BSN);
    ApiRelative childNotFound = getRelativeByBsn(resp, PERSON_NOT_FOUND_BSN);

    assertEquals(REGISTERED, registered.getRelationshipType());
    assertEquals(ApiDeclarationType.REGISTERED, registered.getDeclarationType());
    assertEquals(EXISTING_RELOCATION_CASE, partner.getObstructions().get(0));
    assertFalse(registered.isSuitableForRelocation());

    assertEquals(ApiRelationshipType.PARTNER, partner.getRelationshipType());
    assertEquals(ApiDeclarationType.PARTNER, partner.getDeclarationType());
    assertEquals(EXISTING_RELOCATION_CASE, partner.getObstructions().get(0));
    assertFalse(partner.isSuitableForRelocation());

    assertEquals(CHILD, childUnder16.getRelationshipType());
    assertEquals(AUTHORITY_HOLDER, childUnder16.getDeclarationType());
    assertEquals(0, childUnder16.getObstructions().size());
    assertTrue(childUnder16.isSuitableForRelocation());

    assertEquals(CHILD, adultChild.getRelationshipType());
    assertEquals(ApiDeclarationType.PARENT_LIVING_WITH_ADULT_CHILD, adultChild.getDeclarationType());
    assertEquals(0, adultChild.getObstructions().size());
    assertTrue(adultChild.isSuitableForRelocation());

    assertEquals(AUTHORITY_HOLDER, childWithWrongBirthDate.getDeclarationType());
    assertEquals(0, childWithWrongBirthDate.getObstructions().size());
    assertTrue(childWithWrongBirthDate.isSuitableForRelocation());

    assertEquals(CHILD, childNotFound.getRelationshipType());
    assertEquals(ApiObstructionType.NO_PERSON_RECORD_FOUND, childNotFound.getObstructions().get(0));
    assertEquals(1, childNotFound.getObstructions().size());
    assertFalse(childNotFound.isSuitableForRelocation());

    assertEquals(PARENT, parentInOtherMun.getRelationshipType());
    assertEquals(1, parentInOtherMun.getObstructions().size());
    assertEquals(DIFFERENT_ADDRESS, parentInOtherMun.getObstructions().get(0));
    assertFalse(parentInOtherMun.isSuitableForRelocation());

    assertEquals(PARENT, deceasedParent.getRelationshipType());
    assertEquals(PERSON_IS_DECEASED, deceasedParent.getObstructions().get(0));
    assertEquals(1, deceasedParent.getObstructions().size());
    assertFalse(deceasedParent.isSuitableForRelocation());
  }

  @Test
  public void canGetRegisteredWithExPartner() {
    setupData(DECLARATOR_BSN, getRegisteredWithExPartner());
    setupData(EX_PARTNER_BSN, getExPartner());

    ApiRelativesResponse resp = getApiRelatives("relatives_2");
    ApiRelative exPartner = getRelativeByBsn(resp, EX_PARTNER_BSN);
    assertEquals(ApiRelationshipType.EX_PARTNER, exPartner.getRelationshipType());
    assertNull(exPartner.getDeclarationType());
    assertEquals(ApiObstructionType.RELATIONSHIP_HAS_ENDED, exPartner.getObstructions().get(0));
  }

  @Test
  public void canGetRegisteredWithBlockedChild() {
    setupData(DECLARATOR_BSN, getRegisteredWithChild());
    setupData(CHILD_BSN, getBlockedChild());

    ApiRelativesResponse resp = getApiRelatives("");
    ApiRelative child = getRelativeByBsn(resp, CHILD_BSN);
    assertEquals(ApiObstructionType.PERSON_RECORD_IS_BLOCKED, child.getObstructions().get(0));
    assertEquals(1, child.getObstructions().size());
  }

  @Test
  public void canGetRegisteredWithSuspendedChild() {
    setupData(DECLARATOR_BSN, getRegisteredWithChild());
    setupData(CHILD_BSN, getSuspendedChild());

    ApiRelativesResponse resp = getApiRelatives("");
    ApiRelative child = getRelativeByBsn(resp, CHILD_BSN);
    assertEquals(ApiObstructionType.PERSON_RECORD_IS_SUSPENDED, child.getObstructions().get(0));
    assertEquals(1, child.getObstructions().size());
  }

  @Test
  public void canGetRegisteredWithConfidentialityChild() {
    setupData(DECLARATOR_BSN, getRegisteredWithChild());
    setupData(CHILD_BSN, getConfidentialityChild());

    ApiRelativesResponse resp = getApiRelatives("");
    ApiRelative child = getRelativeByBsn(resp, CHILD_BSN);
    assertEquals(PERSON_HAS_CONFIDENTIALITY, child.getObstructions().get(0));
    assertEquals(1, child.getObstructions().size());
  }

  @Test
  public void canGetRegisteredWithRNIChild() {
    setupData(DECLARATOR_BSN, getRegisteredWithChild());
    setupData(CHILD_BSN, getRNI());

    ApiRelativesResponse resp = getApiRelatives("");
    ApiRelative child = getRelativeByBsn(resp, CHILD_BSN);
    assertTrue(child.getObstructions().contains(PERSON_IS_EMIGRATED));
    assertTrue(child.getObstructions().contains(DIFFERENT_ADDRESS));
    assertEquals(2, child.getObstructions().size());
  }

  private ApiRelativesResponse getApiRelatives(String documentation) {
    return newMockTest().get("/api/v1/relatives/" + DECLARATOR_BSN, DECLARATOR_BSN)
        .documentation(documentation)
        .status(status().isOk())
        .toClass(ApiRelativesResponse.class);
  }

  private void setupData(String bsn, GbaWsPersonList pl) {
    given(service.get(Long.parseLong(bsn))).willReturn(singletonList(pl));
    GbaSource.enqueueJsonResponse(getClass().getResource("relatives-dossiers.json"));
    GbaSource.enqueueJsonResponse(getClass().getResource("relatives-intra-relocation.json"));
  }

  private ApiRelative getRelativeByBsn(ApiRelativesResponse info, String bsn) {
    return info.getRelatives().stream()
        .filter(rel -> rel.getPerson().getBsn().equals(bsn))
        .findFirst()
        .orElse(null);
  }

  public GbaWsPersonList getCompleteRegistered() {
    return new PL()
        .person(DECLARATOR_BSN, 40)
        .address(398, "1234AA")
        .builder()
        //
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, PARTNER_BSN)
        //
        .toPL()
        .addCat(GBACat.KINDEREN)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, CHILD_BSN)
        //
        .toCat()
        .addSet(2)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, ADULT_CHILD_BSN)
        //
        .toCat()
        .addSet(3)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, OTHER_ADDRESS_CHILD_BSN)
        //
        .toCat()
        .addSet(4)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, PERSON_NOT_FOUND_BSN)
        //
        .toCat()
        .addSet(5)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, CHILD_WITH_WRONG_BIRTHDATE_BSN)
        //
        .toPL()
        .addCat(GBACat.OUDER_1)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, OTHER_MUN_PARENT_BSN)
        //
        .toPL()
        .addCat(GBACat.OUDER_2)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, DECEASED_PARENT_BSN)
        .build();
  }

  public GbaWsPersonList getRegisteredWithChild() {
    return new PL()
        .person(DECLARATOR_BSN, 40)
        .address(398, "1234AA")
        .builder()
        //
        .addCat(GBACat.KINDEREN)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, CHILD_BSN)
        .build();
  }

  public GbaWsPersonList getRegisteredWithExPartner() {
    return new PL()
        .person(DECLARATOR_BSN, 40)
        .address(398, "1234AA")
        .builder()
        //
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, EX_PARTNER_BSN)
        .addElem(GBAElem.DATUM_ONTBINDING, "20190101")
        .build();
  }

  public GbaWsPersonList getPartner() {
    return new PL()
        .person(PARTNER_BSN, 39)
        .address(398, "1234AA")
        .build();
  }

  public GbaWsPersonList getExPartner() {
    return new PL()
        .person(EX_PARTNER_BSN, 39)
        .address(398, "1234AA")
        .build();
  }

  public GbaWsPersonList getUnderageChild() {
    return new PL()
        .person(CHILD_BSN, 1)
        .address(398, "1234AA")
        .build();
  }

  public GbaWsPersonList getAdultChild() {
    return new PL()
        .person(ADULT_CHILD_BSN, 19)
        .address(398, "1234AA")
        .build();
  }

  public GbaWsPersonList getChildWithIncompleteBirthdate() {
    String date = String.format("%d0000", LocalDate.now().getYear() - 10);
    return new PL()
        .person(CHILD_WITH_WRONG_BIRTHDATE_BSN, date)
        .address(398, "1234AA")
        .build();
  }

  public GbaWsPersonList getOtherAddressChild() {
    return new PL()
        .person(OTHER_ADDRESS_CHILD_BSN, 18)
        .address(398, "1111BB")
        .blocked()
        .build();
  }

  public GbaWsPersonList getBlockedChild() {
    return new PL()
        .person(CHILD_BSN, 18)
        .address(398, "1234AA")
        .blocked()
        .build();
  }

  public GbaWsPersonList getSuspendedChild() {
    return new PL()
        .person(CHILD_BSN, 18)
        .address(398, "1234AA")
        .suspended()
        .build();
  }

  public GbaWsPersonList getConfidentialityChild() {
    return new PL()
        .person(CHILD_BSN, 18)
        .address(398, "1234AA")
        .confidentiality()
        .build();
  }

  public GbaWsPersonList getRNI() {
    return new PL()
        .person(CHILD_BSN, 18)
        .address(398, "1234AA")
        .RNI()
        .build();
  }

  public GbaWsPersonList getParentInOtherMunipality() {
    return new PL()
        .person(OTHER_MUN_PARENT_BSN, 72)
        .address(392, "1234AA")
        .build();
  }

  public GbaWsPersonList getDeceasedParent() {
    return new PL()
        .person(DECEASED_PARENT_BSN, 72)
        .address(398, "1234AA")
        .deceased()
        .build();
  }

  private String age(int years) {
    return LocalDate.now().minusYears(years).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  class PL {

    private final PersonList.PersonListBuilder builder = PersonList.builder();

    private PL person(String bsn, int age) {
      builder.addCat(GBACat.PERSOON)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.BSN, bsn)
          .addElem(GBAElem.GEBOORTEDATUM, age(age));
      return this;
    }

    private PL person(String bsn, String birthdate) {
      builder.addCat(GBACat.PERSOON)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.BSN, bsn)
          .addElem(GBAElem.GEBOORTEDATUM, birthdate);
      return this;
    }

    private PL address(long code, String postalCode) {
      builder.addCat(GBACat.VB)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.GEM_INSCHR_CODE, code)
          .addElem(GBAElem.POSTCODE, postalCode);
      return this;
    }

    public PL deceased() {
      builder.addCat(GBACat.OVERL)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.DATUM_OVERL, age(1));
      return this;
    }

    public PL suspended() {
      builder.addCat(GBACat.INSCHR)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.OMSCHR_REDEN_OPSCH_BIJHOUD, "O");
      return this;
    }

    public PL confidentiality() {
      builder.addCat(GBACat.INSCHR)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.IND_GEHEIM, "7");
      return this;
    }

    public PL blocked() {
      builder.addCat(GBACat.INSCHR)
          .addSet(1)
          .addRecord(1, GBARecStatus.CURRENT)
          .addElem(GBAElem.DATUM_INGANG_BLOK_PL, "20200202");
      return this;
    }

    public PL RNI() {
      builder.get(GBACat.VB.getCode())
          .get(1)
          .get(1)
          .removeElem(GBAElem.GEM_INSCHR_CODE)
          .addElem(GBAElem.GEM_INSCHR_CODE, "1999");
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
