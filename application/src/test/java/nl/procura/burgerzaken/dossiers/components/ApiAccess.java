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

package nl.procura.burgerzaken.dossiers.components;

import static java.util.Collections.singleton;
import static nl.procura.burgerzaken.dossiers.MockMvcUtils.givenCredentials;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import nl.procura.burgerzaken.dossiers.MockMvcUtils;
import nl.procura.burgerzaken.dossiers.oauth.Client;
import nl.procura.burgerzaken.dossiers.oauth.ClientDetailsService;
import nl.procura.burgerzaken.dossiers.oauth.Grants;
import nl.procura.burgerzaken.dossiers.oauth.Scope;
import nl.procura.burgerzaken.dossiers.repositories.client.ClientRepository;

@Component
public class ApiAccess {

  private static final String CLIENT_ID     = "ApiAccess";
  private static final String CLIENT_SECRET = "secret";

  private final ClientDetailsService clientDetailsService;
  private final ClientRepository     clientRepository;

  private Client client;

  private String accessToken;

  public ApiAccess(ClientDetailsService clientDetailsService, ClientRepository clientRepository) {
    this.clientDetailsService = clientDetailsService;
    this.clientRepository = clientRepository;
  }

  public String clientId() {
    return client.clientId();
  }

  public void apiScope(MockMvc mockMvc) {
    tokenRequest(mockMvc, "api");
  }

  public void adminScope(MockMvc mockMvc) {
    tokenRequest(mockMvc, "api");
  }

  public void tokenRequest(MockMvc mockMvc, String scope) {
    client = new Client(CLIENT_ID, CLIENT_SECRET, Scope.of(scope), singleton(Grants.CLIENT_CREDENTIALS));
    clientDetailsService.create(client);
    clientRepository
        .save(new nl.procura.burgerzaken.dossiers.model.client.Client(CLIENT_ID, "test-customer", "test-application"));
    accessToken = givenCredentials(mockMvc, client, scope);
  }

  public void delete() {
    clientDetailsService.delete(client);
  }

  public HttpHeaders authorization() {
    return MockMvcUtils.authorization(accessToken);
  }
}
