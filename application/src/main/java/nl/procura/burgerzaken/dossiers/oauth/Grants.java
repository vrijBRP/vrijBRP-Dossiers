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
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public enum Grants {

  CLIENT_CREDENTIALS;

  public static Set<Grants> of(String grants) {
    if (grants == null) {
      return emptySet();
    }
    Set<Grants> result = new HashSet<>();
    for (String grant : StringUtils.split(grants, ' ')) {
      ofSingle(grant).ifPresent(result::add);
    }
    return result;
  }

  public static Optional<Grants> ofSingle(String grant) {
    try {
      return Optional.of(Grants.valueOf(grant.toUpperCase(Locale.ENGLISH)));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
