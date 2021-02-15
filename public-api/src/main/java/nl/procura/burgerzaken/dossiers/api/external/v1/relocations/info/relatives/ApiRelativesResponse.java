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
import java.util.stream.Collectors;

import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiDeclarationType;
import nl.procura.burgerzaken.dossiers.model.relocations.info.RelocationRelative;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "RelocationRelativesInfoResponse")
public class ApiRelativesResponse {

  @Schema(name = "relatives")
  private List<ApiRelative> relatives;

  public static ApiRelativesResponse of(List<RelocationRelative> relatives) {
    return ApiRelativesResponse
        .builder()
        .relatives(relatives.stream().map(r -> ApiRelative.builder()
            .person(ApiRelativePerson.builder()
                .bsn(r.getBsn())
                .build())
            .relationshipType(ApiRelationshipType.valueOfType(r.getRelationshipType()))
            .declarationType(r.getDeclarationType().map(ApiDeclarationType::valueOfType).orElse(null))
            .suitableForRelocation(r.getObstructions().isEmpty())
            .obstructions(r.getObstructions().stream()
                .map(ApiRelocationObstructionType::valueOfType)
                .collect(Collectors.toList()))
            .build()).collect(Collectors.toList()))
        .build();
  }
}
