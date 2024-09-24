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

import static nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter.toGbaRestZaakAlgemeen;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.INHOUD_VERMIS;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.REISDOCUMENT;

import java.util.List;

import nl.procura.burgerzaken.dossiers.model.traveldocument.TravelDocument;
import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.extract.Extract;
import nl.procura.burgerzaken.dossiers.model.withholding.WithholdingOrMissingDocument;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;

@Component
public class GbaRestTravelDocumentConverter implements GbaConverter<TravelDocument> {

  @Override
  public DossierType dossierType() {
    return DossierType.TRAVEL_DOCUMENT;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return REISDOCUMENT;
  }

  @Override
  public TravelDocument toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    return new TravelDocument(dossier);
  }

  @Override
  public boolean isRelevantForBsn(GbaRestZaak zaak, List<Bsn> bsns) {
    return true;
  }

  public static GbaRestZaak toGbaRestZaak(Extract extract) {
    Dossier dossier = extract.getDossier();
    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(dossier, REISDOCUMENT));
    return zaak;
  }
}
