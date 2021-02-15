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

import java.io.IOException;
import java.time.LocalDateTime;

import nl.procura.burgerzaken.dossiers.api.external.v1.client.DossierApiClient;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.api.DossiersApi;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.DateTimePeriod;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.DossierSearchResponse;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.RequestPaging;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class DossiersExample {

  public static void main(String[] args) throws IOException {

    DossierApiClient client = ExampleUtils.getClient();
    DossiersApi api = client.getApiClient()
        .createService(DossiersApi.class);

    DossierSearchRequest request = new DossierSearchRequest();
    request.entryDateTimePeriod(new DateTimePeriod()
        .from(LocalDateTime.now().minusDays(10))
        .to(LocalDateTime.now()))
        .paging(new RequestPaging()
            .pageNumber(0)
            .pageSize(10));

    Response<DossierSearchResponse> response = api.searchDossiers(request).execute();
    DossierSearchResponse searchResponse = response.body();
    assert searchResponse != null;
  }
}
