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

import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.INTER_MUNICIPAL_RELOCATION;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.*;

import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonType;
import nl.procura.burgerzaken.dossiers.util.DatabaseFieldNotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class InterMunicipalRelocation implements ConsentRelocation {

  @Id
  @Column(name = "doss_id")
  @DatabaseFieldNotNull
  private Long dossierId;

  @OneToOne(optional = false)
  @MapsId
  @JoinColumn(name = "doss_id")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @DatabaseFieldNotNull
  private Dossier dossier;

  @Column(name = "street")
  @DatabaseFieldNotNull
  private String street;

  @Column(name = "hnr")
  @DatabaseFieldNotNull
  private Integer houseNumber;

  @Column(name = "hnr_l")
  @DatabaseFieldNotNull
  private String houseNumberLetter;

  @Column(name = "hnr_t")
  @DatabaseFieldNotNull
  private String houseNumberAddition;

  @Column(name = "pc")
  @DatabaseFieldNotNull
  private String postalCode;

  @Column(name = "residence")
  @DatabaseFieldNotNull
  private String residence;

  @Column(name = "municipality")
  @DatabaseFieldNotNull
  private String municipality;

  @Column(name = "address_func")
  @DatabaseFieldNotNull
  private String addressFunction;

  @Column(name = "residents_count")
  @DatabaseFieldNotNull
  private Integer residentsCount;

  @Column(name = "dest_curr_residents")
  @DatabaseFieldNotNull
  private String destCurrResidents;

  @Column(name = "live_in")
  @DatabaseFieldNotNull
  private Boolean liveIn;

  @Column(name = "consent")
  @DatabaseFieldNotNull
  private String consent;

  @Column(name = "prev_municipality")
  @DatabaseFieldNotNull
  private String previousMunicipality;

  @Transient
  private final Set<Relocator> relocators = new HashSet<>();

  public InterMunicipalRelocation(Dossier dossier) {
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
    previousMunicipality = "";
  }

  public void addRelocator(Relocator relocator) {
    relocators.add(relocator);
  }

  public Optional<Person> getDeclarant() {
    return dossier.getPersonByRole(PersonType.DECLARANT);
  }

  public void setDeclarant(Person person) {
    dossier.setPersonByRole(person);
  }

  public Optional<Person> getConsenter() {
    return dossier.getPersonByRole(PersonType.CONSENTER);
  }

  @Override
  public void setConsenter(Person person) {
    dossier.setPersonByRole(person);
  }

  @Override
  public Optional<Person> getMainOccupant() {
    return dossier.getPersonByRole(PersonType.MAIN_OCCUPANT);
  }

  public void setMainOccupant(Person person) {
    dossier.setPersonByRole(person);
  }

  public static InterMunicipalRelocation newDefault(Client client) {
    return new InterMunicipalRelocation(new Dossier(INTER_MUNICIPAL_RELOCATION, client));
  }
}
