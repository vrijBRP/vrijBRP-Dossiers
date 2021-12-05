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
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

import nl.procura.burgerzaken.dossiers.api.external.v1.client.DossierApiClient;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.api.DossiersApi;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.DateTimePeriod;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.model.RequestPaging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddDocumentExample {

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

    InputStream resource = AddDocumentExample.class.getClassLoader().getResourceAsStream("test2.pdf");
    System.out.println(resource);
    byte[] src = IOUtils.toByteArray(resource);
    String content = Base64.getEncoder().encodeToString(src);
    System.out.println(content);
    //    String dossierId = "0040-vzj-on3";
    //    DossierDocument dossierDocument = new DossierDocument();
    //    dossierDocument.setContent(Collections.singletonList(content.getBytes(StandardCharsets.UTF_8)));
    //    dossierDocument.setFilename("test.text");
    //    dossierDocument.setEntryDateTime(LocalDateTime.now().withNano(0));
    //    dossierDocument.setId("1");
    //    dossierDocument.setTitle("PDF document");
    //    Response<DossierDocument> response = api.addDocumentToDossier(dossierId, dossierDocument).execute();
    //    DossierDocument searchResponse = response.body();
    //    assert searchResponse != null;
  }
}
