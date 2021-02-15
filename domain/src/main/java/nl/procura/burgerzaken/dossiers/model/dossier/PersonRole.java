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

import java.io.Serializable;

import javax.persistence.*;

import nl.procura.burgerzaken.dossiers.util.DatabaseFieldNotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "person_role")
public class PersonRole implements Serializable {

  @EmbeddedId
  private PersonRoleId id;

  @ManyToOne
  @MapsId("personId")
  @JoinColumn(name = "person_id", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @DatabaseFieldNotNull
  private Person person;

  public PersonRole(Person person, String roleCode) {
    id = new PersonRoleId(roleCode);
    this.person = person;
  }

  public PersonType personType() {
    return PersonType.valueOfCode(getId().getRole());
  }
}
