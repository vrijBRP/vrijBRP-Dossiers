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

package nl.procura.burgerzaken.dossiers.api.external.v1.birth.info;

import static java.util.Optional.ofNullable;

import javax.validation.Valid;

import nl.procura.burgerzaken.dossiers.api.external.v1.birth.ApiPartner;
import nl.procura.burgerzaken.dossiers.model.birth.FamilySituationInfo;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "BirthFamilySituationResponse")
public class ApiFamilySituationInfoResponse {

  @Schema(name = "bornInPartnership")
  private Boolean bornInPartnership;

  @Schema(name = "legalDeceasedPeriodRuleApplicable",
      description = "Is the child born within 301 of the death of the legal partner?")
  private Boolean legalDeceasedPeriodRuleApplicable;

  @Valid
  @Schema(name = "partner",
      description = "The legal partner of the mother")
  private ApiPartner partner;

  public static ApiFamilySituationInfoResponse of(FamilySituationInfo info) {
    return ApiFamilySituationInfoResponse
        .builder()
        .bornInPartnership(info.isBornInPartnership())
        .legalDeceasedPeriodRuleApplicable(info.isLegalDeceasedPeriodRuleApplicable())
        .partner(ofNullable(info.getBsnPartner())
            .map(bsn -> ApiPartner.builder()
                .bsn(BsnUtils.toBsnString(info.getBsnPartner()))
                .build())
            .orElse(null))
        .build();
  }
}
