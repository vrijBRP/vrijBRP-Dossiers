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

package nl.procura.burgerzaken.dossiers.model.commitment;

import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.commons.lang3.ObjectUtils;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CommitmentPlanning implements ModelValidation {

  private CommitmentType commitmentType;
  private LocalDate      commitmentDate;
  private LocalTime      commitmentTime;
  private LocalDate      intentionDate;
  private String         remarks;

  @Override
  public boolean isValid() {
    return ObjectUtils.anyNotNull(commitmentType, commitmentDate, commitmentTime, intentionDate, remarks);
  }
}
