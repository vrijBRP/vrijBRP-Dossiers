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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import nl.procura.burgerzaken.dossiers.GbaSource;
import nl.procura.burgerzaken.dossiers.api.external.v1.birth.ApiUnbornAcknowledgement;

@ContextConfiguration(initializers = GbaSource.class)
public class BirthInfoAcknowledgementResourceV1Test extends BaseResourceTest {

  private static final String URI_ACKNOWLEGDEMENT = "/api/v1/births/info/acknowledgement";

  @Test
  void acknowledgementExistsMustReturnAcknowledgement() throws IOException {
    // given
    GbaSource.enqueueJsonResponse(getClass().getResource("acknowledgement-search.json"));
    GbaSource.enqueueJsonResponse(getClass().getResource("acknowledgement-complete.json"));
    // when
    ApiUnbornAcknowledgement acknowledgement = newMockTest()
        .queryParam("bsnMother", "999990639")
        .get(URI_ACKNOWLEGDEMENT)
        .documentation("birth_info_acknowledgement1")
        .status(status().isOk())
        .toClass(ApiUnbornAcknowledgement.class);
    // then
    assertEquals("ABC-203", acknowledgement.getDossierId());
    assertEquals("999993872", acknowledgement.getAcknowledgerBsn());
    assertEquals("Meijden", acknowledgement.getNameSelection().getLastname());
    assertEquals("van der", acknowledgement.getNameSelection().getPrefix());
    assertNull(acknowledgement.getNameSelection().getTitlePredicate());
  }

  @Test
  void acknowledgementNotFoundMustReturnNotFound() {
    // given
    GbaSource.enqueueJsonResponse(getClass().getResource("acknowledgement-search-empty.json"));
    // when, then
    newMockTest()
        .queryParam("bsnMother", "999993653")
        .get(URI_ACKNOWLEGDEMENT)
        .documentation("birth_info_acknowledgement2")
        .status(status().isNotFound());
  }
}
