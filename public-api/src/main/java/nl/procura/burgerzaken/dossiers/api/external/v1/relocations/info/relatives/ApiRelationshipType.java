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

import nl.procura.burgerzaken.dossiers.model.relocations.info.RelationshipType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelocationRelativesInfoRelationshipType")
public enum ApiRelationshipType {

  REGISTERED(RelationshipType.REGISTERED),
  PARENT(RelationshipType.PARENT),
  PARTNER(RelationshipType.PARTNER),
  EX_PARTNER(RelationshipType.EX_PARTNER),
  CHILD(RelationshipType.CHILD);

  private final RelationshipType type;

  ApiRelationshipType(RelationshipType type) {
    this.type = type;
  }

  public static ApiRelationshipType valueOfType(final RelationshipType type) {
    return Arrays.stream(values())
        .filter(apiType -> apiType.getType() == type)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(format("Illegal value %s", type)));
  }
}
