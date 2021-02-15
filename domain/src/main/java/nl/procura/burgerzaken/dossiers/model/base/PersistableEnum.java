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

package nl.procura.burgerzaken.dossiers.model.base;

import static java.lang.String.format;

import java.util.Arrays;

public interface PersistableEnum<C> {

  C getCode();

  static <T extends PersistableEnum<C>, C> T valueOfCode(T[] values, C code) {
    if (code != null && !"".equals(code)) {
      return Arrays.stream(values)
          .filter(apiType -> apiType.getCode().equals(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(format("No value with code %s", code)));
    }
    return null;
  }
}
