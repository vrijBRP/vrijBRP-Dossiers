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

public class ClientCredentials {

  private final String id;
  private final String secret;

  public ClientCredentials(String id, String secret) {
    this.id = id;
    this.secret = secret;
  }

  public String id() {
    return id;
  }

  public String secret() {
    return secret;
  }

  public static ClientCredentials empty() {
    return new ClientCredentials("", "");
  }

  public static ClientCredentials of(Object principal, Object credentials) {
    if (principal instanceof String && credentials instanceof String) {
      return new ClientCredentials((String) principal, (String) credentials);
    }
    return empty();
  }
}
