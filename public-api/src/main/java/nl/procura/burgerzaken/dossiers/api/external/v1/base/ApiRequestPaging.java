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

package nl.procura.burgerzaken.dossiers.api.external.v1.base;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "RequestPaging")
public class ApiRequestPaging {

  private static final Integer DEFAULT_PAGE_SIZE = 10;

  @Schema(description = "Current page number", example = "0", required = true)
  @NotNull(message = "pageNumber is mandatory")
  private Integer pageNumber;

  @Schema(description = "Number of elements requested per page", example = "10", required = true)
  @NotNull(message = "pageSize is mandatory")
  private Integer pageSize;

  public static ApiRequestPaging defaultPaging() {
    return ApiRequestPaging.builder()
        .pageNumber(0)
        .pageSize(DEFAULT_PAGE_SIZE)
        .build();
  }
}
