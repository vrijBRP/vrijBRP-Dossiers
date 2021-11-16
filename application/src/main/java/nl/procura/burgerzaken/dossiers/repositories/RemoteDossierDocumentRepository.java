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

package nl.procura.burgerzaken.dossiers.repositories;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.converters.GbaRestConverter;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierDocument;
import nl.procura.gba.web.rest.v2.model.base.GbaRestAntwoord;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakDocumentToevoegenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakDocumentenZoekenAntwoord;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakDocument;

@Service
public class RemoteDossierDocumentRepository implements DossierDocumentRepository {

  private final GbaClient client;

  public RemoteDossierDocumentRepository(GbaClient client) {
    this.client = client;
  }

  @Override
  public DossierDocument create(String caseNumber, DossierDocument document) {
    GbaRestZaakDocumentToevoegenVraag request = new GbaRestZaakDocumentToevoegenVraag();
    GbaRestZaakDocument zaakDocument = new GbaRestZaakDocument();
    zaakDocument.setTitel(document.getTitle());
    zaakDocument.setBestandsnaam(document.getFilename());
    request.setDocument(zaakDocument);
    request.setInhoud(document.getContent());
    GbaRestZaakDocument created = client.documenten().addDocument(caseNumber, request).getInhoud();
    return toDossierDocument(created);
  }

  @Override
  public List<DossierDocument> findByCaseNumber(String caseNumber) {
    GbaRestAntwoord<GbaRestZaakDocumentenZoekenAntwoord> documentsByZaakId = client.documenten()
        .getDocumentsByZaakId(caseNumber);
    return documentsByZaakId
        .getInhoud()
        .getDocumenten()
        .stream()
        .map(RemoteDossierDocumentRepository::toDossierDocument)
        .collect(toUnmodifiableList());
  }

  @Override
  public InputStreamResource getStream(String caseNumber, String documentId) {
    return (InputStreamResource) client.documenten()
        .getDocumentById(caseNumber, documentId);

  }

  private static DossierDocument toDossierDocument(GbaRestZaakDocument restZaakDocument) {
    return new DossierDocument(restZaakDocument.getId(),
        restZaakDocument.getTitel(),
        restZaakDocument.getBestandsnaam(),
        GbaRestConverter.toLocalDateTime(restZaakDocument.getInvoerDatum(),
            restZaakDocument.getInvoerTijd()),
        null);
  }
}
