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

package nl.procura.burgerzaken.dossiers.converters;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.dataimport.DataImport;
import nl.procura.burgerzaken.dossiers.model.dataimport.DataImportRecord;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierDocument;
import nl.procura.gba.web.rest.v2.model.dataimport.GbaRestDataImport;
import nl.procura.gba.web.rest.v2.model.dataimport.GbaRestDataImportDocument;
import nl.procura.gba.web.rest.v2.model.dataimport.GbaRestDataImportRecord;
import nl.procura.gba.web.rest.v2.model.dataimport.GbaRestDataImportValue;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakDocument;

@Component
public class GbaRestDataImportConverter {

  public DataImport toDomainModel(GbaRestDataImport gbaDataImport) {
    return new DataImport(gbaDataImport.getName(),
        gbaDataImport.getType(),
        getRecords(gbaDataImport),
        gbaDataImport.getRemarks());
  }

  public static GbaRestDataImport toGbaRestZaak(DataImport dataImport) {
    GbaRestDataImport gbaDataImport = new GbaRestDataImport();
    gbaDataImport.setName(dataImport.getName());
    gbaDataImport.setType(dataImport.getType());
    gbaDataImport.setRecords(getRecords(dataImport));
    return gbaDataImport;
  }

  private static List<DataImportRecord> getRecords(GbaRestDataImport gbaDataImport) {
    return gbaDataImport.getRecords().stream()
        .map(gbaRecord -> new DataImportRecord(gbaRecord.getValues().entrySet().stream()
            .collect(toMap(Entry::getKey, value -> value.getValue().getValue(),
                (key, val) -> val, LinkedHashMap::new)),
            getRemarks(gbaRecord),
            null))
        .collect(Collectors.toList());
  }

  private static List<String> getRemarks(GbaRestDataImportRecord gbaRecord) {
    List<String> remarks = gbaRecord.getValues().entrySet().stream()
        .filter(value -> isNotBlank(value.getValue().getRemark()))
        .map(value -> value.getKey() + ": " + value.getValue().getRemark())
        .collect(Collectors.toList());
    return remarks.isEmpty() ? null : remarks;
  }

  private static List<GbaRestDataImportRecord> getRecords(DataImport dataImport) {
    return dataImport.getRecords().stream()
        .map(record -> new GbaRestDataImportRecord(record.getValues().entrySet().stream()
            .collect(toMap(Entry::getKey, value -> new GbaRestDataImportValue(value.getValue(), ""),
                (key, val) -> val, LinkedHashMap::new)),
            new ArrayList<>(),
            getDocuments(record)))
        .collect(Collectors.toList());
  }

  private static List<GbaRestDataImportDocument> getDocuments(DataImportRecord record) {
    return record.getDocuments().stream()
        .map(document -> new GbaRestDataImportDocument(toGbaRestZaakDocument(document), document.getContent()))
        .collect(Collectors.toList());
  }

  private static GbaRestZaakDocument toGbaRestZaakDocument(DossierDocument document) {
    GbaRestZaakDocument zaakDocument = new GbaRestZaakDocument();
    zaakDocument.setTitel(document.getTitle());
    zaakDocument.setBestandsnaam(document.getFilename());
    return zaakDocument;
  }
}
