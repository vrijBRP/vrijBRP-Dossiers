/*
 * Copyright 2022 - 2023 Procura B.V.
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

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentWitness;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentWitness")
public class ApiWitness {

  @Schema(description = "BurgerServiceNummer",
      example = "999990019",
      maxLength = 9,
      minLength = 9,
      pattern = "^[0-9]*$")
  private String bsn;

  @Schema(name = "firstname")
  private String firstname;

  @Schema(name = "prefix")
  private String prefix;

  @Schema(name = "lastname")
  private String lastname;

  @Schema(name = "birthdate")
  private Integer birthdate;

  @Schema(name = "remarks")
  private String remarks;

  public static ApiWitness of(CommitmentWitness witness) {
    if (ModelValidation.isValid(witness)) {
      return ApiWitness.builder()
          .bsn(BsnUtils.toBsnString(witness.getBsn()))
          .firstname(witness.getFirstname())
          .lastname(witness.getLastname())
          .birthdate(witness.getBirthdate())
          .prefix(witness.getPrefix())
          .remarks(witness.getRemarks())
          .build();
    }
    return null;
  }
}
