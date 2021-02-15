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

package nl.procura.burgerzaken.dossiers.model.birth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nl.procura.burgerzaken.dossiers.model.base.NameSelection;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Birth {

  private Dossier dossier;

  private NameSelection nameSelection;

  private final List<BirthChild> children = new ArrayList<>();

  public Birth(Dossier dossier) {
    this.dossier = dossier;
  }

  public void addChild(BirthChild child) {
    children.add(child);
  }

  public Optional<Person> getDeclarant() {
    return dossier.getPersonByRole(PersonType.DECLARANT);
  }

  public void setDeclarant(Person person) {
    dossier.setPersonByRole(person);
  }

  public Optional<Person> getMother() {
    return dossier.getPersonByRole(PersonType.MOTHER);
  }

  public void setMother(Person person) {
    dossier.setPersonByRole(person);
  }

  public Optional<Person> getFatherDuoMother() {
    return dossier.getPersonByRole(PersonType.FATHER_DUO_MOTHER);
  }

  public void setFatherDuoMother(Person person) {
    dossier.setPersonByRole(person);
  }
}
