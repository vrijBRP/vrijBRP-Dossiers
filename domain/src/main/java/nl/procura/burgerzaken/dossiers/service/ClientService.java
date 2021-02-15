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

import static java.lang.String.format;
import static nl.procura.burgerzaken.dossiers.model.error.ApiErrorType.INTERNAL_ERROR;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.repositories.client.ClientRepository;

@Service
public class ClientService {

  private final ClientRepository repository;

  public ClientService(ClientRepository repository) {
    this.repository = repository;
  }

  public Client getById(String clientId) {
    return repository.findById(clientId)
        .orElseThrow(() -> new ApiException(INTERNAL_ERROR, format("Client %s not set-up correctly", clientId)));
  }
}
