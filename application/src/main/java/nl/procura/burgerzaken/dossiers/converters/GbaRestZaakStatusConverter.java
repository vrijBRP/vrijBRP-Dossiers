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

import java.time.LocalDateTime;
import java.util.List;

import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatusType;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakStatus;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakStatusType;

public final class GbaRestZaakStatusConverter {

  private GbaRestZaakStatusConverter() {
  }

  public static DossierStatus toStatus(List<GbaRestZaakStatus> statusList) {
    if (statusList == null || statusList.isEmpty()) {
      return new DossierStatus(DossierStatusType.UNKNOWN, LocalDateTime.now());
    }
    GbaRestZaakStatus status = statusList.get(0);
    LocalDateTime dateTime = GbaRestConverter.toLocalDateTime(status.getInvoerDatum(), status.getInvoerTijd());
    return new DossierStatus(toStatusType(status.getType()), dateTime);
  }

  public static DossierStatusType toStatusType(GbaRestZaakStatusType status) {
    switch (status) {
      case INCOMPLEET:
        return DossierStatusType.INCOMPLETE;
      case WACHTKAMER:
        return DossierStatusType.ON_HOLD;
      case OPGENOMEN:
        return DossierStatusType.CREATED;
      case GEWEIGERD:
        return DossierStatusType.REFUSED;
      case VERWERKT:
      case VERWERKT_IN_GBA:
        return DossierStatusType.PROCESSED;
      case GEANNULEERD:
        return DossierStatusType.CANCELLED;
      case GEPREVALIDEERD:
      case DOCUMENT_ONTVANGEN:
      case INBEHANDELING:
      case ONBEKEND:
      default:
        return DossierStatusType.PROCESSING;
    }
  }

  public static GbaRestZaakStatusType toGbaStatus(DossierStatusType status) {
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
