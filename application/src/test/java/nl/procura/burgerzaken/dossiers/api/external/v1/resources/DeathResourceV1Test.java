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
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_2;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiCountry;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMunicipality;
import nl.procura.burgerzaken.dossiers.api.external.v1.deaths.*;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiReferenceId;
import nl.procura.burgerzaken.dossiers.model.deaths.CauseOfDeathType;
import nl.procura.burgerzaken.dossiers.model.events.EventType;

@ContextConfiguration(initializers = GbaSource.class)
class DeathResourceV1Test extends BaseResourceTest {

  private static final String DECLARATOR_BSN = String.valueOf(TEST_BSN_2);
  private static final String DECEASED_BSN   = String.valueOf(TEST_BSN_3);

  private static final String URI_ADD_DEATH_IN_MUN       = "/api/v1/deaths/in-municipality";
  private static final String URI_GET_DEATH_IN_MUN_BY_ID = "/api/v1/deaths/in-municipality/{dossierId}";

  private static final String URI_ADD_DISCOVERED_BODY       = "/api/v1/deaths/discovered-body";
  private static final String URI_GET_DISCOVERED_BODY_BY_ID = "/api/v1/deaths/discovered-body/{dossierId}";

  @Test
  void addDeathInMunicipality() {
    GbaSource.enqueueJsonResponse(getClass().getResource("death-in-mun.json"));
    ApiDeathInMunicipality resp = newMockTest()
        .post(URI_ADD_DEATH_IN_MUN, getCompleteDeathInMunicipality())
        .status(status().isCreated())
        .documentation("add_death_in_mun")
        .toClass(ApiDeathInMunicipality.class);
    //    // then request to GBA server must be valid
    String requestBody = GbaSource.takeRequestBody();
    //    // empty zaakId, datumInvoer, tijdInvoer as those are variable
    requestBody = requestBody.replaceFirst("\"datumInvoer\":[ 0-9]*", "\"datumInvoer\":0")
        .replaceFirst("\"tijdInvoer\":[ 0-9]*", "\"tijdInvoer\":0");
    assertJsonEquals(getClass().getResource("deathInMunResourceV1Test-gba-request.json"), requestBody);
    // then resp must be valid
    ApiDossier dossier = resp.getDossier();
    assertEquals("death_in_municipality", dossier.getType().getCode());
    assertEquals("incomplete", dossier.getStatus().getCode());
    String dossierId = dossier.getDossierId();
    assertTrue(StringUtils.isNotBlank(dossierId));
    Set<ApiReferenceId> referenceIds = dossier.getReferenceIds();
    assertEquals(1, referenceIds.size());
    assertEquals("death-1234", referenceIds.iterator().next().getId());
    assertEquals(1, dossier.getReferenceIds().size());

    assertEquals("09:10", resp.getTimeOfDeath());
    assertEquals("13:20", resp.getFuneralServices().getTime());

    // then database must be updated correctly
    eventLogAssertions.assertClientAndType(dossierId, apiAccess.clientId(),
        EventType.DEATH_CREATED);
  }

  @Test
  void addDiscoveredBody() {
    GbaSource.enqueueJsonResponse(getClass().getResource("discovered-body.json"));
    ApiDiscoveredBody resp = newMockTest()
        .post(URI_ADD_DISCOVERED_BODY, getCompleteDiscoveredBody())
        .status(status().isCreated())
        .documentation("add_discovered_body")
        .toClass(ApiDiscoveredBody.class);
    // then request to GBA server must be valid
    String requestBody = GbaSource.takeRequestBody();
    // empty zaakId, datumInvoer, tijdInvoer as those are variable
    requestBody = requestBody.replaceFirst("\"datumInvoer\":[ 0-9]*", "\"datumInvoer\":0")
        .replaceFirst("\"tijdInvoer\":[ 0-9]*", "\"tijdInvoer\":0");
    assertJsonEquals(getClass().getResource("discoveredBodyResourceV1Test-gba-request.json"), requestBody);
    // then resp must be valid
    ApiDossier dossier = resp.getDossier();
    assertEquals("discovered_body", dossier.getType().getCode());
    assertEquals("incomplete", dossier.getStatus().getCode());
    String dossierId = dossier.getDossierId();
    assertTrue(StringUtils.isNotBlank(dossierId));
    Set<ApiReferenceId> referenceIds = dossier.getReferenceIds();
    assertEquals(1, referenceIds.size());
    assertEquals("death-5678", referenceIds.iterator().next().getId());
    assertEquals(1, dossier.getReferenceIds().size());

    assertEquals("09:10", resp.getTimeOfFinding());
    assertEquals("13:20", resp.getFuneralServices().getTime());

    // then database must be updated correctly
    eventLogAssertions.assertClientAndType(dossierId, apiAccess.clientId(),
        EventType.DEATH_CREATED);
  }

  @Test
  void findDeathInMunicipalityById() {
    GbaSource.enqueueJsonResponse(getClass().getResource("death-in-mun.json"));
    String dossierId = "death-1234";
    ApiDeathInMunicipality created = newMockTest()
        .get(URI_GET_DEATH_IN_MUN_BY_ID, dossierId)
        .documentation("search_death_in_mun")
        .status(status().isOk())
        .toClass(ApiDeathInMunicipality.class);
    assertEquals(dossierId, created.getDossier().getDossierId());
    assertEquals(DECLARATOR_BSN, created.getDeclarant().getBsn());
    assertEquals(DECEASED_BSN, created.getDeceased().getBsn());

    assertEquals("09:10", created.getTimeOfDeath());
    assertEquals("13:20", created.getFuneralServices().getTime());
  }

  @Test
  void findDiscoveredBodyById() {
    GbaSource.enqueueJsonResponse(getClass().getResource("discovered-body.json"));
    String dossierId = "death-5678";
    ApiDiscoveredBody created = newMockTest()
        .get(URI_GET_DISCOVERED_BODY_BY_ID, dossierId)
        .documentation("search_discovered_body")
        .status(status().isOk())
        .toClass(ApiDiscoveredBody.class);
    assertEquals(dossierId, created.getDossier().getDossierId());
    assertEquals(DECEASED_BSN, created.getDeceased().getBsn());

    assertEquals("09:10", created.getTimeOfFinding());
    assertEquals("13:20", created.getFuneralServices().getTime());
  }

  @Test
  public void mustReturnError() {
    addDeathMustReturnError(req -> req.setDossier(null), "dossier is mandatory");
    addDeathMustReturnError(req -> req.getDossier().setStartDate(null), "startDate is mandatory");
    addDeathMustReturnError(req -> req.setDeclarant(null), "declarant is mandatory");
    addDeathMustReturnError(req -> req.setDeceased(null), "deceased is mandatory");
    addDeathMustReturnError(req -> req.getExtracts().get(0).setCode(null), "code of extract cannot be empty");
    addDeathMustReturnError(req -> req.getExtracts().get(0).setCode(""), "code of extract cannot be empty");
    addDeathMustReturnError(req -> req.setTimeOfDeath("aa:00"), "time must match HH:mm");
    addDeathMustReturnError(req -> req.setTimeOfDeath("123:00"), "time must match HH:mm");
    addDeathMustReturnError(req -> req.setTimeOfDeath("23:45:22"), "time must match HH:mm");
  }

  @Test
  void discoveredBodyWithoutTimeMustReturnError() {
    ApiDiscoveredBody discoveredBody = getCompleteDiscoveredBody();
    discoveredBody.setTimeOfFinding(null);
    newMockTest()
        .post(URI_ADD_DISCOVERED_BODY, discoveredBody)
        .error("time is mandatory");
  }

  @Test
  void deathInMunicipalityWithoutTimeMustSucceed() {
    // given
    GbaSource.enqueueJsonResponse(getClass().getResource("death-in-mun.json"));
    ApiDeathInMunicipality deathInMunicipality = getCompleteDeathInMunicipality();
    deathInMunicipality.setTimeOfDeath(null);
    // when
    newMockTest()
        .post(URI_ADD_DEATH_IN_MUN, deathInMunicipality)
        // then
        .status(status().isCreated());
  }

  private void addDeathMustReturnError(Consumer<ApiDeathInMunicipality> change, String errorMessage) {
    ApiDeathInMunicipality deathInMunicipality = getCompleteDeathInMunicipality();
    mustReturnError(deathInMunicipality, URI_ADD_DEATH_IN_MUN, change, errorMessage, "");
  }

  private static ApiDeathInMunicipality getCompleteDeathInMunicipality() {
    ApiContactInformation contactInfo = ApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("12345")
        .build();

    return ApiDeathInMunicipality.builder()
        .dossier(ApiDossier.builder()
            .referenceIds(singleton(new ApiReferenceId("death-1234", "External system")))
            .startDate(LocalDate.of(2020, Month.APRIL, 2))
            .build())
        .declarant(ApiDeclarant.builder()
            .bsn(DECLARATOR_BSN)
            .contactInformation(contactInfo)
            .build())
        .deceased(getDeceased(contactInfo))
        .deathByNaturalCauses(true)
        .municipality(ApiMunicipality.builder()
            .code("0637")
            .description("Zoetermeer")
            .build())
        .dateOfDeath(LocalDate.of(2020, 8, 16))
        .timeOfDeath("09:10")
        .funeralServices(getFuneralServices())
        .correspondence(getCorrespondence())
        .extracts(getExtracts())
        .build();
  }

  private static ApiDiscoveredBody getCompleteDiscoveredBody() {
    ApiContactInformation contactInfo = ApiContactInformation.builder()
        .email("burgerzaken@procura.nl")
        .telephoneNumber("12345")
        .build();

    return ApiDiscoveredBody.builder()
        .dossier(ApiDossier.builder()
            .referenceIds(singleton(new ApiReferenceId("discovered-body-1234", "External system")))
            .startDate(LocalDate.of(2020, Month.APRIL, 2))
            .build())
        .writtenDeclarantType(ApiWrittenDeclarantType.PROSECUTOR)
        .explanation("Found at home by the neighbours")
        .deceased(getDeceased(contactInfo))
        .deathByNaturalCauses(true)
        .municipality(ApiMunicipality.builder()
            .code("0637")
            .description("Zoetermeer")
            .build())
        .dateOfFinding(LocalDate.of(2020, 8, 16))
        .timeOfFinding("09:10")
        .funeralServices(getFuneralServices())
        .correspondence(getCorrespondence())
        .extracts(getExtracts())
        .build();
  }

  private static ApiDeceased getDeceased(ApiContactInformation contactInfo) {
    return ApiDeceased.builder()
        .bsn(DECEASED_BSN)
        .lastname("Kaart")
        .firstname("Madonna")
        .prefix("van der")
        .titlePredicate("JV")
        .birthdate(1975_03_07)
        .birthplace("Leidschendam")
        .birthcountry(ApiCountry.builder().code("6030").build())
        .contactInformation(contactInfo)
        .build();
  }

  private static ApiFuneralServices getFuneralServices() {
    return ApiFuneralServices.builder()
        .serviceType(ApiFuneralServiceType.BURIAL_CREMATION)
        .date(LocalDate.of(2020, 8, 20))
        .time("12:20")
        .outsideBenelux(true)
        .causeOfDeathType(CauseOfDeathType.NATURAL_CAUSES)
        .countryOfDestination(ApiCountry.builder()
            .code("8045")
            .description("Antigua en Barbuda").build())
        .placeOfDestination("Timboektoe")
        .via("Het vliegveld")
        .transportation("vliegtuig")
        .build();
  }

  private static ApiCorrespondence getCorrespondence() {
    return ApiCorrespondence.builder()
        .communicationType(ApiCommunicationType.POST)
        .organization("Procura B.V.")
        .departement("Burgerzaken")
        .name("Dhr. F. Janssen")
        .email("test@procura.nl")
        .telephoneNumber("123456")
        .street("Parelweg")
        .houseNumber(1)
        .houseNumberLetter("A")
        .houseNumberAddition("to")
        .postalCode("1812RS")
        .residence("Alkmaar")
        .build();
  }

  private static List<ApiExtract> getExtracts() {
    List<ApiExtract> extracts = new ArrayList<>();
    extracts.add(ApiExtract.builder()
        .code("akt1")
        .description("Akte 1 description")
        .amount(1)
        .build());
    extracts.add(ApiExtract.builder()
        .code("akt2")
        .description("Akte 2 description")
        .amount(2)
        .build());
    return extracts;
  }
}
