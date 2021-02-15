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
import nl.procura.burgerzaken.dossiers.api.external.v1.client.api.CommitmentApi;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.Commitment;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.CommitmentData;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class CommitmentExample {

  public static void main(String[] args) throws IOException {

    DossierApiClient client = ExampleUtils.getClient();
    CommitmentApi api = client.getApiClient()
        .createService(CommitmentApi.class);

    Commitment newCommitment = CommitmentData.createNew();
    assert newCommitment.getDossier() != null;
    newCommitment.getDossier().setEntryDateTime(LocalDateTime.now().withNano(0));
    Response<Commitment> response = api.addCommitment(newCommitment).execute();
    Commitment relocation = response.body();
    assert relocation != null;
    assert relocation.getDossier() != null;
    assert relocation.getDossier().getReferenceIds() != null;
  }
}
