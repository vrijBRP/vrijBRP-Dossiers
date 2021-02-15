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

package nl.procura.burgerzaken.dossiers.service;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.model.dossier.DossierDocument;
import nl.procura.burgerzaken.dossiers.repositories.DossierDocumentRepository;

@Service
public class DossierDocumentService {

  private final DossierDocumentRepository repository;

  public DossierDocumentService(DossierDocumentRepository repository) {
    this.repository = repository;
  }

  public DossierDocument addDocument(String caseNumber, DossierDocument document) {
    return repository.create(caseNumber, document);
  }

  public List<DossierDocument> getDocuments(String caseNumber) {
    return repository.findByCaseNumber(caseNumber);
  }

  public InputStreamResource getStream(String caseNumber, String documentId) {
    return repository.getStream(caseNumber, documentId);
  }
}
