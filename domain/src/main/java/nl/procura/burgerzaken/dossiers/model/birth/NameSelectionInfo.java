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

package nl.procura.burgerzaken.dossiers.model.birth;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NameSelectionInfo {

  private boolean            jointChildren;
  private String             lastname;
  private String             prefix;
  private TitlePredicateType title;

  public void setLastname(String lastname) {
    this.lastname = defaultIfBlank(lastname, null);
  }

  public void setPrefix(String prefix) {
    this.prefix = defaultIfBlank(prefix, null);
  }
}
