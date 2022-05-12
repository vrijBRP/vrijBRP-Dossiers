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

import static java.util.Collections.singleton;
import static nl.procura.burgerzaken.dossiers.JsonAssertions.assertJsonEquals;
import static nl.procura.burgerzaken.gba.numbers.Bsn.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiReferenceId;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.*;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.consent.ApiConsent;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.intra.ApiIntraMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.intra.ApiIntraMunicipalRelocationPerson;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;
import nl.procura.burgerzaken.gba.numbers.Bsn;

import okhttp3.mockwebserver.MockResponse;

@ContextConfiguration(initializers = GbaSource.class)
class IntraRelocationResourceV1Test extends BaseResourceTest {

  private static final String URI_ADD_RELOCATION       = "/api/v1/relocations/intra";
  private static final String URI_GET_RELOCATION_BY_ID = "/api/v1/relocations/intra/{dossierId}";
  private static final String URI_ADD_CONSENT          = "/api/v1/relocations/intra/consent";

  @Test
  public void addCompleteRelocation() {
    GbaSource.enqueueJsonResponse(getClass().getResource("intra_mun_relocation.json"));
    // when
    ApiIntraMunicipalRelocation request = getCompleteRelocation("intra-reloc-1234");
    ApiIntraMunicipalRelocation response = newMockTest()
        .post(URI_ADD_RELOCATION, request)
        .documentation("add_intra_reloc1")
        .status(status().isCreated())
        .toClass(ApiIntraMunicipalRelocation.class);
    // then request to GBA server must be valid
    String requestBody = GbaSource.takeRequestBody();
    // empty zaakId, datumInvoer, tijdInvoer as those are variable
    requestBody = requestBody.replaceFirst("\"datumInvoer\":[ 0-9]*", "\"datumInvoer\":0")
        .replaceFirst("\"tijdInvoer\":[ 0-9]*", "\"tijdInvoer\":0");
    assertJsonEquals(getClass().getResource("IntraRelocationResourceV1Test-gba-request.json"), requestBody);
    // then response must be valid
    ApiDossier dossier = response.getDossier();
    assertEquals("intra_mun_relocation", dossier.getType().getCode());
    assertEquals("created", dossier.getStatus().getCode());
    assertEquals(3, request.getRelocators().size());
    assertEquals(4, response.getRelocators().size());
    String dossierId = dossier.getDossierId();
    assertTrue(StringUtils.isNotBlank(dossierId));
    Set<ApiReferenceId> referenceIds = dossier.getReferenceIds();
    assertEquals(1, referenceIds.size());
    assertEquals("returned-id", referenceIds.iterator().next().getId());
    assertEquals(1, response.getNewAddress().getHouseNumber());
  }

  @Test
  public void addMinimalRelocation() {
    GbaSource.enqueueJsonResponse(getClass().getResource("intra_mun_relocation.json"));
    newMockTest().post(URI_ADD_RELOCATION, getMinRelocation())
        .documentation("add_intra_reloc2")
        .status(status().isCreated());
  }

  @Test
  public void findById() {
    GbaSource.enqueueJsonResponse(getClass().getResource("intra_mun_relocation.json"));
    String dossierId = "test-1234";
    ApiIntraMunicipalRelocation created = newMockTest()
        .get(URI_GET_RELOCATION_BY_ID, dossierId)
        .documentation("search_intra_reloc1")
        .status(status().isOk())
        .toClass(ApiIntraMunicipalRelocation.class);

    assertEquals(dossierId, created.getDossier().getDossierId());
    String declarantBsn = BsnUtils.toBsnString(TEST_BSN_1);
    assertEquals(declarantBsn, created.getDeclarant().getBsn());

    ApiMunicipalAddress foundAddress = created.getNewAddress();

    assertEquals("Dorpstraat", foundAddress.getStreet());
    assertEquals(1, foundAddress.getHouseNumber());
    assertEquals("L", foundAddress.getHouseLetter());
    assertEquals("T", foundAddress.getHouseNumberAddition());
    assertEquals("1234AA", foundAddress.getPostalCode());
    assertEquals("Oudorp", foundAddress.getResidence());
    assertEquals("0361", foundAddress.getMunicipality().getCode());
    assertEquals("W", foundAddress.getAddressFunction().getCode());
    assertEquals(1, foundAddress.getNumberOfResidents());
    ApiLiveIn liveIn = foundAddress.getLiveIn();
    assertEquals(Boolean.TRUE, liveIn.getLiveInApplicable());
    assertEquals(ApiLiveInConsentType.NOT_APPLICABLE, liveIn.getConsent());
    assertEquals(declarantBsn, foundAddress.getMainOccupant().getBsn());
    List<ApiIntraMunicipalRelocationPerson> relocators = created.getRelocators();
    assertEquals(4, relocators.size());
    ApiIntraMunicipalRelocationPerson declarant = relocators.stream()
        .filter(r -> declarantBsn.equals(r.getBsn())).findFirst().orElseThrow();
    assertEquals(ApiDeclarationType.REGISTERED, declarant.getDeclarationType());
    ApiIntraMunicipalRelocationPerson partner = relocators.stream()
        .filter(r -> new Bsn(TEST_BSN_5).equals(r.getBsn())).findFirst().orElseThrow();
    assertEquals(ApiDeclarationType.PARTNER, partner.getDeclarationType());
  }

  @Test
  public void findByIdMustReturn404WhenIdDoesNotExists() {
    GbaSource.enqueue(new MockResponse().setResponseCode(404));
    newMockTest().get(URI_GET_RELOCATION_BY_ID, "non-existing-id")
        .status(status().isNotFound());
  }

  @Test
  public void mustReturnError() {
    mustReturnRelocationError(req -> req.setDossier(null),
        "dossier is mandatory");
    mustReturnRelocationError(req -> req.getDossier().setStartDate(null),
        "startDate is mandatory",
        "add_intra_error");
    mustReturnRelocationError(req -> req.setDeclarant(null),
        "declarant is mandatory");
    mustReturnRelocationError(req -> req.getDeclarant().setBsn(null),
        "bsn is mandatory");
    mustReturnRelocationError(req -> req.setNewAddress(null),
        "newAddress is mandatory");
    mustReturnRelocationError(req -> req.getNewAddress().setPostalCode(null),
        "postalCode is mandatory");
    mustReturnRelocationError(req -> req.getNewAddress().setHouseNumber(null),
        "houseNumber is mandatory");
    mustReturnRelocationError(req -> req.getNewAddress().setNumberOfResidents(null),
        "numberOfResidents is mandatory");
    mustReturnRelocationError(req -> req.getNewAddress().setLiveIn(null),
        "liveIn is mandatory");
    mustReturnRelocationError(req -> req.getNewAddress().getLiveIn().setLiveInApplicable(null),
        "liveInApplicable is mandatory");
    mustReturnRelocationError(req -> req.getNewAddress().getLiveIn().setLiveInApplicable(true),
        "mainOccupant is mandatory if liveInApplicable is true");
    mustReturnRelocationError(req -> req.setRelocators(null),
        "relocators are mandatory");
    mustReturnRelocationError(req -> req.setRelocators(new ArrayList<>()),
        "list of relocators may not be empty");
  }

  @Test
  public void canAddRelocationWithConsent() {
    GbaSource.enqueueJsonResponse(getClass().getResource("intra_mun_relocation-add-consent.json"));
    String dossierId = "GYP-3J1";
    // when
    newMockTest()
        .post(URI_ADD_CONSENT, getConsent(dossierId))
        .status(status().isOk())
        .documentation("add_intra_reloc_consent1");
    // then request to GBA server must be valid
    assertJsonEquals(getClass().getResource("consent-gba-request.json"),
        GbaSource.takeRequestBody());
    // no response body
  }

  @Test
  void timeBefore10MustBeParsedCorrectly() {
    // given
    GbaSource.enqueueJsonResponse(getClass().getResource("intra_mun_relocation-before-10.json"));
    // when
    ApiIntraMunicipalRelocation resp = newMockTest()
        .post(URI_ADD_RELOCATION, getCompleteRelocation("intra-reloc-1234"))
        .documentation("add_intra_reloc1")
        .status(status().isCreated())
        .toClass(ApiIntraMunicipalRelocation.class);
    // then time must be parse with seconds
    assertEquals(LocalDateTime.of(2020, 4, 28, 9, 11, 12), resp.getDossier().getEntryDateTime());
  }

  private static ApiConsent getConsent(String dossierId) {
    ApiContactInformation contactInfo = ApiContactInformation.builder()
        .email("email@procura.nl")
        .telephoneNumber("9876543")
        .build();
    return ApiConsent.builder()
        .dossierId(dossierId)
        .consent(ApiLiveInConsentType.APPROVED)
        .consenter(ApiConsenter.builder()
            .bsn(new Bsn(TEST_BSN_3).toString())
            .contactInformation(contactInfo)
            .build())
        .build();
  }

  private void mustReturnRelocationError(Consumer<ApiIntraMunicipalRelocation> change,
      String errorMessage) {
    super.mustReturnError(getMinRelocation(), URI_ADD_RELOCATION, change, errorMessage, "");
  }

  private void mustReturnRelocationError(Consumer<ApiIntraMunicipalRelocation> change,
      String errorMessage,
      String documentation) {
    super.mustReturnError(getMinRelocation(), URI_ADD_RELOCATION, change, errorMessage, documentation);
  }

  private static ApiIntraMunicipalRelocation getCompleteRelocation(String referenceId) {
    ApiContactInformation contactInfo = ApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("12345")
        .build();

    return ApiIntraMunicipalRelocation.builder()
        .dossier(ApiDossier.builder()
            .referenceIds(singleton(new ApiReferenceId(referenceId, "description")))
            .startDate(LocalDate.of(2019, Month.JANUARY, 1))
            .build())
        .declarant(ApiDeclarant.builder()
            .bsn("999993653")
            .contactInformation(contactInfo)
            .build())
        .newAddress(ApiMunicipalAddress.builder()
            .street("Dorpstraat")
            .houseNumber(1)
            .houseLetter("A")
            .houseNumberAddition("4")
            .postalCode("1234aa")
            .residence("Koedijk")
            .municipality(ApiMunicipality.builder()
                .code("4567")
                .description("Alkmaar")
                .build())
            .addressFunction(ApiAddressFunctionType.LIVING_ADDRESS)
            .numberOfResidents(2)
            .destinationCurrentResidents("Destination current residents")
            .liveIn(ApiLiveIn.builder()
                .liveInApplicable(true)
                .consent(ApiLiveInConsentType.PENDING)
                .consenter(ApiConsenter.builder()
                    .bsn("999990639")
                    .contactInformation(contactInfo)
                    .build())
                .build())
            .mainOccupant(ApiMainOccupant.builder()
                .bsn("999993653")
                .contactInformation(contactInfo)
                .build())
            .build())
        .relocators(Arrays.asList(
            ApiIntraMunicipalRelocationPerson.builder()
                .declarationType(ApiDeclarationType.AUTHORITY_HOLDER)
                .bsn("2458")
                .build(),
            ApiIntraMunicipalRelocationPerson.builder()
                .declarationType(ApiDeclarationType.AUTHORITY_HOLDER)
                .bsn("3116")
                .build(),
            ApiIntraMunicipalRelocationPerson.builder()
                .declarationType(ApiDeclarationType.REGISTERED)
                .bsn("999993653")
                .contactInformation(contactInfo)
                .build()))
        .build();
  }

  private static ApiIntraMunicipalRelocation getMinRelocation() {
    return ApiIntraMunicipalRelocation.builder()
        .dossier(ApiDossier.builder()
            .startDate(LocalDate.of(2019, Month.JANUARY, 1))
            .build())
        .declarant(ApiDeclarant.builder()
            .bsn("999993653")
            .build())
        .newAddress(ApiMunicipalAddress.builder()
            .houseNumber(1)
            .postalCode("1234AA")
            .numberOfResidents(2)
            .liveIn(ApiLiveIn.builder()
                .liveInApplicable(false)
                .build())
            .build())
        .relocators(Arrays.asList(
            ApiIntraMunicipalRelocationPerson.builder()
                .declarationType(ApiDeclarationType.REGISTERED)
                .bsn("999993653")
                .build(),
            ApiIntraMunicipalRelocationPerson.builder()
                .declarationType(ApiDeclarationType.AUTHORITY_HOLDER)
                .bsn("3116")
                .build(),
            ApiIntraMunicipalRelocationPerson.builder()
                .declarationType(ApiDeclarationType.AUTHORITY_HOLDER)
                .bsn("2458")
                .build()))
        .build();
  }
}
