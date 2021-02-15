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

package nl.procura.burgerzaken.dossiers.api.external.v1.task.search;

import java.util.List;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiRequestPaging;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.ApiTaskStatus;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.ApiTaskType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "TaskSearchRequest")
public class ApiSearchRequest {

  private List<String> dossierIds;

  @Schema(description = "BurgerServiceNummer",
      example = "999990019",
      maxLength = 9,
      minLength = 9,
      pattern = "^[0-9]*$")
  private List<String> bsns;

  private List<ApiTaskType> types;

  private List<ApiTaskStatus> statusses;

  private ApiRequestPaging paging;
}
