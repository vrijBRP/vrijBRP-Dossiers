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

import static java.util.Collections.singleton;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailsService {

  private static final int RANDOM_PASSWORD_LENGTH = 32;

  private final OauthClientDetailsRepository repository;
  private final PasswordEncoder              passwordEncoder;

  public ClientDetailsService(OauthClientDetailsRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  public static Client newClientCredentialsClient(String id, Set<Scope> scopes) {
    return new Client(id, randomAlphanumeric(RANDOM_PASSWORD_LENGTH), scopes,
        singleton(Grants.CLIENT_CREDENTIALS));
  }

  public boolean create(Client client) {
    return repository.create(client, passwordEncoder.encode(client.rawSecret()));
  }

  public void delete(Client client) {
    repository.deleteById(client.clientId());
  }

  public void createOrRegeneratePassword(Client client) {
    repository.findById(client.clientId())
        .ifPresentOrElse(c -> regeneratePassword(client), () -> create(client));
  }

  private void regeneratePassword(Client client) {
    OauthClientDetails clientDetails = new OauthClientDetails(client, passwordEncoder.encode(client.rawSecret()));
    repository.save(clientDetails);
  }

  public Optional<Client> validateCredentials(ClientCredentials credentials) {
    return repository.findById(credentials.id())
        .filter(client -> passwordEncoder.matches(credentials.secret(), client.clientSecret()))
        .map(OauthClientDetails::toClient);
  }

}
