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

import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentNameUse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentNameUse")
public class ApiNameAfterCommitment {

  @Schema(name = "nameUseType", required = true)
  @NotNull(message = "nameUseType is mandatory")
  private ApiNameUseType nameUseType;

  @Schema(name = "title")
  private String title;

  @Schema(name = "prefix")
  private String prefix;

  @Schema(name = "lastname", required = true)
  @NotNull(message = "lastname is mandatory")
  private String lastname;

  public CommitmentNameUse toCommitmentNameUse() {
    return new CommitmentNameUse()
        .setTitle(title)
        .setPrefix(prefix)
        .setLastname(lastname)
        .setType(nameUseType.getType());
  }

  public static ApiNameAfterCommitment of(CommitmentNameUse nameUse) {
    if (ModelValidation.isValid(nameUse)) {
      return ApiNameAfterCommitment.builder()
          .nameUseType(ApiNameUseType.valueOfType(nameUse.getType()))
          .title(nameUse.getTitle())
          .prefix(nameUse.getPrefix())
          .lastname(nameUse.getLastname())
          .build();
    }
    return null;
  }
}
