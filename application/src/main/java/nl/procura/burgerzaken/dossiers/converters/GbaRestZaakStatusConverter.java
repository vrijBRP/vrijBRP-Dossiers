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

import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakStatusType.INCOMPLEET;

import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakStatusType;

public final class GbaRestZaakStatusConverter {

  private GbaRestZaakStatusConverter() {
  }

  public static DossierStatus toStatus(GbaRestZaakStatusType status) {
    switch (status) {
      case INCOMPLEET:
        return DossierStatus.INCOMPLETE;
      case WACHTKAMER:
        return DossierStatus.ON_HOLD;
      case OPGENOMEN:
        return DossierStatus.CREATED;
      case GEWEIGERD:
        return DossierStatus.REFUSED;
      case VERWERKT:
      case VERWERKT_IN_GBA:
        return DossierStatus.PROCESSED;
      case GEANNULEERD:
        return DossierStatus.CANCELLED;
      case GEPREVALIDEERD:
      case DOCUMENT_ONTVANGEN:
      case INBEHANDELING:
      case ONBEKEND:
      default:
        return DossierStatus.PROCESSING;
    }
  }

  public static GbaRestZaakStatusType toGbaStatus(DossierStatus status) {
    switch (status) {
      case CREATED:
        return GbaRestZaakStatusType.OPGENOMEN;
      case PROCESSING:
        return GbaRestZaakStatusType.INBEHANDELING;
      case ON_HOLD:
        return GbaRestZaakStatusType.WACHTKAMER;
      case PROCESSED:
        return GbaRestZaakStatusType.VERWERKT;
      case CANCELLED:
        return GbaRestZaakStatusType.GEANNULEERD;
      case REFUSED:
        return GbaRestZaakStatusType.GEWEIGERD;
      case INCOMPLETE:
        return INCOMPLEET;
      case DELETED:
      default:
        throw new IllegalArgumentException(status + " not supported");
    }
  }

}
