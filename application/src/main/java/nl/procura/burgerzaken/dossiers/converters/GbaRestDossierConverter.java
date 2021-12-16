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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.*;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestZaakStatusConverter.toGbaStatus;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestZaakStatusConverter.toStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierReference;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakAlgemeen;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakId;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;

@Component
public class GbaRestDossierConverter {

  private final List<GbaConverter<?>> converters;

  private final List<GbaRestZaakType> supportedTypes;

  public GbaRestDossierConverter(List<GbaConverter<?>> converters) {
    this.converters = unmodifiableList(converters);
    supportedTypes = converters.stream()
        .map(GbaConverter::zaakType)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<GbaRestZaakType> supportedTypes() {
    return supportedTypes;
  }

  public GbaRestZaakType toGbaZaakType(DossierType type) {
    return converters.stream()
        .filter(gbaConverter -> gbaConverter.dossierType() == type)
        .map(GbaConverter::zaakType)
        .findFirst()
        .orElseThrow();
  }

  public static GbaRestZaakAlgemeen toGbaRestZaakAlgemeen(Dossier dossier, GbaRestZaakType zaakType) {
    GbaRestZaakAlgemeen algemeen = new GbaRestZaakAlgemeen();
    algemeen.setZaakId(dossier.getCaseNumber());
    Client client = dossier.getClient();
    algemeen.setBron(client.getApplication());
    algemeen.setLeverancier(client.getCustomer());
    algemeen.setType(zaakType);
    algemeen.setDatumInvoer(toIntegerDate(dossier.getDateAdded()));
    algemeen.setTijdInvoer(toIntegerTime(dossier.getDateAdded()));
    algemeen.setDatumIngang(toIntegerDate(dossier.getDateStart()));
    algemeen.setStatus(toGbaStatus(dossier.getStatus().getType()));
    Set<DossierReference> references = dossier.getReferences();
    if (!references.isEmpty()) {
      algemeen.setIds(references.stream()
          .map(GbaRestConverter::toGbaId)
          .collect(toList()));
    }
    return algemeen;
  }

  public Dossier toDossier(GbaRestZaak zaak, DossierSearchRequest request) {
    DossierType dossierType = toDossierType(zaak.getAlgemeen().getType());
    Dossier dossier = toDossier(zaak, dossierType);
    boolean isDossier = dossierType != null
        && (!request.isOnlyDeclarant() || isRelevantForBsn(zaak, request.getBsns()));
    return isDossier ? dossier : null;
  }

  public static Dossier toDossier(GbaRestZaak zaak, DossierType dossierType) {
    GbaRestZaakAlgemeen algemeen = zaak.getAlgemeen();
    Dossier dossier = new Dossier();
    dossier.setCaseNumber(algemeen.getZaakId());
    dossier.setDescription(algemeen.getOmschrijving());
    dossier.setDossierType(dossierType);
    dossier.setDateAdded(toLocalDateTime(algemeen.getDatumInvoer(), algemeen.getTijdInvoer()));
    dossier.setDateStart(toLocalDate(algemeen.getDatumIngang()));
    dossier.setStatus(toStatus(algemeen.getStatussen()));
    List<GbaRestZaakId> ids = algemeen.getIds();
    if (ids != null) {
      Set<DossierReference> references = new HashSet<>();
      for (GbaRestZaakId id : ids) {
        DossierReference reference = new DossierReference();
        reference.setReferenceNumber(id.getId());
        reference.setDescription(id.getSysteem());
        references.add(reference);
      }
      dossier.setReferences(references);
    }
    return dossier;
  }

  private boolean isRelevantForBsn(GbaRestZaak zaak, List<Bsn> bsns) {
    return converters.stream()
        .filter(converter -> converter.zaakType() == zaak.getAlgemeen().getType())
        .filter(converters -> bsns != null && bsns.size() > 0)
        .map(gbaConverter -> gbaConverter.isRelevantForBsn(zaak, bsns))
        .findFirst()
        .orElse(true);
  }

  public DossierType toDossierType(GbaRestZaakType type) {
    return converters.stream()
        .filter(converter -> converter.zaakType() == type)
        .map(GbaConverter::dossierType)
        .findFirst()
        .orElse(null);
  }
}
