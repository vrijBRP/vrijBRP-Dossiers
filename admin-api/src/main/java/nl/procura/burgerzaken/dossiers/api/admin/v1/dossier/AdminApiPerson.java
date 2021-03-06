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

package nl.procura.burgerzaken.dossiers.api.admin.v1.dossier;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiContactInformation;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "DossierPerson")
public class AdminApiPerson {

  @Schema(required = true,
      description = "BurgerServiceNummer",
      example = "999990019",
      maxLength = 9,
      minLength = 9,
      pattern = "^[0-9]*$")
  private String bsn;

  private AdminApiContactInformation contactInformation;

  protected Person toPerson(Dossier dossier) {
    Person person = new Person();
    // parse BSN better?
    person.setDossier(dossier);
    person.setBsn(Long.valueOf(bsn));
    person.setEmail(contactInformation.getEmail());
    person.setPhoneNumber(contactInformation.getTelephoneNumber());
    return person;
  }
}
