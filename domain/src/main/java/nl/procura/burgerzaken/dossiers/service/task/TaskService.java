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

package nl.procura.burgerzaken.dossiers.service.task;

import static java.util.Collections.singletonList;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierStatusType.INCOMPLETE;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierStatusType.ON_HOLD;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.INTER_MUNICIPAL_RELOCATION;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.INTRA_MUNICIPAL_RELOCATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.relocations.ConsentRelocation;
import nl.procura.burgerzaken.dossiers.model.task.Task;
import nl.procura.burgerzaken.dossiers.service.ConsentRelocationService;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.burgerzaken.gba.numbers.Bsn;

@Service
public class TaskService {

  private DossierService dossierService;

  public TaskService(DossierService dossierService) {
    this.dossierService = dossierService;
  }

  public Page<Task> find(TaskSearchRequest request) {
    List<Task> tasks = new ArrayList<>();

    DossierSearchRequest search = new DossierSearchRequest();
    search.setBsns(singletonList(request.getBsns().get(0)));
    search.setTypes(Arrays.asList(
        INTRA_MUNICIPAL_RELOCATION.getCode(),
        INTER_MUNICIPAL_RELOCATION.getCode()));
    search.setStatusses(Arrays.asList(
        INCOMPLETE.getCode(),
        ON_HOLD.getCode()));

    Page<Dossier> resp = dossierService.find(search);
    resp.getContent().forEach(dossier -> {
      ConsentRelocationService<ConsentRelocation> consentRelocationService = dossierService
          .getDossierTypeService(dossier.getDossierType(), ConsentRelocationService.class);
      ConsentRelocation consent = consentRelocationService.findByCaseNumber(dossier.getCaseNumber());
      getConsentTask(request, dossier, consent).ifPresent(tasks::add);
    });

    return new PageImpl<>(tasks);
  }

  private Optional<Task> getConsentTask(TaskSearchRequest request, Dossier dossier, ConsentRelocation relocation) {
    Optional<Person> mainOccupant = relocation.getMainOccupant();
    if (BooleanUtils.isTrue(relocation.getLiveIn()) && mainOccupant.isPresent()) {
      Bsn bsnMainOccupant = mainOccupant.get().getBsn();
      if ("P".equals(relocation.getConsent())
          && request.isMatchWithBsn(bsnMainOccupant)
          && request.isMatchWithStatus(TaskStatus.PLANNED)
          && request.isMatchWithType(TaskType.RELOCATION_LODGING_CONSENT)) {
        Task task = new Task();
        task.setDossierId(dossier.getCaseNumber());
        task.setDossierType(dossier.getDossierType());
        task.setBsns(singletonList(bsnMainOccupant));
        task.setTaskType(TaskType.RELOCATION_LODGING_CONSENT);
        task.setStatus(TaskStatus.PLANNED);
        return Optional.of(task);
      }
    }
    return Optional.empty();
  }
}
