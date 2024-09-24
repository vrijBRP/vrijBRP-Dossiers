/*
 * Copyright 2023 - 2024 Procura B.V.
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

package nl.procura.burgerzaken.dossiers.api.external.v1.resources;

import static nl.procura.burgerzaken.dossiers.JsonAssertions.assertJsonEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.dataimport.ApiDataImport;
import nl.procura.burgerzaken.dossiers.api.external.v1.dataimport.ApiDataImportRecord;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossierDocument;

import lombok.SneakyThrows;

@ContextConfiguration(initializers = GbaSource.class)
class DataImportResourceV1Test extends BaseResourceTest {

  private static final String URI_ADD_DATAIMPORT = "/api/v1/dataimport/add";

  private static final ObjectMapper mapper = new ObjectMapper();

  @Test
  @SneakyThrows
  void addCompleteDataImport() {
    GbaSource.enqueueJsonResponse(getClass().getResource("dataimport-gba-response.json"));
    ApiDataImport resp = newMockTest()
        .post(URI_ADD_DATAIMPORT, getDataImport())
        .status(status().isCreated())
        .documentation("add_dataimport")
        .toClass(ApiDataImport.class);

    // then request to GBA server must be valid
    String requestBody = GbaSource.takeRequestBody();
    assertJsonEquals(getClass().getResource("dataimport-gba-request.json"), requestBody);
    assertJsonEquals(getClass().getResource("dataimport-response.json"), mapper.writeValueAsString(resp));
  }

  private static ApiDataImport getDataImport() {
    return ApiDataImport.builder()
        .name("Inschrijvers via Internet")
        .type("first_registrants_2022")
        .records(getRecords())
        .build();
  }

  private static List<ApiDossierDocument> getDocuments() {
    List<ApiDossierDocument> documents = new ArrayList<>();

    ApiDossierDocument document1 = new ApiDossierDocument();
    document1.setId(null);
    document1.setTitle("My Title");
    document1.setFilename("test.txt");
    document1.setEntryDateTime(LocalDateTime.now());
    document1.setContent("SGVsbG8NCldvcmxk".getBytes());

    ApiDossierDocument document2 = new ApiDossierDocument();
    document2.setId(null);
    document2.setTitle("My Title2");
    document2.setFilename("test2.txt");
    document2.setEntryDateTime(LocalDateTime.now());
    document2.setContent("SGVsbG8NCldvcmxk".getBytes());

    documents.add(document1);
    documents.add(document2);
    return documents;
  }

  private static List<ApiDataImportRecord> getRecords() {
    ArrayList<ApiDataImportRecord> records = new ArrayList<>();
    ApiDataImportRecord record1 = new ApiDataImportRecord();
    record1.getValues().put("voornamen", "Pietje");
    record1.getValues().put("achternaam", "Puk");
    record1.getValues().put("geboortedatum", "111");
    record1.getValues().put("leeftijd", "11");
    record1.setDocuments(getDocuments());
    records.add(record1);

    ApiDataImportRecord record2 = new ApiDataImportRecord();
    record2.getValues().put("voornamen", "Donald");
    record2.getValues().put("achternaam", "Duck");
    record2.getValues().put("geboortedatum", "01-02-2013");
    record2.getValues().put("leeftijd", "10");
    records.add(record2);

    ApiDataImportRecord record3 = new ApiDataImportRecord();
    record3.getValues().put("voornamen", "Dagobert");
    record3.getValues().put("achternaam", "Duck");
    record3.getValues().put("geboortedatum", "19700405");
    records.add(record3);
    return records;
  }
}
