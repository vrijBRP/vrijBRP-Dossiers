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

package nl.procura.burgerzaken.dossiers.model.deaths;

import java.time.LocalDate;

import org.apache.commons.lang3.ObjectUtils;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.base.TableValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
public class FuneralServices implements ModelValidation {

  private FuneralServiceType serviceType;
  private LocalDate          date;
  private String             time;
  private Boolean            outsideBenelux;
  private CauseOfDeathType   causeOfDeathType;
  private TableValue         countryOfDestination;
  private String             placeOfDestination;
  private String             via;
  private String             transportation;

  @Override
  public boolean isValid() {
    return ObjectUtils.anyNotNull(date, serviceType, outsideBenelux);
  }
}
