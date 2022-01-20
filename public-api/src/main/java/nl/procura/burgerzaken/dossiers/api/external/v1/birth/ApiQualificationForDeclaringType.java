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

package nl.procura.burgerzaken.dossiers.api.external.v1.birth;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiEnum;
import nl.procura.burgerzaken.dossiers.model.birth.QualificationForDeclaringType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "QualificationForDeclaringType")
public enum ApiQualificationForDeclaringType implements ApiEnum<QualificationForDeclaringType> {

  UNDERTAKER(QualificationForDeclaringType.UNDERTAKER),
  FATHER(QualificationForDeclaringType.FATHER),
  DUO_MOTHER(QualificationForDeclaringType.DUO_MOTHER),
  PRESENT_AT_BIRTH(QualificationForDeclaringType.PRESENT_AT_BIRTH),
  BORN_IN_HOME(QualificationForDeclaringType.BORN_IN_HOME),
  BORN_IN_INSTITUTION(QualificationForDeclaringType.BORN_IN_INSTITUTION),
  MOTHER(QualificationForDeclaringType.MOTHER),
  MAYOR(QualificationForDeclaringType.FATHER),
  KNOWLEDGE_CARRIER(QualificationForDeclaringType.KNOWLEDGE_CARRIER);

  private final QualificationForDeclaringType type;

  ApiQualificationForDeclaringType(QualificationForDeclaringType type) {
    this.type = type;
  }

  public static ApiQualificationForDeclaringType valueOfType(final QualificationForDeclaringType type) {
    return ApiEnum.valueOfType(values(), type);
  }
}
