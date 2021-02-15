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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.components.GbaClientConfig;
import nl.procura.gba.web.rest.v2.GbaRestDocumentV2;
import nl.procura.gba.web.rest.v2.model.base.GbaRestAntwoord;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakDocumentToevoegenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakDocument;

public class GbaClientExamples {

  private static final Logger LOGGER = LoggerFactory.getLogger(GbaClientExamples.class);

  private static final String BASE_URL        = "http://localhost:8080/personen/rest";
  private static final String USERNAME        = "1";
  private static final String PASSWORD        = "123456";
  private static final int    TIMEOUT_SECONDS = 5;

  public static void main(String[] args) {
    getZaakByZaakId();
    //    addZaak();
  }

  private static void getZaakByZaakId() {
    GbaClient client = getGbaClient();
    GbaRestAntwoord<GbaRestZaak> zaakByZaakId = client.zaken().getZaakByZaakId("0040-nl1-zj0");
    System.out.println(zaakByZaakId.getInhoud().getAlgemeen().getZaakId());
  }

  private static void addZaak() throws JsonProcessingException {
    show(getGbaClient().zaken().addZaak(new RelocationExampleData().getIntraMunicipalRequest()));
  }

  private static void show(Object obj) throws JsonProcessingException {
    System.out.println(new ObjectMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(obj));
  }

  private static GbaClient getGbaClient() {
    GbaClientConfig config = GbaClientConfig
        .builder()
        .baseUrl(BASE_URL)
        .username(USERNAME)
        .password(PASSWORD)
        .timeoutSeconds(TIMEOUT_SECONDS)
        .build();
    return new GbaClient(config);
  }

  @Test
  public void addDossierDocument() throws IOException {
    String zaakId = "0100-lcw-3rh";
    String inhoud = "some text";

    GbaClientConfig config = GbaClientConfig.builder()
        .baseUrl(BASE_URL)
        .username(USERNAME)
        .password(PASSWORD)
        .timeoutSeconds(TIMEOUT_SECONDS)
        .build();
    GbaRestDocumentV2 client = new GbaClient(config).documenten();
    GbaRestZaakDocumentToevoegenVraag request = new GbaRestZaakDocumentToevoegenVraag();
    GbaRestZaakDocument zaakDocument = new GbaRestZaakDocument();
    zaakDocument.setBestandsnaam("test.txt");
    request.setDocument(zaakDocument);
    request.setInhoud(inhoud.getBytes());
    GbaRestZaakDocument document = client.addDocument(zaakId, request).getInhoud();
    LOGGER.info(document.toString());
    InputStreamResource stream = (InputStreamResource) client.getDocumentById(zaakId, document.getId());
    LOGGER.info("filename: {}", stream.getFilename());
    String actualInhoud = StreamUtils.copyToString(stream.getInputStream(), UTF_8);
    assertEquals(inhoud, actualInhoud);
  }

  @Test
  public void getDocumentsByZaakId() {
    GbaClientConfig config = GbaClientConfig.builder()
        .baseUrl(BASE_URL)
        .username(USERNAME)
        .password(PASSWORD)
        .timeoutSeconds(TIMEOUT_SECONDS)
        .build();
    GbaRestDocumentV2 client = new GbaClient(config).documenten();
    List<GbaRestZaakDocument> documenten = client.getDocumentsByZaakId("0100-lcw-3rh").getInhoud().getDocumenten();
    LOGGER.info(documenten.toString());
  }
}
