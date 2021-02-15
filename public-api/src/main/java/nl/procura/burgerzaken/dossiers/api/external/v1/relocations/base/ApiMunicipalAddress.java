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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiMunicipality;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "RelocationMunicipalAddress")
public class ApiMunicipalAddress {

  @Schema(example = "Dorpstraat")
  private String street;

  @Schema(required = true, example = "1")
  @NotNull(message = "houseNumber is mandatory")
  private Integer houseNumber;

  @Schema(example = "A")
  private String houseLetter;

  @Schema(example = "4")
  private String houseNumberAddition;

  @Schema(required = true, example = "1122AB")
  @NotNull(message = "postalCode is mandatory")
  private String postalCode;

  @Schema(example = "Residence XYZ")
  private String residence;

  @Valid
  @Schema(example = "{\"code\":\"0000\",\"description\":\"Municipality XYZ\"}")
  private ApiMunicipality municipality;

  @Schema(description = "The purpose or function of the new address")
  private ApiAddressFunctionType addressFunction = ApiAddressFunctionType.LIVING_ADDRESS;

  @Schema(required = true,
      description = "The total number of residents living on the new address will have after the relocation",
      example = "1")
  @NotNull(message = "numberOfResidents is mandatory")
  private Integer numberOfResidents;

  @Schema(example = "Unknown")
  private String destinationCurrentResidents;

  @Valid
  @Schema(required = true)
  @NotNull(message = "liveIn is mandatory")
  private ApiLiveIn liveIn;

  @Valid
  @Schema(required = true)
  private ApiMainOccupant mainOccupant;
}
