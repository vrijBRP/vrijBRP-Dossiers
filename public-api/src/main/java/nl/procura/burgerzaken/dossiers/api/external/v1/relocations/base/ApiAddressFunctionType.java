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

import static java.lang.String.format;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelocationAddressFunctionType",
    description = "* LIVING_ADDRESS (W)\n" +
        "* MAILING_ADDRESS (B)")
public enum ApiAddressFunctionType {

  LIVING_ADDRESS("W", "Living address"),
  MAILING_ADDRESS("B", "Mailing address");

  private final String code;
  private final String description;

  ApiAddressFunctionType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public static ApiAddressFunctionType valueOfCode(String code) {
    for (ApiAddressFunctionType function : values()) {
      if (function.code.equals(code)) {
        return function;
      }
    }
    throw new IllegalArgumentException(format("Illegal address function with code %s", code));
  }
}
