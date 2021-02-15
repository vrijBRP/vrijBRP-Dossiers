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

import static java.lang.String.format;

import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;

public interface GbaConverter<T> {

  DossierType dossierType();

  GbaRestZaakType zaakType();

  T toDomainModel(GbaRestZaak zaak);

  default Dossier toDossier(GbaRestZaak zaak) {
    GbaRestZaakType expected = zaakType();
    GbaRestZaakType actual = zaak.getAlgemeen().getType();
    if (actual != expected) {
      throw new IllegalArgumentException(format("Given zaak is of %s, expected %s", actual, expected));
    }
    return GbaRestDossierConverter.toDossier(zaak, dossierType());
  }
}
