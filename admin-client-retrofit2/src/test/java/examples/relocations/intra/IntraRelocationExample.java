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

package examples.relocations.intra;

import java.time.LocalDateTime;
import java.util.List;

import nl.procura.burgerzaken.dossiers.api.admin.v1.client.DossierAdminApiClient;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.api.AdminApiIntraMunicipalRelocationsApi;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.DossierReferenceId;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.IntraMunicipalRelocation;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.model.IntraMunicipalRelocationTest;

import examples.AdminExampleUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class IntraRelocationExample {

  @SneakyThrows
  public static void main(String[] args) {

    DossierAdminApiClient client = AdminExampleUtils.getClient();
    AdminApiIntraMunicipalRelocationsApi api = client.getApiClient()
        .createService(AdminApiIntraMunicipalRelocationsApi.class);

    add(api);
  }

  private static List<DossierReferenceId> add(AdminApiIntraMunicipalRelocationsApi api) throws java.io.IOException {
    IntraMunicipalRelocation newRelocation = IntraMunicipalRelocationTest.createNew();
    assert newRelocation.getDossier() != null;
    newRelocation.getDossier().setEntryDateTime(LocalDateTime.now().withNano(0));
    Response<IntraMunicipalRelocation> response = api.addIntraMunicipalRelocation(newRelocation).execute();
    IntraMunicipalRelocation relocation = response.body();
    assert relocation != null;
    assert relocation.getDossier() != null;
    return relocation.getDossier().getReferenceIds();
  }
}
