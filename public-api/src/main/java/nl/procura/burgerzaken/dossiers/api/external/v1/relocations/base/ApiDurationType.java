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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelocationDurationType",
    description = "* LONGER_THAN_PRESCRIBED_PERIOD (L)\n" +
        "* SHORTER_THAN_PRESCRIBED_PERIOD (S)\n")
public enum ApiDurationType {

  LONGER_THAN_PRESCRIBED_PERIOD("L", "Longer than prescribed period"),
  SHORTER_THAN_PRESCRIBED_PERIOD("S", "Shorter than prescribed period");

  private final String code;
  private final String description;

  ApiDurationType(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
