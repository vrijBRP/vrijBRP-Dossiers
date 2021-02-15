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

package nl.procura.burgerzaken.dossiers.converters;

import static nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestToestemmingStatus.*;

import java.util.HashMap;
import java.util.Map;

import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestAangifteStatus;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestToestemmingStatus;

public final class GbaRestToestemmingStatusConverter {

  private static final Map<GbaRestToestemmingStatus, String> TO_CONSENT = Map.of(
      NIET_INGEVULD, "P",
      JA, "A",
      NEE, "D",
      NIET_VAN_TOEPASSING, "");

  private static final Map<GbaRestToestemmingStatus, GbaRestAangifteStatus> TO_AANGIFTE_STATUS = Map.of(
      NIET_INGEVULD, GbaRestAangifteStatus.NIET_INGEVULD,
      JA, GbaRestAangifteStatus.GEACCEPTEERD,
      NEE, GbaRestAangifteStatus.NIET_GEACCEPTEERD,
      NIET_VAN_TOEPASSING, GbaRestAangifteStatus.NIET_INGEVULD);

  private static final Map<String, GbaRestToestemmingStatus> OF_CONSENT = new HashMap<>(TO_CONSENT.size());

  static {
    TO_CONSENT.forEach((k, v) -> OF_CONSENT.put(v, k));
  }

  private GbaRestToestemmingStatusConverter() {
  }

  public static String toConsent(GbaRestToestemmingStatus status) {
    String consent = TO_CONSENT.get(status);
    return consent == null ? "" : consent;
  }

  public static GbaRestToestemmingStatus ofConsent(String consent) {
    GbaRestToestemmingStatus status = OF_CONSENT.get(consent);
    if (status == null) {
      throw new IllegalArgumentException("Consent " + consent + " doesn't exist");
    }
    return status;
  }

  public static GbaRestAangifteStatus toGbaAangifteStatus(GbaRestToestemmingStatus status) {
    GbaRestAangifteStatus aangifteStatus = TO_AANGIFTE_STATUS.get(status);
    return aangifteStatus == null ? GbaRestAangifteStatus.NIET_INGEVULD : aangifteStatus;
  }
}
