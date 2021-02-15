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

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiCountry;
import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.deaths.CauseOfDeathType;
import nl.procura.burgerzaken.dossiers.model.deaths.FuneralServices;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "FuneralServices")
public class ApiFuneralServices {

  @Schema(name = "serviceType")
  @NotNull(message = "serviceType in funeralServices is mandatory")
  private ApiFuneralServiceType serviceType;

  @Schema(name = "date")
  @NotNull(message = "date in funeralServices is mandatory")
  private LocalDate date;

  @Schema(name = "time", pattern = "HH:mm")
  private String time;

  @Schema(name = "outsideBenelux")
  @NotNull(message = "outsideBenelux is mandatory")
  private Boolean outsideBenelux;

  @Schema(name = "causeOfDeathType")
  private CauseOfDeathType causeOfDeathType;

  @Schema(name = "countryOfDestination")
  private ApiCountry countryOfDestination;

  @Schema(name = "placeOfDestination")
  private String placeOfDestination;

  @Schema(name = "via")
  private String via;

  @Schema(name = "transportation")
  private String transportation;

  public FuneralServices toFuneralServices() {
    return new FuneralServices()
        .setServiceType(serviceType.getType())
        .setDate(date)
        .setTime(time)
        .setOutsideBenelux(outsideBenelux)
        .setCauseOfDeathType(causeOfDeathType)
        .setCountryOfDestination(countryOfDestination.toTableValue())
        .setPlaceOfDestination(placeOfDestination)
        .setVia(via)
        .setTransportation(transportation);
  }

  public static ApiFuneralServices of(FuneralServices funeralServices) {
    if (ModelValidation.isValid(funeralServices)) {
      return ApiFuneralServices.builder()
          .serviceType(ApiFuneralServiceType.valueOfType(funeralServices.getServiceType()))
          .date(funeralServices.getDate())
          .time(funeralServices.getTime())
          .outsideBenelux(funeralServices.getOutsideBenelux())
          .causeOfDeathType(funeralServices.getCauseOfDeathType())
          .countryOfDestination(ApiCountry.of(funeralServices.getCountryOfDestination()))
          .placeOfDestination(funeralServices.getPlaceOfDestination())
          .via(funeralServices.getVia())
          .transportation(funeralServices.getTransportation())
          .build();
    }
    return null;
  }
}
