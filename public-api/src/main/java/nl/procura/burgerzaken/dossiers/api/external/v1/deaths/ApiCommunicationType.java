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
import nl.procura.burgerzaken.dossiers.model.deaths.CommunicationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "CommunicationType")
public enum ApiCommunicationType implements ApiEnum<CommunicationType> {

  EMAIL(CommunicationType.EMAIL),
  POST(CommunicationType.POST);

  private final CommunicationType type;

  ApiCommunicationType(CommunicationType type) {
    this.type = type;
  }

  public static ApiCommunicationType valueOfType(final CommunicationType type) {
    return ApiEnum.valueOfType(values(), type);
  }
}
