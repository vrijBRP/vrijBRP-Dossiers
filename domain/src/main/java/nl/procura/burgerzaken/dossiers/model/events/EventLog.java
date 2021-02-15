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

package nl.procura.burgerzaken.dossiers.model.events;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.*;

import nl.procura.burgerzaken.dossiers.util.DatabaseFieldNotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class EventLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long eventId;

  // don't create a foreign key as client might be removed and events shouldn't be removed
  @Column
  @DatabaseFieldNotNull
  private String clientId;

  @Column
  @Enumerated(EnumType.STRING)
  @DatabaseFieldNotNull
  private EventType type;

  @Column
  @DatabaseFieldNotNull
  private String objectId;

  @Column
  @DatabaseFieldNotNull
  private Timestamp timeStamp;

  @Lob
  @Column(length = 10_000)
  private String payload;

  public EventLog(EventType type, String objectId, String clientId) {
    this.type = type;
    this.objectId = objectId;
    this.clientId = clientId;
    this.timeStamp = Timestamp.from(Instant.now());
  }
}
