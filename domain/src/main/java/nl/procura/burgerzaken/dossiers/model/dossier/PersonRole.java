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

package nl.procura.burgerzaken.dossiers.model.dossier;

import static java.lang.String.format;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum PersonRole {

  RELOCATOR("relocator"),
  DECLARANT("declarant"),
  CONSENTER("consenter"),
  MAIN_OCCUPANT("main_occupant"),
  MOTHER("mother"),
  PARTNER("partner"),
  FATHER_DUO_MOTHER("father_duo_mother"),
  CHILD("child"),
  ACKNOWLEDGER("acknowledger"),
  DECEASED("deceased");

  private final String code;

  PersonRole(String code) {
    this.code = code;
  }

  public static PersonRole valueOfCode(String code) {
    return Arrays.stream(values())
        .filter(v -> v.getCode().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(format("Illegal person type with code %s", code)));
  }
}
