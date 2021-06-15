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

package nl.procura.burgerzaken.dossiers.api.external.v1.birth;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiGenderType;
import nl.procura.burgerzaken.dossiers.model.birth.BirthChild;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonRole;
import nl.procura.burgerzaken.dossiers.util.Constants.Formats;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "BirthChild")
public class ApiChild {

  @Schema(name = "firstname", required = true)
  private String firstname;

  @Schema(name = "gender", required = true)
  private ApiGenderType gender;

  @Schema(name = "birthDateTime", required = true, example = "2020-04-03T10:40:10")
  @JsonFormat(pattern = Formats.DATE_TIME_FORMAT)
  private LocalDateTime birthDateTime;

  public BirthChild toChild() {
    Person person = new Person();
    person.addRole(PersonRole.CHILD);
    person.setBsn(-1L);
    person.setEmail("");
    person.setPhoneNumber("");

    BirthChild child = new BirthChild(person);
    child.setFirstname(firstname);
    child.setGender(gender.getType());
    child.setBirthDate(birthDateTime.toLocalDate());
    child.setBirthTime(birthDateTime.toLocalTime());
    return child;
  }

  public static ApiChild of(BirthChild child) {
    return new ApiChild(
        ApiChild.builder()
            .firstname(child.getFirstname())
            .gender(ApiGenderType.valueOfType(child.getGender()))
            .birthDateTime(LocalDateTime.of(child.getBirthDate(), child.getBirthTime())));
  }
}
