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

package nl.procura.burgerzaken.dossiers.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;

import nl.procura.burgerzaken.dossiers.service.dossier.LocalDatePeriod;
import nl.procura.burgerzaken.dossiers.service.dossier.LocalDateTimePeriod;

public class QueryExpressions {

  public static Predicate getDatePredicate(LocalDatePeriod period, DatePath<LocalDate> path) {
    if (period != null) {
      BooleanBuilder match = new BooleanBuilder();
      if (period.getFrom() != null) {
        match.and(path.goe(period.getFrom())); // Greater or equals
      }
      if (period.getTo() != null) {
        match.and(path.loe(period.getTo())); // Less or equals
      }
      return match;
    }
    return null;
  }

  public static Predicate getDateTimePredicate(LocalDateTimePeriod period, DateTimePath<LocalDateTime> path) {
    if (period != null) {
      BooleanBuilder match = new BooleanBuilder();
      if (period.getFrom() != null) {
        match.and(path.goe(period.getFrom())); // Greater or equals
      }
      if (period.getTo() != null) {
        match.and(path.loe(period.getTo())); // Less or equals
      }
      return match;
    }
    return null;
  }
}
