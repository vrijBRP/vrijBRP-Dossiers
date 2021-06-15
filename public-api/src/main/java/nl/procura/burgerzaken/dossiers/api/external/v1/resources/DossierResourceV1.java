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

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;
import static nl.procura.burgerzaken.dossiers.util.BsnUtils.toBsnList;
import static nl.procura.burgerzaken.dossiers.util.Constants.Errors.BAD_REQUEST;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiDatePeriod;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiDateTimePeriod;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiRequestPaging;
import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiResultPage;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossierDocument;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.search.ApiSearchRequest;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.search.ApiSearchResponse;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierDocument;
import nl.procura.burgerzaken.dossiers.service.DossierDocumentService;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDatePeriod;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDateTimePeriod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dossiers",
    description = "Actions related to dossiers")
@RestController
@RequestMapping("/api/v1/dossiers")
public class DossierResourceV1 {

  private final DossierService         dossierService;
  private final DossierDocumentService documentService;

  public DossierResourceV1(DossierService dossierService, DossierDocumentService documentService) {
    this.dossierService = dossierService;
    this.documentService = documentService;
  }

  @Operation(
      operationId = "searchDossiers",
      summary = "Search for dossiers",
      description = "Search for dossiers")
  @PostMapping(
      value = "/search",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Resources successfully returned"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiSearchResponse> searchDossiers(@Valid @RequestBody ApiSearchRequest request) {

    ApiRequestPaging paging = ofNullable(request.getPaging())
        .orElse(ApiRequestPaging.defaultPaging());

    nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest r = nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest
        .builder()
        .pageRequest(PageRequest.of(paging.getPageNumber(), paging.getPageSize()))
        .startDatePeriod(getDatePeriod(request.getStartDatePeriod()))
        .entryDateTimePeriod(getDateTimePeriod(request.getEntryDateTimePeriod()))
        .bsns(toBsnList(request.getBsns()))
        .statusses(request.getStatusses())
        .types(request.getTypes())
        .dossierIds(request.getDossierIds())
        .build();

    Page<Dossier> page = dossierService.find(r);
    List<ApiDossier> apiDossiers = new ArrayList<>();
    page.forEach(d -> apiDossiers.add(ApiDossier.of(d)));

    ApiResultPage<ApiDossier> apiPage = ApiResultPage.<ApiDossier> builder()
        .elements(page.getNumberOfElements())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageSize(page.getSize())
        .pageNumber(page.getNumber())
        .content(apiDossiers)
        .build();

    return new ResponseEntity<>(ApiSearchResponse.builder()
        .result(apiPage).build(), HttpStatus.OK);
  }

  @Operation(
      operationId = "addDocumentToDossier",
      summary = "Add document to dossier",
      description = "Add document to dossier")
  @PostMapping(
      value = "/{dossierId}/documents",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Document successfully created"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<ApiDossierDocument> addDocumentToDossier(@PathVariable String dossierId,
      @Valid @RequestBody ApiDossierDocument document) {
    DossierDocument created = documentService.addDocument(dossierId, document.toDossierDocument());
    return new ResponseEntity<>(ApiDossierDocument.of(created), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "getDocumentsOfDossier",
      summary = "Get documents of the dossier",
      description = "Get documents of the dossier")
  @GetMapping(
      value = "/{dossierId}/documents",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "List of documents"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<List<ApiDossierDocument>> getDocumentsOfDossier(@PathVariable String dossierId) {
    List<ApiDossierDocument> documents = documentService.getDocuments(dossierId).stream()
        .map(ApiDossierDocument::of)
        .collect(toUnmodifiableList());
    return new ResponseEntity<>(documents, HttpStatus.OK);
  }

  @Operation(
      operationId = "getDossierDocument",
      summary = "Get a document of the dossier",
      description = "Get a document of the dossier as attachment. Filename is always 'document' with the original extension. E.g. 'document.txt'")
  @GetMapping(
      value = "/{dossierId}/documents/{documentId}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Document attachment"),
      @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  })
  public ResponseEntity<Resource> getDossierDocument(@PathVariable String dossierId, @PathVariable String documentId) {
    InputStreamResource resource = documentService.getStream(dossierId, documentId);
    ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        .header(HttpHeaders.CONTENT_DISPOSITION, format("attachment; filename=\"%s\"", resource.getFilename()));
    long contentLength = contentLength(resource);
    if (contentLength > -1) {
      builder.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
    }
    return builder.body(resource);
  }

  private static long contentLength(Resource resource) {
    try {
      return resource.contentLength();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static LocalDatePeriod getDatePeriod(ApiDatePeriod period) {
    return period == null ? null : period.toLocalDatePeriod();
  }

  private static LocalDateTimePeriod getDateTimePeriod(ApiDateTimePeriod period) {
    return period == null ? null : period.toLocalDateTimePeriod();
  }
}
