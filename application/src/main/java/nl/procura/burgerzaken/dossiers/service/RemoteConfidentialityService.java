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
import nl.procura.burgerzaken.dossiers.converters.GbaRestConfidentialityConverter;
import nl.procura.burgerzaken.dossiers.model.confidentiality.Confidentiality;

@Service
public class RemoteConfidentialityService implements ConfidentialityService {

  private final GbaClient                       client;
  private final GbaRestConfidentialityConverter converter;

  public RemoteConfidentialityService(GbaClient client,
      GbaRestConfidentialityConverter converter) {
    this.client = client;
    this.converter = converter;
  }

  @Override
  public Object add(Object dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Object update(Object dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Confidentiality findByCaseNumber(String caseNumber) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
