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

package nl.procura.burgerzaken.dossiers.api.external.v1.base;

import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.base.TableValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Schema(name = "Municipality", example = "{\"code\":\"0000\",\"description\":\"Municipal XYZ\"}")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiMunicipality extends ApiNationalTable {

  @Override
  @NotNull(message = "municipality code is mandatory")
  public String getCode() {
    return super.getCode();
  }

  public static ApiMunicipality of(TableValue tv) {
    return ApiMunicipality.builder()
        .code(tv.getCode())
        .description(tv.getDescription())
        .build();
  }
}
