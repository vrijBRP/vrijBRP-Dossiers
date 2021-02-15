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

package nl.procura.burgerzaken.dossiers.api.admin.v1.base;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "ResultPaging")
public class AdminApiResultPaging {

  private static final Integer DEFAULT_PAGE_SIZE = 10;

  @Schema(description = "Current page number", example = "0")
  private Integer pageNumber;

  @Schema(description = "Number of elements requested per page", example = "10")
  private Integer pageSize;

  @Schema(description = "Type of sorting")
  private List<AdminApiResultSortType> sort;

  public static AdminApiResultPaging ofDefault(AdminApiResultPaging paging) {
    if (paging == null || paging.getPageSize() == 0) {
      return AdminApiResultPaging.builder()
          .pageNumber(0)
          .pageSize(DEFAULT_PAGE_SIZE)
          .sort(Collections.singletonList(AdminApiResultSortType.ENTRY_DATE_TIME_DESC))
          .build();
    }
    return paging;
  }
}
