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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiPerson;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "RelocationMainOccupant")
public class ApiMainOccupant extends ApiPerson {

  @Override
  public Person toPerson() {
    Person person = super.toPerson();
    person.addRole(PersonRole.MAIN_OCCUPANT);
    return person;
  }

  public static ApiMainOccupant of(Person person) {
    if (!person.containsRole(PersonRole.MAIN_OCCUPANT)) {
      throw new IllegalArgumentException("Person doesn't have main occupant role");
    }
    return new ApiMainOccupant(
        ApiMainOccupant.builder()
            .bsn(person.getBsn().toString())
            .contactInformation(ApiContactInformation.builder()
                .email(person.getEmail())
                .telephoneNumber(person.getPhoneNumber())
                .build()));
  }

}
