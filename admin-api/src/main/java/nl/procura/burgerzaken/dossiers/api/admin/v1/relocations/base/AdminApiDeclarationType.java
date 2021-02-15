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
@Schema(name = "RelocationDeclaration", description = "Declaration type of relocator")
public enum AdminApiDeclarationType {

  AUTHORITY_HOLDER("G"),
  ADULT_CHILD_LIVING_WITH_PARENTS("K"),
  ADULT_AUTHORITY_HOLDER("M"),
  PARTNER("P"),
  PARENT_LIVING_WITH_ADULT_CHILD("O"),
  REGISTERED("I");

  private final String code;

  AdminApiDeclarationType(String code) {
    this.code = code;
  }

  public static AdminApiDeclarationType valueOfCode(String code) {
    for (AdminApiDeclarationType declaration : values()) {
      if (declaration.code.equals(code)) {
        return declaration;
      }
    }
    throw new IllegalArgumentException(format("Illegal declaration with code %s", code));
  }
}
