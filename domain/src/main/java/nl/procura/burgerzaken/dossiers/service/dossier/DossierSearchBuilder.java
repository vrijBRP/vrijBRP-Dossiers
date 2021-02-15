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

package nl.procura.burgerzaken.dossiers.service.dossier;

import static nl.procura.burgerzaken.dossiers.util.QueryExpressions.getDatePredicate;
import static nl.procura.burgerzaken.dossiers.util.QueryExpressions.getDateTimePredicate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.dossier.QDossier;

public class DossierSearchBuilder {

  private DossierSearchBuilder() {
  }

  public static BooleanBuilder create(DossierSearchRequest request) {
    return new BooleanBuilder()
        .and(getStartDateRange(request))
        .and(getEntryDateTimeRange(request))
        .and(getBsns(request))
        .and(getIds(request))
        .and(getStatusses(request))
        .and(getTypes(request));
  }

  private static Predicate getBsns(DossierSearchRequest request) {
    if (request.getBsns() != null) {
      BooleanBuilder match = new BooleanBuilder();
      NumberPath<Long> path = QDossier.dossier.people.any().bsn;
      request.getBsns().forEach(id -> match.or(path.eq(id)));
      return match;
    }
    return null;
  }

  private static Predicate getIds(DossierSearchRequest request) {
    if (request.getDossierIds() != null) {
      BooleanBuilder match = new BooleanBuilder();
      StringPath caseNumberPath = QDossier.dossier.caseNumber;
      StringPath referenceNumberPath = QDossier.dossier.references.any().id.referenceNumber;
      request.getDossierIds().forEach(id -> {
        match.or(caseNumberPath.eq(id));
        match.or(referenceNumberPath.eq(id));
      });
      return match;
    }
    return null;
  }

  private static Predicate getStatusses(DossierSearchRequest request) {
    if (request.getStatusses() != null) {
      BooleanBuilder match = new BooleanBuilder();
      EnumPath<DossierStatus> path = QDossier.dossier.status;
      request.getStatusses().forEach(value -> {
        match.or(path.eq(DossierStatus.valueOfCode(value)));
      });
      return match;
    }

    return null;
  }

  private static Predicate getTypes(DossierSearchRequest request) {
    if (request.getTypes() != null) {
      BooleanBuilder match = new BooleanBuilder();
      EnumPath<DossierType> path = QDossier.dossier.dossierType;
      request.getTypes().forEach(value -> {
        match.or(path.eq(DossierType.valueOfCode(value)));
      });
      return match;
    }

    return null;
  }

  private static Predicate getStartDateRange(DossierSearchRequest request) {
    LocalDatePeriod period = request.getStartDatePeriod();
    return getDatePredicate(period, QDossier.dossier.dateStart);
  }

  private static Predicate getEntryDateTimeRange(DossierSearchRequest request) {
    LocalDateTimePeriod period = request.getEntryDateTimePeriod();
    return getDateTimePredicate(period, QDossier.dossier.dateAdded);
  }
}
