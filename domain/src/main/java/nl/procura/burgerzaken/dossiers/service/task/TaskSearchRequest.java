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

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import nl.procura.burgerzaken.gba.numbers.Bsn;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class TaskSearchRequest {

  private List<String> bsns;

  private List<TaskStatus> statusses;

  private List<TaskType> types;

  private PageRequest pageRequest;

  public boolean isMatchWithBsn(String bsn) {
    return isEmpty(getBsns()) || getBsns()
        .stream().map(Bsn::new)
        .filter(Bsn::isCorrect)
        .anyMatch(b -> b.equals(new Bsn(bsn)));
  }

  public boolean isMatchWithStatus(TaskStatus taskStatus) {
    return isEmpty(getStatusses()) || getStatusses()
        .stream().map(TaskStatus::getCode)
        .anyMatch(code -> code.equals(taskStatus.getCode()));
  }

  public boolean isMatchWithType(TaskType taskType) {
    return isEmpty(getTypes()) || getTypes()
        .stream().map(TaskType::getCode)
        .anyMatch(code -> code.equals(taskType.getCode()));
  }
}
