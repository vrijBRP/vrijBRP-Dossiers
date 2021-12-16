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

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentPlanning;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentPlanning")
public class ApiPlanning {

  @Schema(name = "commitmentType", required = true)
  @NotNull(message = "commitmentType is mandatory")
  private ApiCommitmentType commitmentType;

  @Schema(name = "commitmentDateTime", required = true)
  @NotNull(message = "commitmentDateTime is mandatory")
  private LocalDateTime commitmentDateTime;

  @Schema(name = "intentionDate")
  private LocalDate intentionDate;

  @Schema(name = "remarks")
  private String remarks;

  public static ApiPlanning of(CommitmentPlanning planning) {
    if (ModelValidation.isValid(planning)) {
      return ApiPlanning.builder()
          .commitmentType(ApiCommitmentType.valueOfType(planning.getCommitmentType()))
          .commitmentDateTime(getCommitmentDateTime(planning))
          .intentionDate(planning.getIntentionDate())
          .remarks(planning.getRemarks())
          .build();
    }
    return null;
  }

  private static LocalDateTime getCommitmentDateTime(CommitmentPlanning planning) {
    if (planning.getCommitmentDate() != null && planning.getCommitmentTime() != null) {
      return LocalDateTime.of(planning.getCommitmentDate(), planning.getCommitmentTime());
    }
    return null;
  }
}
