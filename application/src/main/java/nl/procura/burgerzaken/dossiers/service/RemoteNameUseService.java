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
import nl.procura.burgerzaken.dossiers.converters.GbaRestNameUseConverter;
import nl.procura.burgerzaken.dossiers.model.extract.Extract;

@Service
public class RemoteNameUseService implements NameUseService {

  private final GbaClient               client;
  private final GbaRestNameUseConverter converter;

  public RemoteNameUseService(GbaClient client,
      GbaRestNameUseConverter converter) {
    this.client = client;
    this.converter = converter;
  }

  @Override
  public Extract add(Extract dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Extract update(Extract dossier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Extract findByCaseNumber(String caseNumber) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
