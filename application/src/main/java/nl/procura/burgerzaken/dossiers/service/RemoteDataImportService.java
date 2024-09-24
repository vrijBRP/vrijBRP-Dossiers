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

package nl.procura.burgerzaken.dossiers.service;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.converters.GbaRestDataImportConverter;
import nl.procura.burgerzaken.dossiers.model.dataimport.DataImport;
import nl.procura.burgerzaken.dossiers.service.dataimport.DataImportService;
import nl.procura.gba.web.rest.v2.model.dataimport.GbaRestDataImport;

@Service
public class RemoteDataImportService implements DataImportService {

  private final GbaClient                  client;
  private final GbaRestDataImportConverter converter;

  public RemoteDataImportService(GbaClient client, GbaRestDataImportConverter converter) {
    this.client = client;
    this.converter = converter;
  }

  @Override
  public DataImport add(DataImport dataImport) {
    GbaRestDataImport gbaDataImport = GbaRestDataImportConverter.toGbaRestZaak(dataImport);
    return converter.toDomainModel(client.dataImport().addImport(gbaDataImport).getInhoud());
  }
}
