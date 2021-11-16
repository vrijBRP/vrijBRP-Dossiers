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
import java.util.*;

import nl.procura.burgerzaken.dossiers.model.client.Client;

import lombok.Data;

@Data
public class Dossier {

  private String        caseNumber;
  private Client        client;
  private DossierType   dossierType;
  private LocalDateTime dateAdded;
  private LocalDate     dateStart;
  private String        description;
  private DossierStatus status;

  public Set<DossierReference> references = new LinkedHashSet<>();
  private List<Person>         people     = new ArrayList<>();

  public Dossier() {
  }

  public Dossier(DossierType type, Client client) {
    this.client = client;
    dossierType = type;
    dateAdded = LocalDateTime.now();
    dateStart = LocalDate.now();
    caseNumber = "";
    status = new DossierStatus(DossierStatusType.CREATED, LocalDateTime.now());
  }

  public Dossier addPerson(Person person) {
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
    PersonRole personType = person.getRoles().iterator().next();
    people.forEach(p -> p.removeRole(personType));
    people.removeIf(p -> !p.hasRoles());
    addPerson(person);
  }

  public Optional<Person> getPersonByRole(PersonRole role) {
    return people.stream()
        .filter(p -> p.containsRole(role))
        .findFirst();
  }

  public Dossier addReference(String referenceNumber, String description) {
    references.add(new DossierReference(referenceNumber, description));
    return this;
  }
}
