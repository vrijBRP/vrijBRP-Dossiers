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

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@Accessors(chain = true)
public class CommitmentWitnesses implements ModelValidation {

  private Integer numberOfMunicipalWitnesses;

  @Setter
  private List<CommitmentWitness> chosenWitnesses = new ArrayList<>();

  @Override
  public boolean isValid() {
    return (numberOfMunicipalWitnesses != null && numberOfMunicipalWitnesses > 0) || !isEmpty(chosenWitnesses);
  }

  public CommitmentWitnesses setNumberOfMunicipalWitnesses(Integer numberOfMunicipalWitnesses) {
    if (numberOfMunicipalWitnesses != null && numberOfMunicipalWitnesses >= 0) {
      this.numberOfMunicipalWitnesses = numberOfMunicipalWitnesses;
    }
    return this;
  }
}
