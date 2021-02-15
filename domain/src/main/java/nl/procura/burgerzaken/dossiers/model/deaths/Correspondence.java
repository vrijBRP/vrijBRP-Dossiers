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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
public class Correspondence implements ModelValidation {

  private CommunicationType communicationType;
  private String            organization;
  private String            departement;
  private String            name;
  private String            email;
  private String            telephoneNumber;
  private String            street              = "";
  private Integer           houseNumber         = 0;
  private String            houseNumberLetter   = "";
  private String            houseNumberAddition = "";
  private String            postalCode          = "";
  private String            residence           = "";

  @Override
  public boolean isValid() {
    return communicationType != null && isNotBlank(name);
  }
}
