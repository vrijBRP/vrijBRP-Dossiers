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

import java.util.List;

import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiDeclarationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "RelocationRelativesInfoRelative")
public class ApiRelative {

  @Schema(name = "person")
  private ApiRelativePerson person;

  @Schema(name = "relationshipType")
  private ApiRelationshipType relationshipType;

  @Schema(name = "declarationType")
  private ApiDeclarationType declarationType;

  @Schema(name = "suitableForRelocation")
  private boolean suitableForRelocation;

  @Schema(name = "obstructions")
  private List<ApiRelocationObstructionType> obstructions;
}
