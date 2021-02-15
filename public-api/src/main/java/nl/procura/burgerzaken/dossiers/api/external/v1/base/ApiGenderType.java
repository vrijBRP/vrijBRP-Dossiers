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

package nl.procura.burgerzaken.dossiers.api.external.v1.base;

import nl.procura.burgerzaken.dossiers.model.base.GenderType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "GenderType")
public enum ApiGenderType implements ApiEnum<GenderType> {

  MAN(GenderType.MAN),
  WOMAN(GenderType.WOMAN),
  UNKNOWN(GenderType.UNKNOWN);

  private GenderType type;

  ApiGenderType(GenderType type) {
    this.type = type;
  }

  public static ApiGenderType valueOfType(final GenderType type) {
    return ApiEnum.valueOfType(values(), type);
  }
}
