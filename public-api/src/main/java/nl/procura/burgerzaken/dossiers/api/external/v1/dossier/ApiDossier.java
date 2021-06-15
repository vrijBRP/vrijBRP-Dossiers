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

package nl.procura.burgerzaken.dossiers.api.external.v1.dossier;

import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.util.Constants.Formats;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Dossier")
public class ApiDossier {

  @Schema(description = "The ID of the dossier", example = "dossier-1234")
  private String dossierId;

  @Schema(description = "The reference IDs provided by the client")
  private Set<ApiReferenceId> referenceIds;

  @Schema(description = "The type of the dossier")
  private ApiDossierType type;

  @Schema(description = "The status of the dossier")
  private ApiDossierStatus status;

  @Schema(required = true, description = "Starting date of the dossier")
  @NotNull(message = "startDate is mandatory")
  private LocalDate startDate;

  @Schema(description = "Date / time the dossier is added",
      example = "2020-01-01T00:00:00")
  @JsonFormat(pattern = Formats.DATE_TIME_FORMAT)
  private LocalDateTime entryDateTime;

  public static ApiDossier of(Dossier dossier) {
    DossierType dossierType = dossier.getDossierType();
    DossierStatus status = dossier.getStatus();
    ApiDossierBuilder<?, ?> builder = ApiDossier.builder()
        .dossierId(dossier.getCaseNumber())
        .type(ApiDossierType.builder()
            .code(String.valueOf(dossierType.getCode()))
            .description(dossierType.getDescription())
            .build())
        .startDate(dossier.getDateStart())
        .entryDateTime(dossier.getDateAdded())
        .status(ApiDossierStatus.builder()
            .code(String.valueOf(status.getCode()))
            .description(status.getDescription())
            .build());
    if (!dossier.getReferences().isEmpty()) {
      builder.referenceIds(dossier.getReferences().stream()
          .map(ApiReferenceId::of)
          .collect(toSet()));
    }
    return builder.build();
  }

  public Dossier createNew(DossierType type, Client client) {
    Dossier dossier = new Dossier(type, client);
    dossier.setCaseNumber(dossierId);
    dossier.setDateStart(startDate);
    if (referenceIds != null) {
      referenceIds.forEach(reference -> dossier.addReference(reference.getId(), reference.getDescription()));
    }
    return dossier;
  }
}
