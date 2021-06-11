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

package nl.procura.burgerzaken.dossiers.api.external.v1.deaths;

import static java.util.Optional.ofNullable;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiCountry;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiNationalTable;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiPerson;
import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.deaths.Deceased;
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
@Schema(name = "Deceased")
public class ApiDeceased extends ApiPerson {

  @Schema(name = "firstname")
  private String firstname;

  @Schema(name = "prefix")
  private String prefix;

  @Schema(name = "titlePredicate")
  private String titlePredicate;

  @Schema(name = "lastname")
  private String lastname;

  @Schema(name = "birthdate")
  private Integer birthdate;

  @Schema(name = "birthplace")
  private String birthplace;

  @Schema(name = "birthcountry")
  private ApiCountry birthcountry;

  public Deceased toDeceased() {
    Deceased deceased = new Deceased();
    deceased.setBsn(getBsn());
    deceased.setFirstname(getFirstname());
    deceased.setLastname(getLastname());
    deceased.setTitle(getTitlePredicate());
    deceased.setPrefix(getPrefix());
    deceased.setBirthdate(getBirthdate());
    deceased.setBirthplace(getBirthplace());
    deceased.setBirthcountry(ofNullable(getBirthcountry()).map(ApiNationalTable::toTableValue).orElse(null));
    return deceased;
  }

  public static ApiDeceased of(Deceased deceased) {
    if (ModelValidation.isValid(deceased)) {
      return ApiDeceased.builder()
          .bsn(BsnUtils.toBsnString(deceased.getBsn()))
          .firstname(deceased.getFirstname())
          .lastname(deceased.getLastname())
          .prefix(deceased.getPrefix())
          .titlePredicate(deceased.getTitle())
          .birthdate(deceased.getBirthdate())
          .birthplace(deceased.getBirthplace())
          .birthcountry(ApiCountry.of(deceased.getBirthcountry()))
          .build();
    }
    return null;
  }
}
