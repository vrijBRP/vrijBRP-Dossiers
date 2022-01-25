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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentLocationOption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentLocationOption")
public class ApiLocationOption {

  @Schema(name = "name")
  private String name;

  @Schema(name = "value", required = true)
  @NotEmpty(message = "location option value is mandatory")
  private String value;

  @Schema(name = "description")
  private String description;

  @Schema(name = "type", required = true)
  @NotNull(message = "location option type is mandatory")
  private ApiCommitmentLocationOptionType type;

  @Schema(name = "aliases")
  private List<String> aliases;

  public static ApiLocationOption of(CommitmentLocationOption option) {
    return ApiLocationOption.builder()
        .name(option.getName())
        .value(option.getValue())
        .description(option.getDescription())
        .type(ApiCommitmentLocationOptionType.valueOfType(option.getType()))
        .aliases(option.getAliases())
        .build();
  }
}
