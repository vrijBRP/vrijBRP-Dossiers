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

import static java.util.stream.Collectors.toList;

import java.util.List;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentOfficials;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentOfficials")
public class ApiOfficials {

  @Schema(name = "preferences")
  private List<ApiOfficial> preferences;

  @Schema(name = "assigned")
  private ApiOfficial assigned;

  public static ApiOfficials of(CommitmentOfficials officials) {
    if (ModelValidation.isValid(officials)) {
      return ApiOfficials.builder()
          .preferences(officials.getPreferedOfficials().stream()
              .map(ApiOfficial::of)
              .collect(toList()))
          .assigned(ApiOfficial.of(officials.getAssignedOfficial()))
          .build();
    }
    return null;
  }
}
