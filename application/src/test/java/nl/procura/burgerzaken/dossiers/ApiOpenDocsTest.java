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

package nl.procura.burgerzaken.dossiers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class ApiOpenDocsTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void apiDocsCanBeDownloaded() throws Exception {
    download("target/admin-api-docs.json", "/public/v3/api-docs/admin-API-v1.0");
    download("target/public-api-docs.json", "/public/v3/api-docs/public-API-v1.0");
  }

  @Test
  void operationIdMustNotContainUnderscore() throws Exception {
    mustNotContainerUnderscores("/public/v3/api-docs/admin-API-v1.0");
    mustNotContainerUnderscores("/public/v3/api-docs/public-API-v1.0");
  }

  @Test
  void operationIdsMustMatch() throws Exception {
    operationsMustMatch("/public/v3/api-docs/admin-API-v1.0",
        "getInfo");

    operationsMustMatch("/public/v3/api-docs/public-API-v1.0",
        "searchTasks",
        "searchDossiers",
        "addInterMunicipalRelocation", "findInterMunicipalRelocation", "addInterMunicipalRelocationConsent",
        "addIntraMunicipalRelocation", "findIntraMunicipalRelocation", "addIntraMunicipalRelocationConsent",
        "findRelatives",
        "findRelocationRelatives",
        "addDataimport",
        "findBirth", "addBirth", "getNameSelection", "getFamilySituation", "getAcknowledgement",
        "addDocumentToDossier", "getDocumentsOfDossier", "getDossierDocument",
        "findCommitment", "addCommitment", "updateCommitment", "cancelCommitment",
        "findDeathInMunicipality", "addDeathInMunicipality", "findDiscoveredBody", "addDiscoveredBody");
  }

  private void download(String filePath, String uri) throws Exception {
    File file = new File(filePath);
    FileUtils.writeByteArrayToFile(file,
        mockMvc.perform(get(uri))
            .andReturn()
            .getResponse()
            .getContentAsString().getBytes());

    log.info(String.format("Public API-docs written to %s.", file.getAbsolutePath()));
  }

  private void mustNotContainerUnderscores(String uri) throws Exception {
    mockMvc.perform(get(uri))
        .andExpect(jsonPath("$..operationId", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$..operationId", not(hasItem(containsString("_")))));
  }

  private void operationsMustMatch(String uri, String... expected) throws Exception {
    String content = mockMvc.perform(get(uri))
        .andReturn()
        .getResponse()
        .getContentAsString();
    Collection<String> operationIds = JsonPath.read(content, "$..operationId");
    assertEquals(Set.of(expected), Set.of(operationIds.toArray()));
  }
}
