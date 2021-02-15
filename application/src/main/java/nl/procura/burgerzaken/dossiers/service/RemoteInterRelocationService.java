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

import static nl.procura.burgerzaken.dossiers.converters.GbaRestRelocationConverter.toGbaRestInwoningVraag;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.converters.GbaRestInterMunicipalRelocationConverter;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.events.EventType;
import nl.procura.burgerzaken.dossiers.model.relocations.InterMunicipalRelocation;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakToevoegenVraag;

@Service
public class RemoteInterRelocationService implements InterRelocationService {

  private final GbaClient                                client;
  private final EventLogService                          eventLog;
  private final GbaRestInterMunicipalRelocationConverter converter;

  public RemoteInterRelocationService(GbaClient client,
      EventLogService eventLog,
      GbaRestInterMunicipalRelocationConverter converter) {
    this.client = client;
    this.eventLog = eventLog;
    this.converter = converter;
  }

  @Override
  public InterMunicipalRelocation add(InterMunicipalRelocation relocation) {
    GbaRestZaakToevoegenVraag request = new GbaRestZaakToevoegenVraag();
    request.setZaak(GbaRestInterMunicipalRelocationConverter.toGbaRestZaak(relocation));
    InterMunicipalRelocation created = converter.toDomainModel(client.zaken()
        .addZaak(request)
        .getInhoud());
    eventLog.add(EventType.INTER_MUNICIPAL_RELOCATION_CREATED, created.getDossier().getCaseNumber(),
        relocation.getDossier().getClient().getClientId());
    return created;
  }

  @Override
  public InterMunicipalRelocation update(InterMunicipalRelocation relocation) {
    throw new IllegalArgumentException("Not yet implemented");
  }

  @Override
  public void addConsent(String dossierId, Person consenter, String consentType) {
    client.verhuizing().updateInwoning(toGbaRestInwoningVraag(dossierId, consenter, consentType));
  }

  @Override
  public InterMunicipalRelocation findByCaseNumber(String caseNumber) {
    return converter.toDomainModel(client.zaken()
        .getZaakByZaakId(caseNumber)
        .getInhoud());
  }
}
