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

package nl.procura.burgerzaken.dossiers.api.admin.v1.client.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.dossiers.api.admin.v1.client.AdminTestBase;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.api.AdminApiSupportApi;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.Info;

import lombok.SneakyThrows;
import retrofit2.Response;

class SupportResourceAdminClientTest extends AdminTestBase {

  @Test
  @SneakyThrows
  public void canGetVersionInfo() {
    AdminApiSupportApi api = getApiClient().getApiClient().createService(AdminApiSupportApi.class);
    Response<Info> response = api.getInfo().execute();
    assertTrue(response.isSuccessful());
    Info info = response.body();
    assertNotNull(info);
    assertTrue(StringUtils.isNotBlank(info.getVersion()));
    assertTrue(StringUtils.isNotBlank(info.getBuildTime()));
  }
}
