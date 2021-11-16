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

package nl.procura.burgerzaken.dossiers.model.dossier;

import static java.lang.String.format;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum DossierStatusType {

  INCOMPLETE("incomplete", "Incomplete", false),
  CREATED("created", "Created", false),
  PROCESSING("processing", "Processing", false),
  ON_HOLD("on_hold", "On hold", false),
  PROCESSED("processed", "Processed", true),
  CANCELLED("cancelled", "Cancelled", true),
  DELETED("deleted", "Deleted", true),
  REFUSED("refused", "Refused", true),
  UNKNOWN("unknown", "Unknown", false);

  private final String  code;
  private final String  description;
  private final boolean endStatus;

  DossierStatusType(String code, String description, boolean endStatus) {
    this.code = code;
    this.description = description;
    this.endStatus = endStatus;
  }

  public static DossierStatusType valueOfCode(String code) {
    return Arrays.stream(values())
        .filter(v -> v.getCode().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(format("Illegal dossier status with code %s", code)));
  }
}
