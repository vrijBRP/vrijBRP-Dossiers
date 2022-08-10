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

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentWitnesses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentWitnesses")
public class ApiWitnesses {

  @Schema(name = "numberOfMunicipalWitnesses", required = true)
  @NotNull(message = "numberOfMunicipalWitnesses is mandatory")
  private Integer numberOfMunicipalWitnesses;

  @Valid
  @Schema(name = "chosen")
  private List<ApiWitness> chosen;

  public static ApiWitnesses of(CommitmentWitnesses witnesses) {
    if (ModelValidation.isValid(witnesses)) {
      return ApiWitnesses.builder()
          .numberOfMunicipalWitnesses(witnesses.getNumberOfMunicipalWitnesses())
          .chosen(witnesses.getChosenWitnesses().stream()
              .map(ApiWitness::of)
              .collect(Collectors.toList()))
          .build();
    }
    return null;
  }
}
