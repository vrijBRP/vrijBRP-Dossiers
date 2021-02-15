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

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.*;

import nl.procura.burgerzaken.dossiers.model.base.GenderType;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.util.DatabaseFieldNotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "birth_child")
public class BirthChild {

  @Id
  private Long personId;

  @OneToOne(optional = false)
  @MapsId
  @JoinColumn(name = "person_id")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Person person;

  @Column(name = "firstname")
  @DatabaseFieldNotNull
  private String firstname;

  @Column(name = "gender")
  @DatabaseFieldNotNull
  private GenderType gender;

  @Column(name = "birth_date")
  @DatabaseFieldNotNull
  private LocalDate birthDate;

  @Column(name = "birth_time")
  @DatabaseFieldNotNull
  private LocalTime birthTime;

  public BirthChild(Person person) {
    this.person = person;
  }
}
