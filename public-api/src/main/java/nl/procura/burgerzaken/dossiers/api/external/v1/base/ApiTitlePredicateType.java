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

import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "TitlePredicateType")
public enum ApiTitlePredicateType implements ApiEnum<TitlePredicateType> {

  B(TitlePredicateType.B),
  BS(TitlePredicateType.BS),
  G(TitlePredicateType.G),
  GI(TitlePredicateType.GI),
  H(TitlePredicateType.H),
  HI(TitlePredicateType.HI),
  JH(TitlePredicateType.JH),
  JV(TitlePredicateType.JV),
  M(TitlePredicateType.M),
  MI(TitlePredicateType.MI),
  P(TitlePredicateType.P),
  PS(TitlePredicateType.PS),
  R(TitlePredicateType.R);

  private final TitlePredicateType type;

  ApiTitlePredicateType(TitlePredicateType type) {
    this.type = type;
  }

  public static ApiTitlePredicateType valueOfType(final TitlePredicateType type) {
    return ApiEnum.valueOfType(values(), type);
  }
}
