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

package nl.procura.burgerzaken.dossiers.service;

import static nl.procura.burgerzaken.dossiers.PersonRecordSource.enqueue;
import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_4;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.PersonRecordSource;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRequest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;

@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = PersonRecordSource.class)
@AutoConfigureMockMvc
class ProcuraWsServiceTest {

  @Autowired
  ProcuraWsService service;

  @Test
  @SneakyThrows
  void canGetPersonLists() {

    enqueueByBsn();
    enqueueByBsn();

    List<GbaWsPersonList> personListList = service.get(TEST_BSN_4);
    assertEquals(1, personListList.size());
    assertEquals(String.valueOf(TEST_BSN_4), personListList.get(0)
        .getCurrentRec(GBACat.PERSOON)
        .map(rec -> rec.getElemValue(GBAElem.BSN)).get());

    GbaWsPersonListRequest request = new GbaWsPersonListRequest();
    request.setIds(Collections.singletonList(TEST_BSN_4));
    List<GbaWsPersonList> personListList2 = service.get(request);
    assertEquals(1, personListList2.size());
    assertEquals(String.valueOf(TEST_BSN_4), personListList2.get(0)
        .getCurrentRec(GBACat.PERSOON)
        .map(rec -> rec.getElemValue(GBAElem.BSN)).get());
  }

  private void enqueueByBsn() throws IOException {
    ClassPathResource resource = new ClassPathResource("person_ws_000000048.json");
    String personWsResponse = IOUtils.toString(resource.getURL(), StandardCharsets.UTF_8);

    enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .setBody(personWsResponse));
  }
}
