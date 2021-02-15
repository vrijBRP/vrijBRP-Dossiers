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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import nl.procura.burgerzaken.dossiers.util.DatabaseFieldNotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "person_id")
  private Long personId;

  @Column(name = "bsn")
  @DatabaseFieldNotNull
  private Long bsn;

  @Column(name = "email")
  @DatabaseFieldNotNull
  private String email;

  @Column(name = "tel")
  @DatabaseFieldNotNull
  private String phoneNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "doss_id", nullable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Dossier dossier;

  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<PersonRole> roles;

  public Person() {
  }

  public Person(Dossier dossier) {
    setDossier(dossier);
  }

  public void addRole(PersonType role) {
    PersonRole personRole = new PersonRole(this, role.getCode());
    if (roles == null) {
      roles = new HashSet<>();
    }
    roles.add(personRole);
  }

  public boolean containsRole(PersonType role) {
    if (roles == null) {
      return false;
    }
    return roles.stream()
        .map(r -> r.getId().getRole())
        .anyMatch(roleId -> Objects.equals(roleId, role.getCode()));
  }

  public void removeRole(PersonType role) {
    if (roles == null) {
      return;
    }
    roles.removeIf(r -> r.personType() == role);
  }

  public boolean hasRoles() {
    return roles != null && !roles.isEmpty();
  }
}
