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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.consent;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiConsenter;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiLiveInConsentType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "RelocationConsent")
public class ApiConsent {

  @Schema(required = true,
      description = "The ID of the dossier",
      example = "123e4567-e89b-12d3-a456-556642440000")
  private String dossierId;

  @Valid
  @Schema(required = true)
  @NotNull(message = "consenter is mandatory")
  private ApiConsenter consenter;

  @Valid
  @Schema(required = true,
      description = "Consent of live in",
      example = "APPROVED")
  private ApiLiveInConsentType consent;
}
