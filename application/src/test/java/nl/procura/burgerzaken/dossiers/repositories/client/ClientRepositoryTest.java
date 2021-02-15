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

package nl.procura.burgerzaken.dossiers.repositories.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nl.procura.burgerzaken.dossiers.model.client.Client;

@SpringBootTest
public class ClientRepositoryTest {

  @Autowired
  private ClientRepository repository;

  @Autowired
  private EntityManager em;

  @Test
  void newClientMustBeSavedWithoutExceptions() {
    // given
    Client client = newClient();
    // when
    repository.save(client);
    // detach all entities so we're sure we're not comparing against cached entities
    em.clear();
    // then
    Client actualClient = em.createQuery("SELECT c FROM Client c" +
        " WHERE c.clientId = :clientId", Client.class)
        .setParameter("clientId", client.getClientId())
        .getSingleResult();
    assertEquals(client, actualClient);
  }

  public static Client newClient() {
    Client client = new Client();
    client.setClientId(UUID.randomUUID().toString());
    client.setApplication("test application");
    client.setCustomer("test customer");
    return client;
  }
}
