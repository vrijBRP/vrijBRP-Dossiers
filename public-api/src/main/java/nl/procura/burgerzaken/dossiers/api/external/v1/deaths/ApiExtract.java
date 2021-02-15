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

package nl.procura.burgerzaken.dossiers.api.external.v1.deaths;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.deaths.Extract;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Extract")
public class ApiExtract {

  @Schema(name = "code")
  @NotEmpty(message = "code of extract cannot be empty")
  private String code;

  @Schema(name = "description")
  private String description;

  @Schema(name = "amount")
  @NotNull(message = "amount of extract is mandatory")
  private Integer amount;

  public Extract toExtract() {
    Extract extract = new Extract();
    extract.setCode(code);
    extract.setDescription(description);
    extract.setAmount(amount);
    return extract;
  }

  public static ApiExtract of(Extract extract) {
    if (ModelValidation.isValid(extract)) {
      return ApiExtract.builder()
          .code(extract.getCode())
          .description(extract.getDescription())
          .amount(extract.getAmount())
          .build();
    }
    return null;
  }
}
