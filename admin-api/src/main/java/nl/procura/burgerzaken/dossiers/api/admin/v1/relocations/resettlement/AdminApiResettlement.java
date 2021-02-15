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

package nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.resettlement;

import java.util.List;

import nl.procura.burgerzaken.dossiers.api.admin.v1.base.AdminApiCountry;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiDossier;
import nl.procura.burgerzaken.dossiers.api.admin.v1.dossier.AdminApiPerson;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.base.AdminApiAdminDurationType;
import nl.procura.burgerzaken.dossiers.api.admin.v1.relocations.base.AdminApiMunicipalAddress;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Resettlement")
public class AdminApiResettlement {

  @Schema(required = true)
  private AdminApiDossier dossier;

  @Schema(required = true)
  private AdminApiPerson declarant;

  @Schema(required = true)
  private AdminApiMunicipalAddress newAddress;

  @Schema(required = true)
  private AdminApiAdminDurationType duration;

  @Schema(required = true)
  private AdminApiCountry previousCountry;

  @Schema(required = true)
  private List<AdminApiResettlementPerson> relocators;
}
