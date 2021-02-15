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

package nl.procura.burgerzaken.dossiers.model.deaths;

import java.time.LocalDate;
import java.util.List;

import nl.procura.burgerzaken.dossiers.model.base.TableValue;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DeathInMunicipality {

  private Dossier         dossier;
  private Boolean         deathByNaturalCauses;
  private LocalDate       dateOfDeath;
  private String          timeOfDeath;
  private TableValue      municipality;
  private Person          declarant;
  private FuneralServices funeralServices;
  private Deceased        deceased;
  private Correspondence  correspondence;
  private List<Extract>   extracts;

  public DeathInMunicipality(Dossier dossier) {
    this.dossier = dossier;
  }
}
