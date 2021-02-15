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

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;

public class BrpDate {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");

  /**
   * Adjust the date to make it a valid LocalDate.
   */
  public enum ADJUSTER {
    TO_START_PERIOD, // Add first month and day
    TO_END_PERIOD // Add last month and day
  }

  private boolean   empty;
  private boolean   unknown;
  private boolean   validISO;
  private boolean   validBRP;
  private LocalDate localDate;
  private Integer   year;
  private Integer   month;
  private Integer   day;

  public static BrpDate from(String date) {
    return from(NumberUtils.toInt(date, -1));
  }

  public static BrpDate from(int date) {
    return new BrpDate(date);
  }

  public boolean isEmpty() {
    return empty;
  }

  public boolean isValidISO() {
    return validISO;
  }

  public boolean isValidBRP() {
    return validBRP;
  }

  public boolean isUnknown() {
    return unknown;
  }

  public Optional<Integer> getYear() {
    return Optional.ofNullable(year);
  }

  public Optional<Integer> getMonth() {
    return Optional.ofNullable(month);
  }

  public Optional<Integer> getDay() {
    return Optional.ofNullable(day);
  }

  public Optional<LocalDate> getLocalDate() {
    return Optional.ofNullable(localDate);
  }

  public Optional<LocalDate> getLocalDate(ADJUSTER adjuster) {
    if (localDate != null) {
      return Optional.of(localDate);
    } else if (validBRP) {
      switch (adjuster) {
        case TO_START_PERIOD:
          return getYear().flatMap(y -> complementPeriod(y, 1, firstDayOfMonth()));
        case TO_END_PERIOD:
          return getYear().flatMap(y -> complementPeriod(y, 12, lastDayOfMonth()));
        default:
          throw new IllegalArgumentException(String.format("Illegal adjuster %s argument", adjuster));
      }
    }
    return Optional.empty();
  }

  private BrpDate(int date) {
    empty = date == -1;
    unknown = date == 0;
    validBRP = unknown;

    String stringDate = String.valueOf(date);

    if (stringDate.length() == 8) {
      localDate = toLocalDate(stringDate);
      validISO = localDate != null;
      validBRP = validBRP || validISO;
      year = parsePeriod(stringDate.substring(0, 4));
      month = parsePeriod(stringDate.substring(4, 6));
      day = parsePeriod(stringDate.substring(6, 8));

      if (!validISO && getYear().isPresent()) {

        boolean isMonth = getMonth().isPresent();
        boolean isDay = getDay().isPresent();

        if (isMonth && isDay) {
          validBRP = false;
          year = null;
          month = null;
          day = null;

        } else if (isMonth || !isDay) {
          validBRP = true;
        }
      }
    }
  }

  private Integer parsePeriod(String stringDate) {
    return Optional.of(Integer.parseInt(stringDate)).filter(i -> i > 0).orElse(null);
  }

  private LocalDate toLocalDate(String stringDate) {
    try {
      return LocalDate.from(DATE_FORMATTER.parse(stringDate));
    } catch (java.time.DateTimeException e) {
      return null;
    }
  }

  private Optional<LocalDate> complementPeriod(Integer y, int fallbackMonth, TemporalAdjuster dayAdjuster) {
    try {
      Integer m = getMonth().orElse(fallbackMonth);
      Integer d = LocalDate.of(y, m, 1).with(dayAdjuster).getDayOfMonth();
      return Optional.ofNullable(toLocalDate(String.format("%04d%02d%02d", y, m, d)));
    } catch (java.time.DateTimeException e) {
      return Optional.empty();
    }
  }
}
