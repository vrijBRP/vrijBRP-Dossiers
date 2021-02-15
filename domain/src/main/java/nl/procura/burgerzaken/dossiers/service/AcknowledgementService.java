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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.model.birth.Acknowledgement;
import nl.procura.burgerzaken.dossiers.model.birth.BirthAcknowledgementType;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.repositories.AcknowledgementRepository;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDateTimePeriod;

@Service
public class AcknowledgementService {

  private final DossierService            dossierService;
  private final AcknowledgementRepository repository;

  public AcknowledgementService(DossierService dossierService, AcknowledgementRepository repository) {
    this.dossierService = dossierService;
    this.repository = repository;
  }

  public Optional<Acknowledgement> findUnborn(Long bsn) {

    DossierSearchRequest request = new DossierSearchRequest();
    request.setBsns(singletonList(bsn));
    request.setTypes(singletonList(DossierType.ACKNOWLEDGEMENT.getCode()));
    request.setStatusses(asList(DossierStatus.CREATED.getCode(), DossierStatus.PROCESSED.getCode()));
    request.setEntryDateTimePeriod(
        new LocalDateTimePeriod(LocalDateTime.now().minus(1, ChronoUnit.YEARS), LocalDateTime.now()));
    Page<Dossier> dossiers = dossierService.find(request);
    List<Acknowledgement> acknowledgements = dossiers.stream()
        .map(dossier -> repository.findByCaseNumber(dossier.getCaseNumber()))
        .filter(acknowledgement -> acknowledgement.getType() == BirthAcknowledgementType.UNBORN)
        .filter(acknowledgement -> acknowledgement.getMother().getBsn().equals(bsn))
        .filter(acknowledgement -> acknowledgement.acknowledger().isPresent())
        .collect(Collectors.toUnmodifiableList());
    if (acknowledgements.size() > 1) {
      throw new ApiException(ApiErrorType.INTERNAL_ERROR, "More than 1 acknowledgement found for " + bsn);
    }
    return acknowledgements.isEmpty() ? Optional.empty() : Optional.of(acknowledgements.get(0));
  }
}
