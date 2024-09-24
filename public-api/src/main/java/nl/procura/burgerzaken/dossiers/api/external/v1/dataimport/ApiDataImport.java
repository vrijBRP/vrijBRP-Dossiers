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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossierDocument;
import nl.procura.burgerzaken.dossiers.model.dataimport.DataImport;
import nl.procura.burgerzaken.dossiers.model.dataimport.DataImportRecord;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierDocument;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "DataImport")
public class ApiDataImport {

  @NotBlank
  @NotNull(message = "name is required")
  private String name;

  @NotBlank
  @NotNull(message = "type is required")
  private String type;

  @Valid
  @NotEmpty
  @NotNull(message = "records is required")
  private List<ApiDataImportRecord> records;

  private List<String> remarks;

  public static ApiDataImport of(DataImport dataImport) {
    return ApiDataImport.builder()
        .name(dataImport.getName())
        .type(dataImport.getType())
        .records(dataImport.getRecords()
            .stream().map(ApiDataImportRecord::of)
            .collect(Collectors.toList()))
        .remarks(dataImport.getRemarks())
        .build();
  }

  public DataImport createNew() {
    DataImport dataImport = new DataImport();
    dataImport.setName(name);
    dataImport.setType(type);
    dataImport.setRecords(records.stream()
        .map(ApiDataImport::getDataImportRecord)
        .collect(Collectors.toList()));
    dataImport.setRemarks(remarks);
    return dataImport;
  }

  private static DataImportRecord getDataImportRecord(ApiDataImportRecord r) {
    return new DataImportRecord(r.getValues(), r.getRemarks(), toDocuments(r));
  }

  private static List<DossierDocument> toDocuments(ApiDataImportRecord r) {
    return Optional.ofNullable(r.getDocuments())
        .orElse(new ArrayList<>())
        .stream().map(ApiDossierDocument::toDossierDocument)
        .collect(Collectors.toList());
  }
}
