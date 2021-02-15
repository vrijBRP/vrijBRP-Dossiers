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

package nl.procura.burgerzaken.dossiers.repositories.events;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nl.procura.burgerzaken.dossiers.model.events.EventLog;
import nl.procura.burgerzaken.dossiers.model.events.EventType;

@SpringBootTest
class EventLogRepositoryTest {

  @Autowired
  private EventLogRepository repository;

  @Autowired
  private EntityManager em;

  @Test
  void saveMustStoreTypeAsString() {
    // given
    String objectId = "dossier-id";
    String clientId = "client-id";
    EventLog event = new EventLog(EventType.INTRA_MUNICIPAL_RELOCATION_CREATED, objectId, clientId);
    // when
    repository.save(event);
    // then
    Tuple db = (Tuple) em.createNativeQuery("SELECT * FROM event_log" +
        " WHERE event_id = ?", Tuple.class)
        .setParameter(1, event.getEventId())
        .getSingleResult();
    Assertions.assertEquals("INTRA_MUNICIPAL_RELOCATION_CREATED", db.get("type"));
    Assertions.assertEquals(objectId, db.get("object_id"));
    Assertions.assertEquals(clientId, db.get("client_id"));
  }
}
