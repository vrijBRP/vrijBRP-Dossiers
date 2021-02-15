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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class Scope {

  private final String code;

  public Scope(String code) {
    this.code = code;
  }

  public String scope() {
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Scope scope1 = (Scope) o;
    return Objects.equals(code, scope1.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  public static Set<Scope> of(String scopes) {
    if (scopes == null) {
      return emptySet();
    }
    Set<Scope> result = new HashSet<>();
    for (String scope : StringUtils.split(scopes, ' ')) {
      result.add(new Scope(scope));
    }
    return result;
  }

  public static String toString(Set<Scope> scopes) {
    return scopes.stream()
        .map(Scope::scope)
        .collect(Collectors.joining(" "));
  }
}
