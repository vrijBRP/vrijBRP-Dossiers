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

package nl.procura.burgerzaken.dossiers.api.external.v1.resources;

import static java.util.Optional.ofNullable;
import static nl.procura.burgerzaken.dossiers.model.base.PersistableEnum.valueOfCode;
import static nl.procura.burgerzaken.dossiers.util.Constants.Errors.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiRequestPaging;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiResultPage;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.ApiTask;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.search.ApiSearchRequest;
import nl.procura.burgerzaken.dossiers.api.external.v1.task.search.ApiSearchResponse;
import nl.procura.burgerzaken.dossiers.model.task.Task;
import nl.procura.burgerzaken.dossiers.service.task.TaskService;
import nl.procura.burgerzaken.dossiers.service.task.TaskStatus;
import nl.procura.burgerzaken.dossiers.service.task.TaskType;
import nl.procura.burgerzaken.dossiers.util.BsnUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tasks",
    description = "Tasks related to dossiers or residents")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskResourceV1 {

  private final TaskService taskService;

  public TaskResourceV1(TaskService taskService) {
    this.taskService = taskService;
  }

  @Operation(
      operationId = "searchTasks",
      summary = "Search for outstanding tasks",
      description = "Search for tasks")
  @PostMapping(
      value = "/search",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resource successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiSearchResponse> searchTasks(@Valid @RequestBody ApiSearchRequest request) {

    ApiRequestPaging paging = ofNullable(request.getPaging())
        .orElse(ApiRequestPaging.defaultPaging());

    nl.procura.burgerzaken.dossiers.service.task.TaskSearchRequest r = nl.procura.burgerzaken.dossiers.service.task.TaskSearchRequest
        .builder()
        .pageRequest(PageRequest.of(paging.getPageNumber(), paging.getPageSize()))
        .bsns(BsnUtils.toBsnList(request.getBsns()))
        .types(Optional.ofNullable(request.getTypes())
            .orElse(new ArrayList<>())
            .stream()
            .map(type -> valueOfCode(TaskType.values(), type.getCode()))
            .collect(Collectors.toList()))
        .statusses(Optional.ofNullable(request.getStatusses())
            .orElse(new ArrayList<>())
            .stream().map(s -> valueOfCode(TaskStatus.values(), s.getCode()))
            .collect(Collectors.toList()))
        .build();

    Page<Task> page = taskService.find(r);
    List<ApiTask> apiTasks = new ArrayList<>();
    page.forEach(t -> apiTasks.add(ApiTask.of(t)));

    ApiResultPage<ApiTask> apiPage = ApiResultPage.<ApiTask> builder()
        .elements(page.getNumberOfElements())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageSize(page.getSize())
        .pageNumber(page.getNumber())
        .content(apiTasks)
        .build();

    return new ResponseEntity<>(ApiSearchResponse
        .builder()
        .result(apiPage)
        .build(), HttpStatus.OK);
  }
}
