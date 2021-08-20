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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.function.Consumer;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.components.ApiAccess;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
public class BaseResourceTest {

  protected static final String URI_GET_DOSSIER_BY_ID = "/api/v1/dossiers/{dossierId}";
  protected static final String URI_SEARCH_DOSSIERS   = "/api/v1/dossiers/search";

  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  protected ApiAccess apiAccess;

  @Autowired
  protected EntityManager em;

  @BeforeEach
  public void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(documentationConfiguration(restDocumentation))
        .apply(springSecurity())
        .build();
    apiAccess.apiScope(mockMvc);
  }

  @AfterEach
  public void tearDown() {
    apiAccess.delete();
  }

  protected MockTest newMockTest() {
    return MockTest.builder(mockMvc, apiAccess, mapper);
  }

  protected <T> void mustReturnError(T req, String uri, Consumer<T> change, String errorMessage, String documentation) {
    change.accept(req);
    newMockTest().post(uri, req)
        .documentation(documentation)
        .error(errorMessage);
  }
}
