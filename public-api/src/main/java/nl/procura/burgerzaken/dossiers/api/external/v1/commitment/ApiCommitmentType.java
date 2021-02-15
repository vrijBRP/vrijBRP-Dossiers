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

package nl.procura.burgerzaken.dossiers.api.external.v1.commitment;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiEnum;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "CommitmentType")
public enum ApiCommitmentType implements ApiEnum<CommitmentType> {

  MARRIAGE(CommitmentType.MARRIAGE),
  GPS(CommitmentType.GPS);

  private final CommitmentType type;

  ApiCommitmentType(CommitmentType type) {
    this.type = type;
  }

  public static ApiCommitmentType valueOfType(final CommitmentType type) {
    return ApiEnum.valueOfType(values(), type);
  }
}
