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

package nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.base;

import static java.lang.String.format;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelocationAddressFunctionType", description = "Function of address")
public enum AdminApiAddressFunctionType {

  LIVING_ADDRESS("W"),
  MAILING_ADDRESS("B");

  private final String code;

  AdminApiAddressFunctionType(String code) {
    this.code = code;
  }

  public static AdminApiAddressFunctionType valueOfCode(String code) {
    for (AdminApiAddressFunctionType function : values()) {
      if (function.code.equals(code)) {
        return function;
      }
    }
    throw new IllegalArgumentException(format("Illegal address function with code %s", code));
  }
}
