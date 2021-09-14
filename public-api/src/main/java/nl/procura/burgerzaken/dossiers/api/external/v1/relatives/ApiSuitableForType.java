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

package nl.procura.burgerzaken.dossiers.api.external.v1.relatives;

import static java.lang.String.format;

import java.util.Arrays;

import nl.procura.burgerzaken.dossiers.model.relatives.SuitableForType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelativesInfoSuitableForType")
public enum ApiSuitableForType {

  GENERAL_USE_CASE(SuitableForType.GENERAL_USE_CASE),
  NEW_RELOCATION_CASE(SuitableForType.NEW_RELOCATION_CASE),
  NEW_BRP_EXTRACT_CASE(SuitableForType.NEW_BRP_EXTRACT_CASE),
  NEW_CONFIDENTIALITY_CASE(SuitableForType.NEW_CONFIDENTIALITY_CASE);

  private SuitableForType type;

  ApiSuitableForType(SuitableForType obstructionType) {
    this.type = obstructionType;
  }

  public static ApiSuitableForType valueOfType(final SuitableForType type) {
    return Arrays.stream(values())
        .filter(apiType -> apiType.getType() == type)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(format("Illegal value %s", type)));
  }
}
