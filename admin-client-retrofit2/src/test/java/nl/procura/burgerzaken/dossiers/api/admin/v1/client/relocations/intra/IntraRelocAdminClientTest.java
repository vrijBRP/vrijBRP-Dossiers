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

package nl.procura.burgerzaken.dossiers.api.admin.v1.client.relocations.intra;

import static examples.AdminExampleUtils.toPrettyJson;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import nl.procura.burgerzaken.dossiers.api.admin.v1.client.api.AdminApiDossiersApi;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.api.AdminApiIntraMunicipalRelocationsApi;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.DossierSearchResponse;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.IntraMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.IntraMunicipalRelocationTest;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.relocations.AdminTestBase;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Admin API not important anymore since relocation are add directly to the Front-desk")
public class IntraRelocAdminClientTest extends AdminTestBase {

  private static final String REFERENCE_ID = "1234";

  @Test
  @Order(1)
  @SneakyThrows
  public void canAddDossier() {
    AdminApiIntraMunicipalRelocationsApi api = getApiClient().getApiClient()
        .createService(AdminApiIntraMunicipalRelocationsApi.class);
    Response<IntraMunicipalRelocation> response = api
        .addIntraMunicipalRelocation(IntraMunicipalRelocationTest.createNew()).execute();
    assertTrue(response.isSuccessful());
    IntraMunicipalRelocation relocation = response.body();
    assertNotNull(relocation);
    assert relocation.getDossier() != null;
    assertTrue(StringUtils.isNotBlank(relocation.getDossier().getDossierId()));
  }

  @Test
  @Order(2)
  @SneakyThrows
  public void canFindDossierByReferenceId() {
    String dossierId = getDossierIdByReferenceId();
    AdminApiIntraMunicipalRelocationsApi api = getApiClient().getApiClient()
        .createService(AdminApiIntraMunicipalRelocationsApi.class);
    Response<IntraMunicipalRelocation> response = api.findIntraMunicipalRelocation(dossierId).execute();
    assertTrue(response.isSuccessful());
    log.info(toPrettyJson(response.body()));
  }

  @Test
  @Order(3)
  @SneakyThrows
  public void canDeleteDossier() {
    String dossierId = getDossierIdByReferenceId();
    AdminApiDossiersApi api = getApiClient().getApiClient()
        .createService(AdminApiDossiersApi.class);
    Response<Void> response = api.deleteDossier(dossierId).execute();
    assertTrue(response.isSuccessful());
  }

  private String getDossierIdByReferenceId() throws java.io.IOException {
    AdminApiDossiersApi api = getApiClient().getApiClient().createService(AdminApiDossiersApi.class);
    DossierSearchRequest request = new DossierSearchRequest().dossierIds(singletonList(REFERENCE_ID));
    Response<DossierSearchResponse> response = api.searchDossiers(request).execute();
    assertTrue(response.isSuccessful());
    DossierSearchResponse relocation = response.body();
    assert relocation != null;
    assert relocation.getResult() != null;
    assert relocation.getResult().getContent() != null;
    return relocation.getResult().getContent().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No dossiers found"))
        .getDossierId();
  }
}
