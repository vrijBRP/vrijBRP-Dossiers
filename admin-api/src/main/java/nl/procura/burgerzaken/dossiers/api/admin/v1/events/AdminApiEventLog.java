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

package nl.procura.burgerzaken.dossiers.api.admin.v1.events;

import nl.procura.burgerzaken.dossiers.model.events.EventLog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EventLog")
public class AdminApiEventLog {

  @Schema(description = "The ID of the event", example = "123")
  private Long eventId;

  @Schema(description = "The ID of the client which triggered the event", example = "web-client")
  private String clientId;

  @Schema(description = "The type of the event", example = "DOSSIER_UPDATED")
  private AdminApiEventType type;

  @Schema(description = "The ID of the object related to the event", example = "AB123")
  private String objectId;

  @Schema(description = "Payload of event encoded as JSON", example = "{\"status\":\"DOSSIER_UPDATED\"}")
  private String payload;

  public static AdminApiEventLog of(EventLog event) {
    return new AdminApiEventLog(event.getEventId(), event.getClientId(), AdminApiEventType.of(event.getType()),
        event.getObjectId(), event.getPayload());
  }
}
