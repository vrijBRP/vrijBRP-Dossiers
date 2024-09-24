/*
 * Copyright 2023 - 2024 Procura B.V.
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

package nl.procura.burgerzaken.dossiers.api.external.v1.dataimport;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossierDocument;
import nl.procura.burgerzaken.dossiers.model.dataimport.DataImportRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Record")
public class ApiDataImportRecord {

  @NotEmpty
  @NotNull(message = "values is required")
  private Map<String, String> values = new LinkedHashMap<>();

  private List<String> remarks;

  @Valid
  private List<ApiDossierDocument> documents;

  public static ApiDataImportRecord of(DataImportRecord dataImportRecord) {
    if (dataImportRecord != null) {
      return ApiDataImportRecord.builder()
          .values(dataImportRecord.getValues())
          .remarks(dataImportRecord.getRemarks())
          .documents(null)
          .build();
    }
    return null;
  }

}
