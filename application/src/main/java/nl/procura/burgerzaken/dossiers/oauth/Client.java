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

import static java.util.Collections.unmodifiableSet;

import java.util.Set;

public class Client {

  private final String      clientId;
  private final Set<Scope>  scopes;
  private final Set<Grants> grants;
  private final String      rawSecret;

  public Client(String clientId, String rawSecret, Set<Scope> scopes, Set<Grants> grants) {
    this.clientId = clientId;
    this.scopes = unmodifiableSet(scopes);
    this.grants = unmodifiableSet(grants);
    this.rawSecret = rawSecret;
  }

  public String clientId() {
    return clientId;
  }

  public Set<Scope> scopes() {
    return scopes;
  }

  public Set<Grants> grants() {
    return grants;
  }

  public String rawSecret() {
    return rawSecret;
  }
}
