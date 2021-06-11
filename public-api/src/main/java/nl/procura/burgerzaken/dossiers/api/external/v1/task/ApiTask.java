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

package nl.procura.burgerzaken.dossiers.api.external.v1.task;

import static java.util.Optional.ofNullable;

import java.util.List;

import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossierType;
import nl.procura.burgerzaken.dossiers.model.task.Task;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Task")
public class ApiTask {

  @Schema(description = "BurgerServiceNummer",
      example = "999990019",
      maxLength = 9,
      minLength = 9,
      pattern = "^[0-9]*$")
  private List<String> bsn;

  @Schema(description = "The ID of the dossier",
      example = "dossier-1234")
  private String dossierId;

  private ApiDossierType dossierType;

  private ApiTaskType taskType;

  private ApiTaskStatus status;

  public static ApiTask of(Task d) {
    return ApiTask
        .builder()
        .bsn(BsnUtils.toBsnStringList(d.getBsns()))
        .dossierId(d.getDossierId())
        .dossierType(ofNullable(d.getDossierType())
            .map(dt -> ApiDossierType.builder()
                .code(dt.getCode())
                .description(dt.getDescription())
                .build())
            .orElse(null))
        .taskType(ApiTaskType.builder()
            .code(d.getTaskType().getCode())
            .description(d.getTaskType().getDescription())
            .build())
        .status(ApiTaskStatus.builder()
            .code(d.getStatus().getCode())
            .description(d.getStatus().getDescription())
            .build())
        .build();
  }
}
