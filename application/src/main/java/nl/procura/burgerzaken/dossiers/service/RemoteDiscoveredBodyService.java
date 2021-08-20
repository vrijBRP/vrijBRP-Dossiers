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
import nl.procura.burgerzaken.dossiers.converters.GbaRestDiscoveredBodyConverter;
import nl.procura.burgerzaken.dossiers.model.deaths.DiscoveredBody;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakToevoegenVraag;

@Service
public class RemoteDiscoveredBodyService implements DiscoveredBodyService {

  private final GbaClient                      client;
  private final GbaRestDiscoveredBodyConverter converter;

  public RemoteDiscoveredBodyService(GbaClient client,
      GbaRestDiscoveredBodyConverter converter) {
    this.client = client;
    this.converter = converter;
  }

  @Override
  public DiscoveredBody add(DiscoveredBody discoveredBody) {
    GbaRestZaakToevoegenVraag request = new GbaRestZaakToevoegenVraag();
    request.setZaak(GbaRestDiscoveredBodyConverter.toGbaRestZaak(discoveredBody));
    return converter.toDomainModel(client.zaken()
        .addZaak(request)
        .getInhoud());
  }

  @Override
  public DiscoveredBody update(DiscoveredBody discoveredBody) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public DiscoveredBody findByCaseNumber(String caseNumber) {
    return converter.toDomainModel(client.zaken()
        .getZaakByZaakId(caseNumber)
        .getInhoud());
  }
}
