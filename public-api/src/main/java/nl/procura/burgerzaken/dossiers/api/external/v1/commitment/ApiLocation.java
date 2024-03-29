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

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentLocation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentLocation")
public class ApiLocation {

  @Schema(name = "name", description = "Name of the location", required = true)
  @NotEmpty(message = "Name of the location is mandatory")
  private String name;

  @Schema(name = "aliases")
  private List<String> aliases;

  @Schema(name = "options")
  private List<ApiLocationOption> options;

  public static ApiLocation of(CommitmentLocation location) {
    if (ModelValidation.isValid(location)) {
      return ApiLocation.builder()
          .name(location.getName())
          .aliases(location.getAliases())
          .options(location.getOptions().stream()
              .map(ApiLocationOption::of)
              .collect(toList()))
          .build();
    }
    return null;
  }
}
