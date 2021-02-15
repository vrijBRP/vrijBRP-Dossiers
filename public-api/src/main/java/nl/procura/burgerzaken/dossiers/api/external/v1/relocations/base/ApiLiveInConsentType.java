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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base;

import static java.lang.String.format;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "RelocationLiveInConsentType",
    description = "* NOT_APPLICABLE\n" +
        "* PENDING\n" +
        "* APPROVED\n" +
        "* DECLINED")
public enum ApiLiveInConsentType {

  NOT_APPLICABLE("", "Not applicable"),
  PENDING("P", "Pending"),
  APPROVED("A", "Approved"),
  DECLINED("D", "Declined");

  private final String code;
  private final String description;

  ApiLiveInConsentType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public static ApiLiveInConsentType valueOfCode(String code) {
    for (ApiLiveInConsentType consent : values()) {
      if (consent.code.equals(code)) {
        return consent;
      }
    }
    throw new IllegalArgumentException(format("Illegal consent with code %s", code));
  }
}
