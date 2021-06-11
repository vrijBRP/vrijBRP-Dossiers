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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static nl.procura.burgerzaken.dossiers.JsonAssertions.assertJsonEquals;
import static nl.procura.burgerzaken.gba.numbers.Bsn.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiGenderType;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiTitlePredicateType;
import nl.procura.burgerzaken.dossiers.api.external.v1.birth.*;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiReferenceId;
import nl.procura.burgerzaken.dossiers.model.events.EventType;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

import okhttp3.mockwebserver.RecordedRequest;

@ContextConfiguration(initializers = GbaSource.class)
class BirthResourceV1Test extends BaseResourceTest {

  private static final String DECLARATOR_BSN        = BsnUtils.toBsnString(TEST_BSN_2);
  private static final String MOTHER_BSN            = BsnUtils.toBsnString(TEST_BSN_3);
  private static final String FATHER_DUO_MOTHER_BSN = BsnUtils.toBsnString(TEST_BSN_4);

  private static final String URI_ADD_BIRTH       = "/api/v1/births";
  private static final String URI_GET_BIRTH_BY_ID = "/api/v1/births/{dossierId}";

  @Test
  void addCompleteBirth() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("birth.json"));
    ApiBirth resp = newMockTest()
        .post(URI_ADD_BIRTH, getCompleteBirth())
        .status(status().isCreated())
        .documentation("add_birth1")
        .toClass(ApiBirth.class);

    // then request to GBA server must be valid
    String requestBody = GbaSource.takeRequestBody();
    // empty zaakId, datumInvoer, tijdInvoer as those are variable
    requestBody = requestBody.replaceFirst("\"datumInvoer\":[ 0-9]*", "\"datumInvoer\":0")
        .replaceFirst("\"tijdInvoer\":[ 0-9]*", "\"tijdInvoer\":0");
    assertJsonEquals(getClass().getResource("birthResourceV1Test-gba-request.json"), requestBody);
    // then resp must be valid
    ApiDossier dossier = resp.getDossier();
    assertEquals("birth", dossier.getType().getCode());
    assertEquals("created", dossier.getStatus().getCode());
    String dossierId = dossier.getDossierId();
    assertTrue(StringUtils.isNotBlank(dossierId));
    Set<ApiReferenceId> referenceIds = dossier.getReferenceIds();
    assertEquals(1, referenceIds.size());
    assertEquals("birth-1234", referenceIds.iterator().next().getId());
    assertEquals(1, dossier.getReferenceIds().size());

    assertEquals("Vries", resp.getNameSelection().getLastname());
    assertEquals("de", resp.getNameSelection().getPrefix());
    assertEquals("B", resp.getNameSelection().getTitlePredicate().getType().getCode());

    // then database must be updated correctly
    eventLogAssertions.assertClientAndType(dossierId, apiAccess.clientId(),
        EventType.BIRTH_CREATED);
  }

  @Test
  void addCompleteBirthWithoutTitleAndPrefix() throws Exception {
    ApiBirth newBirthRequest = getCompleteBirth();
    newBirthRequest.getNameSelection().setPrefix(null);
    newBirthRequest.getNameSelection().setTitlePredicate(null);

    GbaSource.enqueueJsonResponse(getClass().getResource("birth-no-prefix.json"));
    ApiBirth resp = newMockTest()
        .post(URI_ADD_BIRTH, newBirthRequest)
        .status(status().isCreated())
        .toClass(ApiBirth.class);

    // then request to GBA server must be valid
    RecordedRequest request = GbaSource.takeRequest();
    String requestBody = request.getBody().readString(UTF_8);
    // empty zaakId, datumInvoer, tijdInvoer as those are variable
    requestBody = requestBody.replaceFirst("\"datumInvoer\":[ 0-9]*", "\"datumInvoer\":0")
        .replaceFirst("\"tijdInvoer\":[ 0-9]*", "\"tijdInvoer\":0");
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actual = mapper.readTree(requestBody);
    JsonNode expected = mapper.readTree(getClass().getResource("birthResourceV1Test-gba-request-no-prefix.json"));
    assertEquals(expected, actual);

    assertEquals("Vries", resp.getNameSelection().getLastname());
    assertNull(resp.getNameSelection().getPrefix());
    assertNull(resp.getNameSelection().getTitlePredicate());
  }

  @Test
  void findById() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("birth.json"));
    String dossierId = "test-1234";
    ApiBirth created = newMockTest()
        .get(URI_GET_BIRTH_BY_ID, dossierId)
        .documentation("search_birth1")
        .status(status().isOk())
        .toClass(ApiBirth.class);
    assertEquals(dossierId, created.getDossier().getDossierId());
    assertEquals(DECLARATOR_BSN, created.getDeclarant().getBsn());
    assertEquals(MOTHER_BSN, created.getMother().getBsn());
    assertEquals(FATHER_DUO_MOTHER_BSN, created.getFatherDuoMother().getBsn());
    assertEquals(2, created.getChildren().size());

    assertEquals("John", created.getChildren().get(0).getFirstname());
    assertEquals(ApiGenderType.MAN, created.getChildren().get(0).getGender());
    assertEquals("20200403",
        created.getChildren().get(0).getBirthDateTime().format(DateTimeFormatter.ofPattern("yyyMMdd")));
    assertEquals("1112", created.getChildren().get(0).getBirthDateTime().format(DateTimeFormatter.ofPattern("HHmm")));

    assertEquals("Mary", created.getChildren().get(1).getFirstname());
    assertEquals(ApiGenderType.WOMAN, created.getChildren().get(1).getGender());
    assertEquals("20200402",
        created.getChildren().get(1).getBirthDateTime().format(DateTimeFormatter.ofPattern("yyyMMdd")));
    assertEquals("1430", created.getChildren().get(1).getBirthDateTime().format(DateTimeFormatter.ofPattern("HHmm")));

    assertEquals("Vries", created.getNameSelection().getLastname());
    assertEquals("de", created.getNameSelection().getPrefix());
    assertEquals("B", created.getNameSelection().getTitlePredicate().getType().getCode());
  }

  @Test
  public void mustReturnError() {
    mustReturnBirthError(req -> req.setDossier(null), "dossier is mandatory");
    mustReturnBirthError(req -> req.getDossier().setStartDate(null), "startDate is mandatory");
    mustReturnBirthError(req -> req.setDeclarant(null), "declarant is mandatory");
    mustReturnBirthError(req -> req.setChildren(null), "children are mandatory");
    mustReturnBirthError(req -> req.setChildren(new ArrayList<>()), "list of children may not be empty");
  }

  private void mustReturnBirthError(Consumer<ApiBirth> change, String errorMessage) {
    ApiBirth birth = getCompleteBirth();
    mustReturnError(birth, URI_ADD_BIRTH, change, errorMessage, "");
  }

  private static ApiBirth getCompleteBirth() {
    ApiContactInformation contactInfo = ApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("12345")
        .build();

    return ApiBirth.builder()
        .dossier(ApiDossier.builder()
            .referenceIds(singleton(new ApiReferenceId("birth-1234", "External system")))
            .startDate(LocalDate.of(2020, Month.APRIL, 2))
            .build())
        .declarant(ApiDeclarant.builder()
            .bsn(DECLARATOR_BSN)
            .contactInformation(contactInfo)
            .build())
        .mother(ApiMother.builder()
            .bsn(MOTHER_BSN)
            .contactInformation(contactInfo)
            .build())
        .fatherDuoMother(ApiFatherDuoMother.builder()
            .bsn(FATHER_DUO_MOTHER_BSN)
            .contactInformation(contactInfo)
            .build())
        .nameSelection(ApiNameSelection.builder()
            .lastname("Vries")
            .prefix("de")
            .titlePredicate(ApiTitlePredicateType.B)
            .build())
        .children(Arrays.asList(
            ApiChild.builder()
                .firstname("John")
                .gender(ApiGenderType.MAN)
                .birthDateTime(LocalDateTime.of(2020, 4, 3, 11, 12))
                .build(),
            ApiChild.builder()
                .firstname("Mary")
                .gender(ApiGenderType.WOMAN)
                .birthDateTime(LocalDateTime.of(2020, 4, 2, 14, 30))
                .build()))
        .build();
  }
}
