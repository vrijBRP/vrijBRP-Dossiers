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

package nl.procura.burgerzaken.dossiers.api.external.v1.birth;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.birth.Acknowledgement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UnbornAcknowledgement")
public class ApiUnbornAcknowledgement {

  @Schema(description = "The ID of the dossier", example = "dossier-1234")
  private String dossierId;

  @Schema(type = "string",
      pattern = "^[0-9]{9}$",
      minLength = 9,
      maxLength = 9,
      example = "999990019")
  private String acknowledgerBsn;

  @Valid
  @Schema(name = "nameSelection", required = true)
  @NotNull(message = "nameSelection is mandatory")
  private ApiNameSelection nameSelection;

  public static ApiUnbornAcknowledgement of(Acknowledgement acknowledgement) {
    return builder()
        .dossierId(acknowledgement.getDossier().getCaseNumber())
        .acknowledgerBsn(acknowledgement.getAcknowledger().getBsn().toString())
        .nameSelection(ApiNameSelection.of(acknowledgement.getNameSelection()))
        .build();
  }
}
