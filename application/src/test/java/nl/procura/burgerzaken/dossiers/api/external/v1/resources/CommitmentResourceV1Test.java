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
import static java.util.Collections.singletonList;
import static nl.procura.burgerzaken.dossiers.JsonAssertions.assertJsonEquals;
import static nl.procura.burgerzaken.gba.numbers.Bsn.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMessage;
import nl.procura.burgerzaken.dossiers.api.external.v1.commitment.*;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiReferenceId;
import nl.procura.burgerzaken.dossiers.model.events.EventType;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

@ContextConfiguration(initializers = GbaSource.class)
class CommitmentResourceV1Test extends BaseResourceTest {

  private static final String PARTNER1_BSN = BsnUtils.toBsnString(TEST_BSN_2);;
  private static final String PARTNER2_BSN = BsnUtils.toBsnString(TEST_BSN_3);;
  private static final String WITNESS_BSN  = BsnUtils.toBsnString(TEST_BSN_4);;

  private static final String URI_ADD_COMMITMENT       = "/api/v1/commitments";
  private static final String URI_GET_COMMITMENT_BY_ID = "/api/v1/commitments/{dossierId}";
  private static final String URI_UPDATE_COMMITMENT    = "/api/v1/commitments/{dossierId}";
  private static final String URI_CANCEL_COMMITMENT    = "/api/v1/commitments/marriage-1234/cancel";

  @Test
  void addCompleteCommitment() {
    GbaSource.enqueueJsonResponse(getClass().getResource("commitment.json"));
    ApiCommitment resp = newMockTest()
        .post(URI_ADD_COMMITMENT, getCompleteCommitment())
        .status(status().isCreated())
        .documentation("add_commitment1")
        .toClass(ApiCommitment.class);

    // then request to GBA server must be valid
    String requestBody = GbaSource.takeRequestBody();
    // empty zaakId, datumInvoer, tijdInvoer as those are variable
    requestBody = requestBody.replaceFirst("\"datumInvoer\":[ 0-9]*", "\"datumInvoer\":0")
        .replaceFirst("\"tijdInvoer\":[ 0-9]*", "\"tijdInvoer\":0");
    assertJsonEquals(getClass().getResource("commitmentResourceV1Test-gba-request.json"), requestBody);
    // then resp must be valid
    ApiDossier dossier = resp.getDossier();
    assertEquals("commitment", dossier.getType().getCode());
    assertEquals("incomplete", dossier.getStatus().getCode());
    String dossierId = dossier.getDossierId();
    assertTrue(StringUtils.isNotBlank(dossierId));
    Set<ApiReferenceId> referenceIds = dossier.getReferenceIds();
    assertEquals(1, referenceIds.size());
    assertEquals("commitment-1234", referenceIds.iterator().next().getId());
    assertEquals(1, dossier.getReferenceIds().size());
    assertEquals("Het Generaalshuis (Theater aan het Vrijthof)", resp.getLocation().getName());
    assertEquals(2, resp.getLocation().getAliases().size());
    assertEquals(3, resp.getLocation().getOptions().size());
    assertEquals(ApiCommitmentType.MARRIAGE, resp.getPlanning().getCommitmentType());
    assertEquals(LocalDateTime.of(2020, 9, 10, 11, 12), resp.getPlanning().getCommitmentDateTime());

    // then database must be updated correctly
    eventLogAssertions.assertClientAndType(dossierId, apiAccess.clientId(),
        EventType.COMMITMENT_CREATED);
  }

  @Test
  void updateCompleteCommitment() {
    GbaSource.enqueueJsonResponse(getClass().getResource("commitment.json"));
    String dossierId = "commitment-1234";
    ApiCommitment commitment = getCompleteCommitment();
    commitment.getDossier().setReferenceIds(null);
    ApiCommitment resp = newMockTest()
        .post(URI_UPDATE_COMMITMENT, commitment, dossierId)
        .status(status().isOk())
        .documentation("update_commitment1")
        .toClass(ApiCommitment.class);
    // then resp must be valid
    ApiDossier dossier = resp.getDossier();
    assertEquals("commitment", dossier.getType().getCode());
    assertEquals("incomplete", dossier.getStatus().getCode());
    String returnedDossierId = dossier.getDossierId();
    assertTrue(StringUtils.isNotBlank(returnedDossierId));
    Set<ApiReferenceId> referenceIds = dossier.getReferenceIds();
    assertEquals(1, referenceIds.size());
    assertEquals("commitment-1234", referenceIds.iterator().next().getId());
    assertEquals(1, dossier.getReferenceIds().size());
    assertEquals("Het Generaalshuis (Theater aan het Vrijthof)", resp.getLocation().getName());
    assertEquals(2, resp.getLocation().getAliases().size());
    assertEquals(3, resp.getLocation().getOptions().size());
    assertEquals(ApiCommitmentType.MARRIAGE, resp.getPlanning().getCommitmentType());
    assertEquals(LocalDateTime.of(2020, 9, 10, 11, 12), resp.getPlanning().getCommitmentDateTime());

    // then database must be updated correctly
    eventLogAssertions.assertClientAndType(returnedDossierId, apiAccess.clientId(),
        EventType.COMMITMENT_UPDATED);
  }

  @Test
  void findCompleteById() {
    GbaSource.enqueueJsonResponse(getClass().getResource("commitment.json"));
    String dossierId = "marriage-1234";
    ApiCommitment created = newMockTest()
        .get(URI_GET_COMMITMENT_BY_ID, dossierId)
        .documentation("search_commitment1")
        .status(status().isOk())
        .toClass(ApiCommitment.class);
    assertEquals(dossierId, created.getDossier().getDossierId());

    assertEquals(PARTNER1_BSN, created.getPartner1().getBsn());
    assertEquals(PARTNER2_BSN, created.getPartner2().getBsn());
    assertEquals(1, created.getOfficials().getPreferences().size());
    assertEquals(2, created.getWitnesses().getChosen().size());
  }

  @Test
  void findBasicById() {
    GbaSource.enqueueJsonResponse(getClass().getResource("commitment-basic.json"));
    String dossierId = "marriage-1234";
    ApiCommitment created = newMockTest()
        .get(URI_GET_COMMITMENT_BY_ID, dossierId)
        .status(status().isOk())
        .toClass(ApiCommitment.class);
    assertEquals(dossierId, created.getDossier().getDossierId());

    assertEquals(PARTNER1_BSN, created.getPartner1().getBsn());
    assertEquals(PARTNER2_BSN, created.getPartner2().getBsn());
    assertNull(created.getOfficials());
    assertNull(created.getWitnesses());
    assertNull(created.getLocation());
  }

  @Test
  public void mustReturnError() {
    mustReturnCommitmentError(req -> req.setDossier(null), "dossier is mandatory");
    mustReturnCommitmentError(req -> req.getDossier().setStartDate(null), "startDate is mandatory");
    mustReturnCommitmentError(req -> req.getPlanning().setCommitmentType(null), "commitmentType is mandatory");
    mustReturnCommitmentError(req -> req.getPlanning().setCommitmentDateTime(null), "commitmentDateTime is mandatory");
    mustReturnCommitmentError(req -> {
      req.getWitnesses().getChosen().get(0).setBsn(null);
      req.getWitnesses().getChosen().get(0).setFirstname(null);
    }, "First name of the witness is required");

    mustReturnCommitmentError(req -> {
      req.getWitnesses().getChosen().get(0).setBsn(null);
      req.getWitnesses().getChosen().get(0).setLastname(null);
    }, "Last name of the witness is required");

    mustReturnCommitmentError(req -> {
      req.getWitnesses().getChosen().get(0).setBsn(null);
      req.getWitnesses().getChosen().get(0).setBirthdate(null);
    }, "Birthdate of the witness is required");
  }

  @Test
  void cancelCommitmentWithoutMessageMustReturnNoContent() {
    GbaSource.enqueueJsonResponse(getClass().getResource("cancel-commitment-gba-response.json"));
    newMockTest()
        .post(URI_CANCEL_COMMITMENT, null)
        .status(status().isNoContent());
    // then
    assertJsonEquals(getClass().getResource("cancel-commitment-no-message-gba-request.json"),
        GbaSource.takeRequestBody());
  }

  @Test
  void cancelCommitmentWithNullMessageMustReturnBadRequest() {
    newMockTest()
        .post(URI_CANCEL_COMMITMENT, new ApiMessage())
        .status(status().isBadRequest());
  }

  @Test
  void cancelCommitmentWithMessageMustReturnNoContent() {
    GbaSource.enqueueJsonResponse(getClass().getResource("cancel-commitment-gba-response.json"));
    newMockTest()
        .post(URI_CANCEL_COMMITMENT, new ApiMessage("cancel message"))
        .documentation("cancel_commitment")
        .status(status().isNoContent());
    // then
    assertJsonEquals(getClass().getResource("cancel-commitment-with-message-gba-request.json"),
        GbaSource.takeRequestBody());
  }

  //
  private void mustReturnCommitmentError(Consumer<ApiCommitment> change, String errorMessage) {
    ApiCommitment commitment = getCompleteCommitment();
    mustReturnError(commitment, URI_ADD_COMMITMENT, change, errorMessage, "");
  }

  private static ApiCommitment getCompleteCommitment() {
    ApiContactInformation contactInfo1 = ApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("06-54 32 23 45")
        .build();

    ApiContactInformation contactInfo2 = ApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("0887708100")
        .build();

    return ApiCommitment.builder()
        .dossier(ApiDossier.builder()
            .referenceIds(singleton(new ApiReferenceId("commitment-1234", "External system")))
            .startDate(LocalDate.of(2020, Month.APRIL, 2))
            .build())
        .partner1(ApiPartner.builder()
            .bsn(PARTNER1_BSN)
            .contactInformation(contactInfo1)
            .nameAfterCommitment(ApiNameAfterCommitment.builder()
                .nameUseType(ApiNameUseType.N)
                .lastname("Vries")
                .prefix("de")
                .title("B")
                .build())
            .build())
        .partner2(ApiPartner.builder()
            .bsn(PARTNER2_BSN)
            .contactInformation(contactInfo2)
            .nameAfterCommitment(ApiNameAfterCommitment.builder()
                .nameUseType(ApiNameUseType.V)
                .lastname("Jansen")
                .prefix(null)
                .title("JH")
                .build())
            .build())
        .planning(ApiPlanning.builder()
            .commitmentDateTime(LocalDateTime.of(2020, Month.APRIL, 2, 11, 12))
            .commitmentType(ApiCommitmentType.MARRIAGE)
            .intentionDate(LocalDate.of(2020, Month.JANUARY, 2))
            .remarks("Nice wedding!")
            .build())
        .location(ApiLocation.builder()
            .name("Het Generaalshuis (Theater aan het Vrijthof)")
            .aliases(singletonList("generaalshuis"))
            .options(Arrays
                .asList(
                    ApiLocationOption.builder()
                        .name("Taalceremonie")
                        .value("Spaans")
                        .description("Taalceremonie")
                        .type(ApiCommitmentLocationOptionType.TEXT)
                        .aliases(singletonList("taal_ceremonie")).build(),
                    ApiLocationOption.builder()
                        .name("Max. aantal personen")
                        .value("75")
                        .description("Max. aantal personen (1 - 250)")
                        .type(ApiCommitmentLocationOptionType.NUMBER)
                        .aliases(singletonList("taal_ceremonie")).build(),
                    ApiLocationOption.builder()
                        .name("Trouwboekje/Partnerschapsboekje")
                        .value("true")
                        .description("Trouwboekje/Partnerschapsboekje")
                        .type(ApiCommitmentLocationOptionType.BOOLEAN)
                        .aliases(singletonList("trouwboekje")).build()))
            .build())
        .officials(ApiOfficials.builder()
            .preferences(singletonList(ApiOfficial.builder()
                .name("Pietje precies")
                .contactInformation(contactInfo1)
                .aliases(singletonList("pietje"))
                .build()))
            .build())
        .witnesses(ApiWitnesses.builder()
            .numberOfMunicipalWitnesses(2)
            .chosen(Arrays.asList(
                ApiWitness.builder()
                    .bsn(null)
                    .firstname("Truus")
                    .prefix("de")
                    .lastname("Vries")
                    .birthdate(1990_01_01)
                    .remarks("First witness!")
                    .build(),
                ApiWitness.builder()
                    .bsn(WITNESS_BSN)
                    .firstname("Klaas")
                    .prefix(null)
                    .lastname("Jansen")
                    .birthdate(1980_05_03)
                    .remarks("Second witness!")
                    .build()))
            .build())
        .build();
  }
}
