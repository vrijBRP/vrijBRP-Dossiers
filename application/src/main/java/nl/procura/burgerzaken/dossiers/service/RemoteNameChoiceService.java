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

package nl.procura.burgerzaken.dossiers.service;

import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakStatusType.OPGENOMEN;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakStatusType.VERWERKT;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.NAAMSKEUZE;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.converters.GbaRestNameChoiceConverter;
import nl.procura.burgerzaken.dossiers.model.namechoice.NameChoice;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gba.web.rest.v2.model.base.GbaRestAntwoord;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakZoekenAntwoord;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakZoekenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakZoekGegeven;
import nl.procura.gba.web.rest.v2.model.zaken.base.naamskeuze.GbaRestNaamskeuze;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemoteNameChoiceService implements NameChoiceService {

  private final GbaClient                  client;
  private final GbaRestNameChoiceConverter converter;

  @Override
  public NameChoice add(NameChoice dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public NameChoice update(NameChoice dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public NameChoice findByCaseNumber(String caseNumber) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Optional<NameChoice> findByMotherAndFather(Bsn bsnMother, Bsn bsnFather) {
    GbaRestZaakZoekenVraag request = new GbaRestZaakZoekenVraag();
    request.setPersoonId(bsnMother.toString());
    request.setZoekGegevens(Collections.singletonList(GbaRestZaakZoekGegeven.SPECIFIEKE_ZAAKGEGEVENS));
    request.setZaakTypes(List.of(NAAMSKEUZE));
    request.setZaakStatussen(List.of(OPGENOMEN, VERWERKT));
    request.setMax(5);
    GbaRestAntwoord<GbaRestZaakZoekenAntwoord> antwoord = client.zaken().findZaken(request);
    for (GbaRestZaak zaak : antwoord.getInhoud().getZaken()) {
      GbaRestNaamskeuze naamskeuze = zaak.getNaamskeuze();
      if (naamskeuze != null) {
        boolean isMother = naamskeuze.getMoeder().getBsn().equals(bsnMother.toLong());
        boolean isPartner = naamskeuze.getPartner().getBsn().equals(bsnFather.toLong());
        if (isMother && isPartner) {
          return Optional.of(converter.toDomainModel(zaak));
        }
      }
    }
    return Optional.empty();
  }
}
