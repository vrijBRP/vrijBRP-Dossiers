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
import java.util.List;

import lombok.Getter;

@Getter
public enum DossierType {

  INTRA_MUNICIPAL_RELOCATION("intra_mun_relocation", "Intra-municipal relocation"),
  INTER_MUNICIPAL_RELOCATION("inter_mun_relocation", "Inter-municipal relocation"),
  EMIGRATION("emigration", "Emigration"),
  RESETTLEMENT("resettlement", "Resettlement"),
  BIRTH("birth", "Birth"),
  CONFIDENTIALITY("confidentiality", "Confidentiality"),
  COMMITMENT("commitment", "Commitment"),
  DEATH_IN_MUNICIPALITY("death_in_municipality", "Death in municipality"),
  DISCOVERED_BODY("discovered_body", "Discovered body"),
  ACKNOWLEDGEMENT("acknowledgement", "Acknowledgement");

  private final String code;
  private final String description;

  DossierType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public boolean matches(DossierType... types) {
    return List.of(types).contains(this);
  }

  public static DossierType valueOfCode(String code) {
    return Arrays.stream(values())
        .filter(v -> v.getCode().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(format("Illegal dossier type with code %s", code)));
  }
}
