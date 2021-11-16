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

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;

import nl.procura.burgerzaken.dossiers.model.dossier.DossierDocument;
import nl.procura.burgerzaken.dossiers.util.Constants;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "DossierDocument")
public class ApiDossierDocument {

  @Schema(description = "ID", example = "Unique identifier")
  private String id;

  @Schema(description = "Title", example = "Example title")
  private String title;

  @NotEmpty(message = "filename must not be empty")
  @Schema(required = true, description = "Filename", example = "file.pdf")
  private String filename;

  @Schema(description = "Date / time the status is added",
      example = "2020-01-01T00:00:00")
  @JsonFormat(pattern = Constants.Formats.DATE_TIME_FORMAT)
  private LocalDateTime entryDateTime;

  @NotEmpty(message = "content must not be empty")
  @Schema(required = true, description = "Base 64 encoded content")
  private byte[] content;

  public static ApiDossierDocument of(DossierDocument document) {
    return ApiDossierDocument.builder()
        .id(document.getId())
        .entryDateTime(document.getDateTime())
        .title(document.getTitle())
        .filename(document.getFilename())
        .content(document.getContent())
        .build();
  }

  public DossierDocument toDossierDocument() {
    return new DossierDocument(id, title, filename, entryDateTime, content);
  }
}
