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

package examples;

import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_1;

import java.io.IOException;

import nl.procura.burgerzaken.dossiers.api.external.v1.client.DossierApiClient;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.api.RelocationsApi;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.RelocationRelativesInfoResponse;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class IntraRelocationRelativesExample {

  public static void main(String[] args) throws IOException {

    DossierApiClient client = ExampleUtils.getClient();
    RelocationsApi api = client.getApiClient()
        .createService(RelocationsApi.class);

    Response<RelocationRelativesInfoResponse> response = api.findRelocationRelatives(String.valueOf(TEST_BSN_1))
        .execute();
    RelocationRelativesInfoResponse relocation = response.body();
    assert relocation != null;
  }
}
