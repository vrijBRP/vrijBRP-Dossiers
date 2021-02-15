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

package nl.procura.burgerzaken.dossiers.api.admin.v1.dossier;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.util.Constants.Formats;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Dossier")
public class AdminApiDossier {

  @Schema(description = "The ID of the dossier", example = "123e4567-e89b-12d3-a456-556642440000")
  private String dossierId;

  @Schema(description = "The reference IDs provided by the client")
  private Set<AdminApiReferenceId> referenceIds;

  @Schema(description = "The type of the dossier")
  private AdminDossierType type;

  @Schema(description = "The status of the dossier")
  private AdminApiDossierStatus status;

  @Schema(required = true, description = "Starting date of the dossier")
  @JsonFormat(pattern = Formats.DATE_FORMAT)
  private LocalDate startDate;

  @Schema(description = "Date / time the dossier is added",
      example = "2020-01-01T00:00:00")
  @JsonFormat(pattern = Formats.DATE_TIME_FORMAT)
  private LocalDateTime entryDateTime;

  public static AdminApiDossier of(Dossier dossier) {
    DossierType dossierType = dossier.getDossierType();
    DossierStatus status = dossier.getStatus();
    AdminApiDossierBuilder<?, ?> builder = AdminApiDossier.builder()
        .dossierId(dossier.getCaseNumber())
        .type(AdminDossierType.builder()
            .code(String.valueOf(dossierType.getCode()))
            .description(dossierType.getDescription())
            .build())
        .startDate(dossier.getDateStart())
        .entryDateTime(dossier.getDateAdded())
        .status(AdminApiDossierStatus.of(status));
    if (dossier.getReferences() != null) {
      builder.referenceIds(
          dossier.getReferences().stream()
              .map(AdminApiReferenceId::of)
              .collect(toSet()));
    }
    return builder.build();
  }

  public Dossier withNewId(DossierType type, Client client) {
    if (dossierId != null) {
      throw new ApiException(ApiErrorType.BAD_REQUEST, format("New dossier shouldn't have an ID (%s)", dossierId));
    }
    dossierId = UUID.randomUUID().toString();
    Dossier dossier = new Dossier(type, client);
    dossier.setDateStart(startDate);
    dossier.setDateAdded(entryDateTime);
    dossier.setCaseNumber(dossierId);
    dossier.setPeople(new ArrayList<>());
    if (referenceIds != null) {
      referenceIds.forEach(reference -> dossier.addReference(reference.getId(), reference.getDescription()));
    }
    return dossier;
  }
}
