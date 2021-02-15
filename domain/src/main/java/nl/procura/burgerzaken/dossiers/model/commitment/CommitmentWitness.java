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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@Accessors(chain = true)
public class CommitmentWitness implements ModelValidation {

  @Setter
  private String  bsn;
  @Setter
  private String  firstname;
  @Setter
  private String  lastname;
  @Setter
  private String  prefix;
  @Setter
  private String  remarks;
  private Integer birthdate;

  @Override
  public boolean isValid() {
    return isNotBlank(bsn) || (isNotBlank(lastname) && birthdate != null && birthdate > -1);
  }

  public CommitmentWitness setBirthdate(Integer birthdate) {
    if (birthdate != null && birthdate >= 0) {
      this.birthdate = birthdate;
    }
    return this;
  }
}
