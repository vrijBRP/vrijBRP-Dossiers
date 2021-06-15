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

package nl.procura.burgerzaken.dossiers.model.relocations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonRole;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class IntraMunicipalRelocation implements ConsentRelocation {

  @EqualsAndHashCode.Include
  private Dossier dossier;

  private String                street;
  private Integer               houseNumber;
  private String                houseNumberLetter;
  private String                houseNumberAddition;
  private String                postalCode;
  private String                residence;
  private String                municipality;
  private String                addressFunction;
  private Integer               residentsCount;
  private String                destCurrResidents;
  private Boolean               liveIn;
  private String                consent;
  private final List<Relocator> relocators = new ArrayList<>();

  public IntraMunicipalRelocation(Dossier dossier) {
    this.dossier = dossier;
    street = "";
    houseNumber = 0;
    houseNumberLetter = "";
    houseNumberAddition = "";
    postalCode = "";
    residence = "";
    municipality = "";
    addressFunction = "";
    residentsCount = 0;
    destCurrResidents = "";
    liveIn = false;
    consent = "";
  }

  public void addRelocator(Relocator relocator) {
    relocators.add(relocator);
  }

  public Optional<Person> getDeclarant() {
    return dossier.getPersonByRole(PersonRole.DECLARANT);
  }

  public void setDeclarant(Person person) {
    dossier.setPersonByRole(person);
  }

  public Optional<Person> getConsenter() {
    return dossier.getPersonByRole(PersonRole.CONSENTER);
  }

  @Override
  public void setConsenter(Person person) {
    dossier.setPersonByRole(person);
  }

  @Override
  public Optional<Person> getMainOccupant() {
    return dossier.getPersonByRole(PersonRole.MAIN_OCCUPANT);
  }

  public void setMainOccupant(Person person) {
    dossier.setPersonByRole(person);
  }
}
