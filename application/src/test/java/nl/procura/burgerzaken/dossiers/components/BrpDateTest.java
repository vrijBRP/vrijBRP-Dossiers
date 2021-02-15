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

package nl.procura.burgerzaken.dossiers.components;

import static nl.procura.burgerzaken.dossiers.components.BrpDate.ADJUSTER.TO_END_PERIOD;
import static nl.procura.burgerzaken.dossiers.components.BrpDate.ADJUSTER.TO_START_PERIOD;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class BrpDateTest {

  @Test
  public void canParseBrpDates() {
    BrpDate validISO = BrpDate.from("20160405");
    BrpDate inValidISO = BrpDate.from("20160432");
    BrpDate unknownDate = BrpDate.from("0");
    BrpDate undefinedDate = BrpDate.from("-1");
    BrpDate emptyDate = BrpDate.from("");
    BrpDate wrongLength = BrpDate.from("2016");
    BrpDate onlyYear = BrpDate.from("20160000");
    BrpDate onlyYearAndMonthApril = BrpDate.from("20160400");
    BrpDate onlyYearAndMonthMarch = BrpDate.from("20160300");

    assertTrue(validISO.isValidISO());
    assertTrue(validISO.isValidBRP());
    assertFalse(validISO.isEmpty());
    assertFalse(validISO.isUnknown());
    assertTrue(validISO.getLocalDate().isPresent());
    assertTrue(validISO.getLocalDate(TO_START_PERIOD).isPresent());
    assertTrue(validISO.getLocalDate(TO_END_PERIOD).isPresent());
    assertEquals(2016, validISO.getYear().orElse(null));
    assertEquals(4, validISO.getMonth().orElse(null));
    assertEquals(5, validISO.getDay().orElse(null));
    assertEquals(LocalDate.of(2016, 4, 5),
        validISO.getLocalDate(TO_START_PERIOD).orElse(null));
    assertEquals(LocalDate.of(2016, 4, 5),
        validISO.getLocalDate(TO_END_PERIOD).orElse(null));

    assertFalse(inValidISO.isValidISO());
    assertFalse(inValidISO.isValidBRP());
    assertFalse(inValidISO.isEmpty());
    assertFalse(inValidISO.isUnknown());
    assertFalse(inValidISO.getLocalDate().isPresent());
    assertFalse(inValidISO.getLocalDate(TO_START_PERIOD).isPresent());
    assertFalse(inValidISO.getLocalDate(TO_END_PERIOD).isPresent());
    assertFalse(inValidISO.getYear().isPresent());
    assertFalse(inValidISO.getMonth().isPresent());
    assertFalse(inValidISO.getDay().isPresent());

    assertFalse(wrongLength.isValidISO());
    assertFalse(wrongLength.isValidBRP());
    assertFalse(wrongLength.isEmpty());
    assertFalse(wrongLength.isUnknown());
    assertFalse(wrongLength.getLocalDate().isPresent());
    assertFalse(wrongLength.getLocalDate(TO_START_PERIOD).isPresent());
    assertFalse(wrongLength.getLocalDate(TO_END_PERIOD).isPresent());
    assertFalse(wrongLength.getYear().isPresent());
    assertFalse(wrongLength.getMonth().isPresent());
    assertFalse(wrongLength.getDay().isPresent());

    assertFalse(onlyYear.isValidISO());
    assertTrue(onlyYear.isValidBRP());
    assertFalse(onlyYear.isEmpty());
    assertFalse(onlyYear.isUnknown());
    assertFalse(onlyYear.getLocalDate().isPresent());
    assertTrue(onlyYear.getYear().isPresent());
    assertFalse(onlyYear.getMonth().isPresent());
    assertFalse(onlyYear.getDay().isPresent());
    assertEquals(2016, onlyYear.getYear().get());
    assertEquals(LocalDate.of(2016, 1, 1),
        onlyYear.getLocalDate(TO_START_PERIOD).orElse(null));
    assertEquals(LocalDate.of(2016, 12, 31),
        onlyYear.getLocalDate(TO_END_PERIOD).orElse(null));

    assertFalse(onlyYearAndMonthApril.isValidISO());
    assertTrue(onlyYearAndMonthApril.isValidBRP());
    assertFalse(onlyYearAndMonthApril.isEmpty());
    assertFalse(onlyYearAndMonthApril.isUnknown());
    assertFalse(onlyYearAndMonthApril.getLocalDate().isPresent());
    assertTrue(onlyYearAndMonthApril.getYear().isPresent());
    assertTrue(onlyYearAndMonthApril.getMonth().isPresent());
    assertFalse(onlyYearAndMonthApril.getDay().isPresent());
    assertEquals(4, onlyYearAndMonthApril.getMonth().get());
    assertEquals(LocalDate.of(2016, 4, 1),
        onlyYearAndMonthApril.getLocalDate(TO_START_PERIOD).orElse(null));
    assertEquals(LocalDate.of(2016, 4, 30),
        onlyYearAndMonthApril.getLocalDate(TO_END_PERIOD).orElse(null));
    assertEquals(LocalDate.of(2016, 3, 31),
        onlyYearAndMonthMarch.getLocalDate(TO_END_PERIOD).orElse(null));

    assertFalse(emptyDate.isValidISO());
    assertFalse(emptyDate.isValidBRP());
    assertTrue(emptyDate.isEmpty());
    assertFalse(emptyDate.isUnknown());
    assertFalse(emptyDate.getLocalDate().isPresent());
    assertFalse(emptyDate.getYear().isPresent());
    assertFalse(emptyDate.getMonth().isPresent());
    assertFalse(emptyDate.getDay().isPresent());

    assertFalse(undefinedDate.isValidISO());
    assertFalse(undefinedDate.isValidBRP());
    assertTrue(undefinedDate.isEmpty());
    assertFalse(undefinedDate.isUnknown());
    assertFalse(undefinedDate.getLocalDate().isPresent());
    assertFalse(undefinedDate.getYear().isPresent());
    assertFalse(undefinedDate.getMonth().isPresent());
    assertFalse(undefinedDate.getDay().isPresent());

    assertFalse(unknownDate.isValidISO());
    assertTrue(unknownDate.isValidBRP());
    assertFalse(unknownDate.isEmpty());
    assertTrue(unknownDate.isUnknown());
    assertFalse(unknownDate.getLocalDate().isPresent());
    assertFalse(unknownDate.getYear().isPresent());
    assertFalse(unknownDate.getMonth().isPresent());
    assertFalse(unknownDate.getDay().isPresent());
  }
}
