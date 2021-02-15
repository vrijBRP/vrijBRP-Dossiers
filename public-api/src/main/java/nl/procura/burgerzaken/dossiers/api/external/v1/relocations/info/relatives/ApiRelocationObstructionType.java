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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.info.relatives;

import static java.lang.String.format;

import java.util.Arrays;

import nl.procura.burgerzaken.dossiers.model.relocations.info.RelocationObstructionType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelocationRelativesInfoObstructionType")
public enum ApiRelocationObstructionType {

  EXISTING_RELOCATION(RelocationObstructionType.EXISTING_RELOCATION),
  DIFFERENT_ADDRESS(RelocationObstructionType.DIFFERENT_ADDRESS),
  MULTIPLE_PERSON_RECORDS_FOUND(RelocationObstructionType.MULTIPLE_PERSON_RECORDS_FOUND),
  NO_PERSON_RECORD_FOUND(RelocationObstructionType.NO_PERSON_RECORD_FOUND),
  RELATIONSHIP_HAS_ENDED(RelocationObstructionType.RELATIONSHIP_HAS_ENDED),
  PERSON_IS_DECEASED(RelocationObstructionType.PERSON_IS_DECEASED),
  PERSON_IS_EMIGRATED(RelocationObstructionType.PERSON_IS_EMIGRATED),
  PERSON_RECORD_IS_BLOCKED(RelocationObstructionType.PERSON_RECORD_IS_BLOCKED),
  PERSON_RECORD_IS_SUSPENDED(RelocationObstructionType.PERSON_RECORD_IS_SUSPENDED),
  PERSON_HAS_CURATOR(RelocationObstructionType.PERSON_HAS_CURATOR);

  private RelocationObstructionType type;

  ApiRelocationObstructionType(RelocationObstructionType obstructionType) {
    this.type = obstructionType;
  }

  public static ApiRelocationObstructionType valueOfType(final RelocationObstructionType type) {
    return Arrays.stream(values())
        .filter(apiType -> apiType.getType() == type)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(format("Illegal value %s", type)));
  }
}
