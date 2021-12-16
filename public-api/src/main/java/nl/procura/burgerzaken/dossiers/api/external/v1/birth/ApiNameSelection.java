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

package nl.procura.burgerzaken.dossiers.api.external.v1.birth;

import java.util.Optional;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiTitlePredicateType;
import nl.procura.burgerzaken.dossiers.model.base.NameSelection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "BirthNameSelection")
public class ApiNameSelection {

  @Schema(name = "lastname", required = true)
  private String lastname;

  @Schema(name = "prefix")
  private String prefix;

  @Schema(name = "titlePredicate")
  private ApiTitlePredicateType titlePredicate;

  private Optional<ApiTitlePredicateType> titlePredicate() {
    return Optional.ofNullable(titlePredicate);
  }

  public static ApiNameSelection of(NameSelection nameSelection) {
    if (nameSelection != null) {
      return ApiNameSelection.builder()
          .lastname(nameSelection.getLastName())
          .prefix(nameSelection.getPrefix())
          .titlePredicate(ApiTitlePredicateType.valueOfType(nameSelection.getTitle()))
          .build();
    }
    return null;
  }

  public NameSelection toNameSelection() {
    return new NameSelection(getLastname(),
        getPrefix(),
        titlePredicate()
            .map(ApiTitlePredicateType::getType)
            .orElse(null));

  }

}
