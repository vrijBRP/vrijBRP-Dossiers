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

package nl.procura.burgerzaken.dossiers.api.external.v1.relocations.inter;

import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiPerson;
import nl.procura.burgerzaken.dossiers.api.external.v1.relocations.base.ApiDeclarationType;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonRole;
import nl.procura.burgerzaken.dossiers.model.relocations.Relocator;
import nl.procura.burgerzaken.dossiers.model.relocations.info.DeclarationType;
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
@Schema(name = "InterMunicipalRelocationPerson")
public class ApiInterMunicipalRelocationPerson extends ApiPerson {

  @Schema(example = "REGISTERED")
  @NotNull(message = "declarationType is mandatory")
  private ApiDeclarationType declarationType;

  public Relocator toRelocator() {
    Person person = toPerson();
    person.addRole(PersonRole.RELOCATOR);
    Relocator relocator = new Relocator(person);
    relocator.setDeclaration(declarationType.getType().getCode());
    return relocator;
  }

  public static ApiInterMunicipalRelocationPerson of(Relocator relocator) {
    return new ApiInterMunicipalRelocationPerson(
        ApiInterMunicipalRelocationPerson.builder()
            .bsn(BsnUtils.toBsnString(relocator.getPerson().getBsn()))
            .contactInformation(ApiContactInformation.builder()
                .email(relocator.getPerson().getEmail())
                .telephoneNumber(relocator.getPerson().getPhoneNumber())
                .build())
            .declarationType(ApiDeclarationType.valueOfType(DeclarationType.valueOfCode(relocator.getDeclaration()))));
  }
}
