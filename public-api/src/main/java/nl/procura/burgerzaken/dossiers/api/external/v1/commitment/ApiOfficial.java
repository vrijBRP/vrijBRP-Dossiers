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

package nl.procura.burgerzaken.dossiers.api.external.v1.commitment;

import java.util.List;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentOfficial;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "CommitmentOfficial")
public class ApiOfficial {

  @Schema(name = "name")
  private String name;

  @Schema(name = "contactInformation")
  private ApiContactInformation contactInformation;

  @Schema(name = "aliases")
  private List<String> aliases;

  public static ApiOfficial of(CommitmentOfficial official) {
    if (ModelValidation.isValid(official)) {
      Person contactPerson = new Person();
      contactPerson.setEmail(official.getEmail());
      contactPerson.setPhoneNumber(official.getTelephoneNumber());
      return ApiOfficial.builder()
          .name(official.getName())
          .contactInformation(ApiContactInformation.of(contactPerson))
          .aliases(official.getAliases())
          .build();
    }
    return null;
  }
}
