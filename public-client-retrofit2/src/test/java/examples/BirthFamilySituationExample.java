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
import java.time.LocalDate;
import java.time.Month;

import nl.procura.burgerzaken.dossiers.api.external.v1.client.DossierApiClient;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.api.BirthApi;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.BirthFamilySituationResponse;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class BirthFamilySituationExample {

  public static void main(String[] args) throws IOException {

    DossierApiClient client = ExampleUtils.getClient();
    BirthApi api = client.getApiClient()
        .createService(BirthApi.class);

    Response<BirthFamilySituationResponse> response = api.getFamilySituation(
        LocalDate.of(2016, Month.FEBRUARY, 1),
        "999990019").execute();
    BirthFamilySituationResponse info = response.body();
    assert info != null;
  }
}
