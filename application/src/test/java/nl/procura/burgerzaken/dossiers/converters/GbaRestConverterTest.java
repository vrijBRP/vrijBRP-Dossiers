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

package nl.procura.burgerzaken.dossiers.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class GbaRestConverterTest {

  @Test
  public void mustReturnDateAndTimes() {
    assertEquals("2020-01-01T00:00", GbaRestConverter.toLocalDateTime(20200101, null).toString());
    assertEquals("2020-01-01T00:00", GbaRestConverter.toLocalDateTime(20200101, -1).toString());
    assertEquals("2020-01-01T10:11:12", GbaRestConverter.toLocalDateTime(20200101, 101112).toString());
    assertEquals("10:11:01", GbaRestConverter.toLocalTime(101101).toString());
    assertEquals("09:10", GbaRestConverter.toLocalTime("09:10").toString());
    assertEquals("91000", GbaRestConverter.toIntegerTime("09:10").toString());
    assertNull(GbaRestConverter.toLocalDateTime(null, null));
    assertNull(GbaRestConverter.toIntegerTime("-1"));
    assertNull(GbaRestConverter.toLocalTime(-1));
    assertNull(GbaRestConverter.toLocalTime("-1"));
  }
}
