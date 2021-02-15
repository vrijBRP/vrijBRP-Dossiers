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

package nl.procura.burgerzaken.dossiers.api.admin.v1.client.dossier;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import nl.procura.burgerzaken.dossiers.api.admin.v1.client.api.AdminApiDossiersApi;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.api.AdminApiIntraMunicipalRelocationsApi;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.*;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.Error;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.relocations.AdminTestBase;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.repositories.dossier.DossierRepository;
import nl.procura.burgerzaken.dossiers.util.Constants;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Admin API not important anymore since relocation are add directly to the Front-desk")
public class DossierAdminClientTest extends AdminTestBase {

  private static final String REFERENCE_ID = "1234";

  @Autowired
  private DossierRepository dossierRepository;

  @BeforeEach
  public void setUp() {
    deleteAllDossiers();
    addDossier();
  }

  private void deleteAllDossiers() {
    dossierRepository.deleteAll();
    Assertions.assertEquals(0, dossierRepository.findAll().size());
    log.info("All dossiers deleted");
  }

  @Test
  @SneakyThrows
  public void canFindDossierByReferenceId() {
    String dossierId = dossierRepository.findAll().stream().findFirst()
        .orElseThrow(IllegalArgumentException::new).getCaseNumber();
    findDossiers(1, new DossierSearchRequest().dossierIds(singletonList(REFERENCE_ID)));
    findDossiers(1, new DossierSearchRequest().dossierIds(singletonList(dossierId)));
    findDossiers(0, new DossierSearchRequest().dossierIds(singletonList("WRONG REF")));
  }

  @Test
  @SneakyThrows
  public void canFindDossiersByBsn() {
    findDossiers(1, new DossierSearchRequest().bsns(singletonList("999993653")));
    findDossiers(0, new DossierSearchRequest().bsns(singletonList("123456789")));
  }

  @Test
  @SneakyThrows
  public void canFindDossiersByStatus() {
    String correctStatus = String.valueOf(DossierStatus.CREATED.getCode());
    String incorrectStatus = String.valueOf(DossierStatus.CANCELLED.getCode());
    findDossiers(1, new DossierSearchRequest().statusses(singletonList(correctStatus)));
    findDossiers(0, new DossierSearchRequest().statusses(singletonList(incorrectStatus)));
  }

  @Test
  @SneakyThrows
  public void canFindDossiersByType() {
    String correctStatus = String.valueOf(DossierType.INTRA_MUNICIPAL_RELOCATION.getCode());
    String incorrectStatus = String.valueOf(DossierType.INTER_MUNICIPAL_RELOCATION.getCode());
    findDossiers(1, new DossierSearchRequest().types(singletonList(correctStatus)));
    findDossiers(0, new DossierSearchRequest().types(singletonList(incorrectStatus)));
  }

  @Test
  @SneakyThrows
  public void canFindDossiersByStartDate() {
    LocalDate date = LocalDate.of(2019, 1, 1);
    LocalDate dateBefore = date.minusDays(1);
    LocalDate dateAfter = date.plusDays(1);

    DatePeriod correct1 = new DatePeriod().from(date).to(date);
    DatePeriod correct2 = new DatePeriod().from(dateBefore);
    DatePeriod correct3 = new DatePeriod().to(dateAfter);

    DatePeriod incorrect1 = new DatePeriod().from(dateBefore).to(dateBefore);
    DatePeriod incorrect2 = new DatePeriod().from(dateAfter).to(dateAfter);
    DatePeriod incorrect3 = new DatePeriod().from(dateAfter);
    DatePeriod incorrect4 = new DatePeriod().to(dateBefore);

    findDossiers(1, new DossierSearchRequest().startDatePeriod(correct1));
    findDossiers(1, new DossierSearchRequest().startDatePeriod(correct2));
    findDossiers(1, new DossierSearchRequest().startDatePeriod(correct3));
    findDossiers(0, new DossierSearchRequest().startDatePeriod(incorrect1));
    findDossiers(0, new DossierSearchRequest().startDatePeriod(incorrect2));
    findDossiers(0, new DossierSearchRequest().startDatePeriod(incorrect3));
    findDossiers(0, new DossierSearchRequest().startDatePeriod(incorrect4));
  }

  @Test
  @SneakyThrows
  public void canFindDossiersByEntryDate() {
    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);
    LocalDateTime dateBefore = startOfDay.minusDays(1);
    LocalDateTime dateAfter = startOfDay.plusDays(1);

    DateTimePeriod correct1 = new DateTimePeriod().from(startOfDay).to(endOfDay);
    DateTimePeriod correct2 = new DateTimePeriod().from(dateBefore);
    DateTimePeriod correct3 = new DateTimePeriod().to(dateAfter);

    DateTimePeriod incorrect1 = new DateTimePeriod().from(dateBefore).to(dateBefore);
    DateTimePeriod incorrect2 = new DateTimePeriod().from(dateAfter).to(dateAfter);
    DateTimePeriod incorrect3 = new DateTimePeriod().from(dateAfter);
    DateTimePeriod incorrect4 = new DateTimePeriod().to(dateBefore);

    findDossiers(1, new DossierSearchRequest().entryDateTimePeriod(correct1));
    findDossiers(1, new DossierSearchRequest().entryDateTimePeriod(correct2));
    findDossiers(1, new DossierSearchRequest().entryDateTimePeriod(correct3));
    findDossiers(0, new DossierSearchRequest().entryDateTimePeriod(incorrect1));
    findDossiers(0, new DossierSearchRequest().entryDateTimePeriod(incorrect2));
    findDossiers(0, new DossierSearchRequest().entryDateTimePeriod(incorrect3));
    findDossiers(0, new DossierSearchRequest().entryDateTimePeriod(incorrect4));
  }

  @Test
  @SneakyThrows
  public void canParseError() {
    AdminApiDossiersApi api = getApiClient().getApiClient().createService(AdminApiDossiersApi.class);
    Response<DossierSearchResponse> response = api.searchDossiers(new DossierSearchRequest()).execute();
    Error error = getApiClient().parseError(response.errorBody());
    Assertions.assertEquals(Constants.Errors.BAD_REQUEST, error.getCode());
  }

  private void findDossiers(int expectedSize, DossierSearchRequest request) throws java.io.IOException {
    AdminApiDossiersApi api = getApiClient().getApiClient().createService(AdminApiDossiersApi.class);
    Response<DossierSearchResponse> response = api.searchDossiers(request).execute();
    assertTrue(response.isSuccessful());
    DossierSearchResponse relocation = response.body();
    assert relocation != null;
    assert relocation.getResult() != null;
    assert relocation.getResult().getContent() != null;
    Assertions.assertEquals(expectedSize, relocation.getResult().getContent().size());
  }

  @SneakyThrows
  private void addDossier() {
    AdminApiIntraMunicipalRelocationsApi api = getApiClient().getApiClient()
        .createService(AdminApiIntraMunicipalRelocationsApi.class);
    assertTrue(api.addIntraMunicipalRelocation(IntraMunicipalRelocationTest.createNew())
        .execute().isSuccessful());
  }
}
