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

package nl.procura.burgerzaken.dossiers.api.external.v1.birth.info;

import static nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiTitlePredicateType.valueOfType;

import java.util.Optional;

import javax.validation.Valid;

import nl.procura.burgerzaken.dossiers.api.external.v1.birth.ApiNameSelection;
import nl.procura.burgerzaken.dossiers.model.birth.NameSelectionInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "BirthNameSelectionInfoResponse")
public class ApiNameSelectionInfoResponse {

  @Schema(name = "jointChildren",
      description = "Do the mother and father or duomother already have joint children?")
  private Boolean jointChildren;

  @Valid
  @Schema(name = "nameSelection",
      description = "The nameselection based on previous joint children")
  private ApiNameSelection nameSelection;

  public static ApiNameSelectionInfoResponse of(NameSelectionInfo info) {
    return ApiNameSelectionInfoResponse
        .builder()
        .jointChildren(info.isJointChildren())
        .nameSelection(Optional.ofNullable(info.getLastname())
            .map(lastName -> ApiNameSelection
                .builder()
                .lastname(lastName)
                .prefix(info.getPrefix())
                .titlePredicate(valueOfType(info.getTitle()))
                .build())
            .orElse(null))
        .build();
  }
}
