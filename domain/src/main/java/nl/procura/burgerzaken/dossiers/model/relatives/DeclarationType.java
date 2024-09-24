/*
 * Copyright 2022 - 2023 Procura B.V.
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

package nl.procura.burgerzaken.dossiers.model.relatives;

import static java.lang.String.format;

import lombok.Getter;

@Getter
public enum DeclarationType {

  EX_OFFICIO("A", "Ex officio"),
  MINISTERIAL_DECISION("B", "Ministerial decision"),
  AUTHORITY_HOLDER("G", "Authority holder"),
  HEAD_OF_AN_INSTITUTION("H", "Head of an institution"),
  REGISTERED("I", "Registered"),
  ADULT_CHILD_LIVING_WITH_PARENTS("K", "Adult child living with parents"),
  ADULT_AUTHORIZED_REPRESENTATIVE("M", "Adult authorized representative"),
  PARTNER("P", "Partner"),
  PARENT_LIVING_WITH_ADULT_CHILD("O", "Parent living with adult child");

  private final String code;
  private final String description;

  DeclarationType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public static DeclarationType valueOfCode(String code) {
    for (DeclarationType declaration : values()) {
      if (declaration.code.equals(code)) {
        return declaration;
      }
    }
    throw new IllegalArgumentException(format("Illegal declaration with code %s", code));
  }
}
