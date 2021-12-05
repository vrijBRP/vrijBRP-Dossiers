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
import nl.procura.burgerzaken.dossiers.converters.GbaRestEmigrationConverter;
import nl.procura.burgerzaken.dossiers.model.relocations.Emigration;

@Service
public class RemoteEmigrationService implements EmigrationService {

  private final GbaClient                  client;
  private final GbaRestEmigrationConverter converter;

  public RemoteEmigrationService(GbaClient client,
      GbaRestEmigrationConverter converter) {
    this.client = client;
    this.converter = converter;
  }

  @Override
  public Emigration add(Emigration dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Emigration update(Emigration dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Emigration findByCaseNumber(String caseNumber) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
