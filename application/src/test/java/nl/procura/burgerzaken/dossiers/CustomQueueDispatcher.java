/*
 * Copyright 2022 - 2023 Procura B.V.
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

package nl.procura.burgerzaken.dossiers;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.QueueDispatcher;

@Slf4j
public class CustomQueueDispatcher extends QueueDispatcher {

  public void emptyQueue() {
    try {
      while (getResponseQueue().size() > 0) {
        getResponseQueue().poll(1, TimeUnit.MILLISECONDS);
      }
    } catch (InterruptedException e) {
      // interrupt thread owner too, if any
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }
}
