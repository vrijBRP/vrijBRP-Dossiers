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

package nl.procura.burgerzaken.dossiers.api.external.v1.deaths;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiEnum;
import nl.procura.burgerzaken.dossiers.model.deaths.WrittenDeclarantType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "WrittenDeclarantType")
public enum ApiWrittenDeclarantType implements ApiEnum<WrittenDeclarantType> {

  PROSECUTOR(WrittenDeclarantType.PROSECUTOR),
  ASSISTANT_PROSECUTOR(WrittenDeclarantType.ASSISTANT_PROSECUTOR);

  private final WrittenDeclarantType type;

  ApiWrittenDeclarantType(WrittenDeclarantType type) {
    this.type = type;
  }

  public static ApiWrittenDeclarantType valueOfType(final WrittenDeclarantType type) {
    return ApiEnum.valueOfType(values(), type);
  }
}
