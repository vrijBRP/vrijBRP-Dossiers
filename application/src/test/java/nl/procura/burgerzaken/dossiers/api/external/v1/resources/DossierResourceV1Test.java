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

package nl.procura.burgerzaken.dossiers.api.external.v1.resources;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.procura.burgerzaken.dossiers.JsonAssertions.assertJsonEquals;
import static nl.procura.burgerzaken.dossiers.api.external.v1.resources.MockTest.documentPrettyPrintReqResp;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_3;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiDatePeriod;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiDateTimePeriod;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiRequestPaging;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossierDocument;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.search.ApiSearchRequest;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.search.ApiSearchResponse;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

import okhttp3.mockwebserver.MockResponse;

@ContextConfiguration(initializers = GbaSource.class)
class DossierResourceV1Test extends BaseResourceTest {

  private static final String URI_SEARCH_DOSSIER = "/api/v1/dossiers/search";

  @Test
  void mustReturnDossiers() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("dossiers.json"));
    ApiSearchRequest searchRequest = ApiSearchRequest
        .builder()
        .entryDateTimePeriod(ApiDateTimePeriod.builder()
            .from(LocalDateTime.now().minusDays(1))
            .to(LocalDateTime.now())
            .build())
        .startDatePeriod(ApiDatePeriod.builder()
            .from(LocalDate.now().minusDays(1))
            .to(LocalDate.now())
            .build())
        .paging(ApiRequestPaging.defaultPaging())
        .bsns(Arrays.asList(BsnUtils.toBsnString(TEST_BSN_3)))
        .dossierIds(Arrays.asList("zaak-1234"))
        .statusses(Arrays.asList(DossierStatus.INCOMPLETE.getCode()))
        .build();

    ApiSearchResponse response = newMockTest()
        .post(URI_SEARCH_DOSSIER, searchRequest)
        .documentation("search_dossier1")
        .status(status().isOk())
        .toClass(ApiSearchResponse.class);

    assertEquals(1L, response.getResult().getTotalElements());
  }

  @Test
  void addDossierDocumentMustReturnCreated() {
    GbaSource.enqueueJsonResponse(getClass().getResource("add-document-gba-response.json"));
    ApiDossierDocument document = document().build();
    // when
    ApiDossierDocument created = newMockTest()
        .post("/api/v1/dossiers/1234-abc-5de/documents", document)
        .documentation("add_document")
        .status(status().isCreated())
        .toClass(ApiDossierDocument.class);
    // then request to front-desk must be correct
    assertJsonEquals(getClass().getResource("add-document-gba-request.json"), GbaSource.takeRequestBody());
    // then response must be correct
    assertFalse(created.getId().isEmpty());
    assertEquals(document.getTitle(), created.getTitle());
    // hmm, given filename is not the same as returned filename
    assertNull(created.getContent());
  }

  @Test
  void addDossierDocumentMustReturnCreatedWhenTitleIsNull() {
    GbaSource.enqueueJsonResponse(getClass().getResource("add-document-gba-response.json"));
    // when, then
    newMockTest()
        .post("/api/v1/dossiers/1234-abc-5de/documents", document().title(null).build())
        .status(status().isCreated());
  }

  @Test
  void addDossierDocumentMustReturnBadRequestWhenRequestContainsNullOrEmptyValues() {
    whenAddDocumentAssertBadRequest(document().filename(null).build(), "filename must not be empty");
    whenAddDocumentAssertBadRequest(document().filename("").build(), "filename must not be empty");
    whenAddDocumentAssertBadRequest(document().content(null).build(), "content must not be empty");
    whenAddDocumentAssertBadRequest(document().content("".getBytes(UTF_8)).build(), "content must not be empty");
  }

  @Test
  void getDocumentsOfDossierMustReturnDocuments() {
    GbaSource.enqueueJsonResponse(getClass().getResource("get-documents-gba-response.json"));
    // when
    List<ApiDossierDocument> documents = newMockTest()
        .get("/api/v1/dossiers/1234-abc-5de/documents")
        .documentation("get_documents")
        .status(status().isOk())
        .toClass(new TypeReference<>() {
        });
    // then 2 documents must be returned in same order as front-desk response
    assertEquals(2, documents.size());
    assertEquals("dXBsb2FkLXRlc3QtMjY0MzQwNTU5NjU0NzU5NDA1", documents.get(0).getId());
    assertEquals("dXBsb2FkLXRlc3QtMjY0MzQwNTU5NjU0NzU5NDA2", documents.get(1).getId());
  }

  @Test
  void getDocumentById() throws Exception {
    String content = "some text";
    GbaSource.enqueue(new MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        .setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.txt\"")
        .setBody(content));
    // when
    String actualContent = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dossiers/1234-abc-5de/documents/testid")
        .headers(apiAccess.authorization()))
        .andDo(documentPrettyPrintReqResp("get_document"))
        // then
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.txt\""))
        .andReturn()
        .getResponse()
        .getContentAsString();
    assertEquals(content, actualContent);
  }

  private void whenAddDocumentAssertBadRequest(ApiDossierDocument document, String message) {
    newMockTest()
        .post("/api/v1/dossiers/1234-abc-5de/documents", document)
        .status(status().isBadRequest())
        .error(message);
  }

  private static ApiDossierDocument.ApiDossierDocumentBuilder<?, ?> document() {
    return ApiDossierDocument.builder()
        .title("A title")
        .filename("document.txt")
        .content("text".getBytes(UTF_8));
  }
}
