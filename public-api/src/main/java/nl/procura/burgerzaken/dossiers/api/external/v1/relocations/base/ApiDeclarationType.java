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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base;

import static java.lang.String.format;

import java.util.Arrays;

import nl.procura.burgerzaken.dossiers.model.relocations.info.DeclarationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelocationDeclarationType")
public enum ApiDeclarationType {

  EX_OFFICIO(DeclarationType.EX_OFFICIO),
  MINISTERIAL_DECISION(DeclarationType.MINISTERIAL_DECISION),
  AUTHORITY_HOLDER(DeclarationType.AUTHORITY_HOLDER),
  HEAD_OF_AN_INSTITUTION(DeclarationType.HEAD_OF_AN_INSTITUTION),
  REGISTERED(DeclarationType.REGISTERED),
  ADULT_CHILD_LIVING_WITH_PARENTS(DeclarationType.ADULT_CHILD_LIVING_WITH_PARENTS),
  ADULT_AUTHORIZED_REPRESENTATIVE(DeclarationType.ADULT_AUTHORIZED_REPRESENTATIVE),
  PARTNER(DeclarationType.PARTNER),
  PARENT_LIVING_WITH_ADULT_CHILD(DeclarationType.PARENT_LIVING_WITH_ADULT_CHILD);

  private DeclarationType type;

  ApiDeclarationType(DeclarationType type) {
    this.type = type;
  }

  public static ApiDeclarationType valueOfType(final DeclarationType type) {
    return Arrays.stream(values())
        .filter(apiType -> apiType.getType() == type)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(format("Illegal value %s", type)));
  }
}
