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

import static nl.procura.burgerzaken.dossiers.model.base.PersistableEnum.valueOfCode;
import static nl.procura.gba.web.rest.v2.model.base.GbaRestEnum.toEnum;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import nl.procura.burgerzaken.dossiers.model.base.PersistableEnum;
import nl.procura.burgerzaken.dossiers.model.base.TableValue;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierReference;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.dossier.PersonRole;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDatePeriod;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDateTimePeriod;
import nl.procura.burgerzaken.gba.StringUtils;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gba.web.rest.v2.model.base.GbaRestEnum;
import nl.procura.gba.web.rest.v2.model.base.HeeftBsn;
import nl.procura.gba.web.rest.v2.model.base.HeeftContactgegevens;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestPeriode;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestTabelWaarde;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakId;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestContactgegevens;

public final class GbaRestConverter {

  private static final int               TIME_WITH_SECONDS_LENGTH    = 6;
  private static final String            INTEGER_TIME_WITH_SECONDS   = "HHmmss";
  private static final String            STRING_TIME_WITHOUT_SECONDS = "HH:mm";
  private static final DateTimeFormatter DATE_FORMATTER              = DateTimeFormatter.ofPattern("yyyyMMdd");

  private GbaRestConverter() {
  }

  public static LocalDateTime toLocalDateTime(Integer date, Integer time) {
    if (date == null) {
      return null;
    }
    LocalDate ld = toLocalDate(date);
    LocalTime lt = toLocalTime(time);
    return LocalDateTime.of(ld, lt == null ? toLocalTime(0) : lt);
  }

  public static LocalTime toLocalTime(String time) {
    if (time == null || toInt(time) < 0) {
      return null;
    }
    return LocalTime.parse(time, DateTimeFormatter.ISO_TIME);
  }

  public static LocalTime toLocalTime(Integer time) {
    if (time == null || time < 0) {
      return null;
    }
    String paddedTime = StringUtils.leftPad(time.toString(), TIME_WITH_SECONDS_LENGTH, '0');
    return LocalTime.from(DateTimeFormatter.ofPattern(INTEGER_TIME_WITH_SECONDS).parse(paddedTime));
  }

  public static LocalDate toLocalDate(Integer date) {
    if (date == null || date <= 0) {
      return null;
    }
    return LocalDate.from(DATE_FORMATTER.parse(date.toString()));
  }

  public static Integer toIntegerDate(LocalDateTime dateTime) {
    if (dateTime == null) {
      return null;
    }
    return Integer.valueOf(dateTime.format(DATE_FORMATTER));
  }

  public static Integer toIntegerDate(LocalDate date) {
    if (date == null) {
      return null;
    }
    return Integer.valueOf(date.format(DATE_FORMATTER));
  }

  public static Integer toIntegerTime(LocalDateTime dateTime) {
    if (dateTime == null) {
      return null;
    }
    return Integer.valueOf(dateTime.format(DateTimeFormatter.ofPattern(INTEGER_TIME_WITH_SECONDS)));
  }

  public static Integer toIntegerTime(LocalTime time) {
    if (time == null) {
      return null;
    }
    return Integer.valueOf(time.format(DateTimeFormatter.ofPattern(INTEGER_TIME_WITH_SECONDS)));
  }

  public static String toStringTimeWithoutSeconds(Integer time) {
    if (time == null) {
      return null;
    }
    return toLocalTime(time).format(DateTimeFormatter.ofPattern(STRING_TIME_WITHOUT_SECONDS));
  }

  public static Integer toIntegerTime(String time) {
    if (time == null) {
      return null;
    }
    return toIntegerTime(toLocalTime(time));
  }

  public static Optional<Person> toPersonWithContactinfo(HeeftContactgegevens personWithContactinfo,
      PersonRole personType) {
    Optional<Person> person = toPerson(personWithContactinfo, personType);
    person.ifPresent(p -> {
      GbaRestContactgegevens cg = personWithContactinfo.getContactgegevens();
      if (cg != null) {
        p.setEmail(cg.getEmail());
        p.setPhoneNumber(getTelephoneNumber(cg));
      }
    });
    return person;
  }

  public static Optional<Person> toPerson(HeeftBsn personWithBsn, PersonRole personType) {
    if (personWithBsn != null && personWithBsn.getBsn() != null) {
      Person person = new Person(personType);
      person.setBsn(new Bsn(personWithBsn.getBsn()));
      return Optional.of(person);
    }
    return Optional.empty();
  }

  public static GbaRestZaakId toGbaId(DossierReference reference) {
    GbaRestZaakId id = new GbaRestZaakId();
    id.setId(reference.getReferenceNumber());
    id.setSysteem(reference.getDescription());
    return id;
  }

  public static Optional<GbaRestPeriode> toGbaRestPeriode(LocalDatePeriod period) {
    if (period != null) {
      GbaRestPeriode periode = new GbaRestPeriode();
      if (period.getFrom() != null) {
        periode.setVan(toIntegerDate(period.getFrom()));
      }
      if (period.getTo() != null) {
        periode.setTm(toIntegerDate(period.getTo()));
      }
      return Optional.of(periode);
    }
    return Optional.empty();
  }

  public static Optional<GbaRestPeriode> toGbaRestPeriode(LocalDateTimePeriod period) {
    if (period != null) {
      GbaRestPeriode periode = new GbaRestPeriode();
      if (period.getFrom() != null) {
        periode.setVan(toIntegerDate(period.getFrom()));
      }
      if (period.getTo() != null) {
        periode.setTm(toIntegerDate(period.getTo()));
      }
      return Optional.of(periode);
    }
    return Optional.empty();
  }

  public static String getTelephoneNumber(GbaRestContactgegevens contactgegevens) {
    String tel1 = contactgegevens.getTelefoonMobiel();
    String tel2 = contactgegevens.getTelefoonThuis();
    return isNotBlank(tel1) ? tel1 : tel2;
  }

  public static TableValue toTableValue(GbaRestTabelWaarde restTabelWaarde) {
    if (restTabelWaarde == null) {
      return null;
    }
    return TableValue.builder()
        .code(restTabelWaarde.getWaarde())
        .description(restTabelWaarde.getOmschrijving())
        .build();
  }

  public static GbaRestTabelWaarde toGbaRestWaarde(TableValue tableValue) {
    if (tableValue == null) {
      return null;
    }
    return new GbaRestTabelWaarde(tableValue.getCode(), tableValue.getDescription());
  }

  public static <T extends GbaRestEnum<?>> T toGbaRestEnum(T[] values, PersistableEnum<String> enumValue) {
    if (enumValue == null) {
      return null;
    }
    return toEnum(values, enumValue.getCode());
  }

  public static <T extends PersistableEnum<String>> T toDomainEnum(T[] values, GbaRestEnum<String> enumValue) {
    if (enumValue == null) {
      return null;
    }
    return valueOfCode(values, enumValue.getCode());
  }

  public static boolean isBsnMatch(Bsn bsn, HeeftContactgegevens persoonBsn) {
    if (persoonBsn != null && persoonBsn.getBsn() != null) {
      return bsn.isCorrect() && new Bsn(persoonBsn.getBsn()).equals(bsn);
    }
    return false;
  }
}
