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

package nl.procura.burgerzaken.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonAssertions {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JsonAssertions() {
  }

  public static void assertJsonEquals(URL expected, String actual) {
    try {
      assertNotNull(expected, "expected is null");
      assertNotNull(actual, "actual is null");
      assertEquals(MAPPER.readTree(expected), MAPPER.readTree(actual));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
