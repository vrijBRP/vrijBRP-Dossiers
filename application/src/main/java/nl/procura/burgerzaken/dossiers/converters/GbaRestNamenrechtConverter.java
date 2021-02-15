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

package nl.procura.burgerzaken.dossiers.converters;

import static java.util.Optional.ofNullable;
import static nl.procura.burgerzaken.dossiers.model.base.PersistableEnum.valueOfCode;

import nl.procura.burgerzaken.dossiers.model.base.NameSelection;
import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;
import nl.procura.gba.web.rest.v2.model.zaken.base.namenrecht.GbaRestNamenrecht;

public final class GbaRestNamenrechtConverter {

  private GbaRestNamenrechtConverter() {
  }

  public static NameSelection toNameSelection(GbaRestNamenrecht namenrecht) {
    return new NameSelection(namenrecht.getGeslachtsnaam(),
        namenrecht.getVoorvoegsel(),
        valueOfCode(TitlePredicateType.values(), namenrecht.getTitelPredikaat()));
  }

  public static GbaRestNamenrecht toGbaNamenrecht(NameSelection selection) {
    GbaRestNamenrecht namenrecht = new GbaRestNamenrecht();
    namenrecht.setGeslachtsnaam(selection.getLastName());
    namenrecht.setVoorvoegsel(selection.getPrefix());
    namenrecht.setTitelPredikaat(ofNullable(selection.getTitle())
        .map(TitlePredicateType::getCode)
        .orElse(null));
    return namenrecht;
  }
}
