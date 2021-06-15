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

import java.util.LinkedHashSet;
import java.util.Set;

import nl.procura.burgerzaken.gba.numbers.Bsn;

import lombok.Data;

@Data
public class Person {

  private Bsn             bsn         = new Bsn(-1L);
  private String          email       = "";
  private String          phoneNumber = "";
  private Set<PersonRole> roles       = new LinkedHashSet<>();

  public Person() {
  }

  public Person(PersonRole role) {
    addRole(role);
  }

  public void addRole(PersonRole role) {
    roles.add(role);
  }

  public boolean containsRole(PersonRole role) {
    return roles.contains(role);
  }

  public void removeRole(PersonRole role) {
    roles.remove(role);
  }

  public boolean hasRoles() {
    return roles != null && !roles.isEmpty();
  }
}
