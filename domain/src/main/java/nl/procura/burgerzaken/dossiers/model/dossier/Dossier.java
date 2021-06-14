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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.*;

import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.util.DatabaseFieldNotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class Dossier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "doss_id")
  private Long dossierId;

  @OneToOne
  @JoinColumn(name = "client_id")
  private Client client;

  @Column(name = "doss_type")
  @DatabaseFieldNotNull
  private DossierType dossierType;

  @Column(name = "dt_added")
  @DatabaseFieldNotNull
  private LocalDateTime dateAdded;

  @Column(name = "d_start")
  @DatabaseFieldNotNull
  private LocalDate dateStart;

  @Column(name = "casenr")
  @DatabaseFieldNotNull
  private String caseNumber;

  @Column(name = "status")
  private DossierStatus status;

  @OneToMany(mappedBy = "dossier")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<Person> people;

  @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  public Set<DossierReference> references;

  /**
   * Public empty constructor for JPA only.
   */
  public Dossier() {
    // empty constructor for JPA only
  }

  public Dossier(DossierType type, Client client) {
    this.client = client;
    dossierType = type;
    dateAdded = LocalDateTime.now();
    dateStart = LocalDate.now();
    caseNumber = "";
    status = DossierStatus.CREATED;
  }

  public Dossier addPerson(Person person) {
    person.setDossier(this);
    people.add(person);
    return this;
  }

  /**
   * Adds the person to the dossier. If a person with the same role already exists, it replaces the person.
   */
  public void setPersonByRole(Person person) {
    // for now, only support one role
    int numberOfRoles = person.getRoles().size();
    if (numberOfRoles != 1) {
      throw new IllegalArgumentException(
          format("Person %s must have one role, %d found", person.getBsn(), numberOfRoles));
    }
    PersonType personType = person.getRoles().iterator().next().personType();
    people.forEach(p -> p.removeRole(personType));
    people.removeIf(p -> !p.hasRoles());
    addPerson(person);
  }

  public Optional<Person> getPersonByRole(PersonType role) {
    return people.stream()
        .filter(p -> p.containsRole(role))
        .findFirst();
  }

  public Dossier addReference(String referenceNumber, String description) {
    DossierReference reference = new DossierReference(this, referenceNumber, description);
    if (references == null) {
      references = new HashSet<>();
    }
    references.add(reference);
    return this;
  }
}
