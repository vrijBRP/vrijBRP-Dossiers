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

package nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.base;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiPerson;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonType;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "RelocationConsenter")
public class AdminApiConsenter extends AdminApiPerson {

  @Override
  public Person toPerson(Dossier dossier) {
    Person person = super.toPerson(dossier);
    person.addRole(PersonType.CONSENTER);
    return person;
  }

  public static AdminApiConsenter of(Person person) {
    if (!person.containsRole(PersonType.CONSENTER)) {
      throw new IllegalArgumentException("Person doesn't have consenter role");
    }
    return new AdminApiConsenter(
        AdminApiConsenter.builder()
            .bsn(BsnUtils.toBsnString(person.getBsn()))
            .contactInformation(AdminApiContactInformation.builder()
                .email(person.getEmail())
                .telephoneNumber(person.getPhoneNumber())
                .build()));
  }
}
