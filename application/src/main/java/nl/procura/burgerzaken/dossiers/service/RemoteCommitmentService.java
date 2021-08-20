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

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.converters.GbaRestCommitmentConverter;
import nl.procura.burgerzaken.dossiers.model.commitment.Commitment;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakStatusUpdateVraag;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakToevoegenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakUpdateVraag;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakStatusType;

@Service
public class RemoteCommitmentService implements CommitmentService {

  private final GbaClient                  client;
  private final GbaRestCommitmentConverter converter;

  public RemoteCommitmentService(GbaClient client,
      GbaRestCommitmentConverter converter) {
    this.client = client;
    this.converter = converter;
  }

  @Override
  public Commitment add(Commitment commitment) {
    GbaRestZaakToevoegenVraag request = new GbaRestZaakToevoegenVraag();
    request.setZaak(GbaRestCommitmentConverter.toGbaRestZaak(commitment));
    return converter.toDomainModel(client.zaken()
        .addZaak(request)
        .getInhoud());
  }

  @Override
  public Commitment update(Commitment commitment) {
    GbaRestZaakUpdateVraag request = new GbaRestZaakUpdateVraag();
    request.setZaak(GbaRestCommitmentConverter.toGbaRestZaak(commitment));
    return converter.toDomainModel(client.zaken()
        .updateZaak(request)
        .getInhoud());
  }

  @Override
  public Commitment findByCaseNumber(String caseNumber) {
    return converter.toDomainModel(client.zaken()
        .getZaakByZaakId(caseNumber)
        .getInhoud());
  }

  @Override
  public void cancel(String caseNumber) {
    GbaRestZaakStatusUpdateVraag request = new GbaRestZaakStatusUpdateVraag();
    request.setZaakId(caseNumber);
    request.setZaakStatus(GbaRestZaakStatusType.GEANNULEERD);
    client.zaken().updateZaakStatus(request);
  }

  @Override
  public void cancel(String caseNumber, String message) {
    GbaRestZaakStatusUpdateVraag request = new GbaRestZaakStatusUpdateVraag();
    request.setZaakId(caseNumber);
    request.setZaakStatus(GbaRestZaakStatusType.GEANNULEERD);
    request.setOpmerking(message);
    client.zaken().updateZaakStatus(request);
  }
}
