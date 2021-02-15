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

package nl.procura.burgerzaken.dossiers.oauth;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static nl.procura.burgerzaken.dossiers.MockMvcUtils.authorization;
import static nl.procura.burgerzaken.dossiers.MockMvcUtils.givenCredentials;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import nl.procura.burgerzaken.dossiers.GbaSource;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = GbaSource.class)
class TokenControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ClientDetailsService clientDetailsService;

  private Client adminClient;
  private Client apiClient;

  private final List<Client> createdClients = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    adminClient = createClient(
        new Client("admin", "secret", Scope.of("admin api"), singleton(Grants.CLIENT_CREDENTIALS)));
    apiClient = createClient(new Client("api", "secret", Scope.of("api"), singleton(Grants.CLIENT_CREDENTIALS)));
  }

  @AfterEach
  public void tearDown() {
    createdClients.forEach(clientDetailsService::delete);
  }

  @Test
  void noGrantTypeMustReturnInvalidRequest() throws Exception {
    mockMvc.perform(post("/oauth/token"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid_request")))
        .andExpect(jsonPath("$.error_description").exists())
        .andExpect(jsonPath("$.error_uri").doesNotHaveJsonPath());
  }

  @Test
  void wrongGrantTypeMustReturnUnsupportedGrantType() throws Exception {
    mockMvc.perform(post("/oauth/token?grant_type=wrong"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("unsupported_grant_type")))
        .andExpect(jsonPath("$.error_description").doesNotHaveJsonPath())
        .andExpect(jsonPath("$.error_uri").doesNotHaveJsonPath());
  }

  @Test
  void noAuthorizationHeaderMustReturnInvalidClient() throws Exception {
    mockMvc.perform(post("/oauth/token?grant_type=client_credentials"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error", is("invalid_client")))
        .andExpect(jsonPath("$.error_description").doesNotHaveJsonPath())
        .andExpect(jsonPath("$.error_uri").doesNotHaveJsonPath());
  }

  @Test
  void invalidSecretMustReturnInvalidClient() throws Exception {
    mockMvc.perform(post("/oauth/token?grant_type=client_credentials&scope=api")
        .headers(authorization(apiClient.clientId(), "wrong")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error", is("invalid_client")))
        .andExpect(jsonPath("$.error_description").doesNotHaveJsonPath())
        .andExpect(jsonPath("$.error_uri").doesNotHaveJsonPath());
  }

  @Test
  void unknownScopeMustReturnInvalidScope() throws Exception {
    mockMvc.perform(post("/oauth/token?grant_type=client_credentials&scope=unknown")
        .headers(authorization(apiClient.clientId(), apiClient.rawSecret())))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error", is("invalid_scope")))
        .andExpect(jsonPath("$.error_description").doesNotHaveJsonPath())
        .andExpect(jsonPath("$.error_uri").doesNotHaveJsonPath());
  }

  @Test
  void apiScopeMustGrantAccessToApiResource() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("TokenControllerTest-empty.json"));
    String accessToken = givenCredentials(mockMvc, apiClient, "api");
    mockMvc.perform(
        post("/api/v1/dossiers/search")
            .headers(authorization(accessToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"types\":[\"intra_mun_relocation\"]}"))
        .andExpect(status().isOk());
  }

  @Test
  void apiScopeMustDenyAccessToAdminResource() throws Exception {
    String accessToken = givenCredentials(mockMvc, apiClient, "api");
    mockMvc.perform(
        get("/admin/api/v1/relocations/intra/nonexistent")
            .headers(authorization(accessToken)))
        .andExpect(status().isForbidden());
  }

  /**
   * For now you must provides all scopes. We might introduce scopes which includes other scopes.
   */
  @Test
  void apiAndAdminScopeMustGrantAccessToApiResource() throws Exception {
    GbaSource.enqueueJsonResponse(getClass().getResource("TokenControllerTest-empty.json"));
    String accessToken = givenCredentials(mockMvc, adminClient, "api admin");
    mockMvc.perform(
        post("/api/v1/dossiers/search")
            .headers(authorization(accessToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"types\":[\"intra_mun_relocation\"]}"))
        .andExpect(status().isOk());
  }

  @Test
  void adminScopeGrantAccessToAdminResource() throws Exception {
    String accessToken = givenCredentials(mockMvc, adminClient, "admin");
    mockMvc.perform(get("/admin/api/v1/support/info").headers(authorization(accessToken)))
        .andExpect(status().isOk());
  }

  @Test
  void emptyAuthorizedGrantTypeMustReturnInvalidClient() throws Exception {
    Client client = createClient(new Client("empty", "secret", Scope.of("api"), emptySet()));
    mockMvc.perform(post("/oauth/token?grant_type=client_credentials&scope=api")
        .headers(authorization(client.clientId(), client.rawSecret())))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error", is("invalid_client")))
        .andExpect(jsonPath("$.error_description").doesNotHaveJsonPath())
        .andExpect(jsonPath("$.error_uri").doesNotHaveJsonPath());

  }

  private Client createClient(Client client) {
    clientDetailsService.create(client);
    createdClients.add(client);
    return client;
  }
}
