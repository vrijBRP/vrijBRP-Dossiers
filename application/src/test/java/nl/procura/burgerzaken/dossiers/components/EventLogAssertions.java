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

package nl.procura.burgerzaken.dossiers.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.events.EventType;

@Component
public class EventLogAssertions {

  private final EntityManager em;

  public EventLogAssertions(EntityManager em) {
    this.em = em;
  }

  public void assertClientAndType(String objectId, String clientId, EventType type) {
    Tuple db = (Tuple) em.createNativeQuery("SELECT * FROM event_log" +
        " WHERE object_id = ?" +
        " ORDER BY event_id DESC" +
        " FETCH FIRST ROW ONLY", Tuple.class)
        .setParameter(1, objectId)
        .getSingleResult();
    assertEquals(type.name(), db.get("type"));
    assertEquals(clientId, db.get("client_id"));
  }
}
