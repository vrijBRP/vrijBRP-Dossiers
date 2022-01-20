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

package nl.procura.burgerzaken.dossiers.model.birth;

import nl.procura.burgerzaken.dossiers.model.base.PersistableEnum;

import lombok.Getter;

@Getter
public enum QualificationForDeclaringType implements PersistableEnum<String> {

  UNDERTAKER("B", "Begrafenisondernemer"),
  FATHER("V", "Vader"),
  DUO_MOTHER("D", "Duo-moeder"),
  PRESENT_AT_BIRTH("A", "Aanwezig bij geboorte"),
  BORN_IN_HOME("W", "Geboorte in mijn woning"),
  BORN_IN_INSTITUTION("I", "Geboorte in mijn instelling"),
  MOTHER("M", "Moeder"),
  MAYOR("X", "Burgemeester"),
  KNOWLEDGE_CARRIER("K", "Kennisdrager");

  private final String code;
  private final String description;

  QualificationForDeclaringType(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
