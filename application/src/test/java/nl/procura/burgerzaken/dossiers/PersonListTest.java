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

import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListSet;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PersonListTest {

  @Test
  @SneakyThrows
  void canBuildPersonList() {

    GbaWsPersonList pl = PersonList.builder()
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.BSN, "123")
        .addElem(GBAElem.VOORNAMEN, "Frits")
        .toPL()
        //
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN, "Truus")
        .toSet()
        //
        .addRecord(2, GBARecStatus.HIST)
        .addElem(GBAElem.VOORNAMEN, "Truusje")
        .toCat()
        //
        .addSet(2)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN, "Linda")
        .build();

    assertEquals(2, pl.getCats().size());
    assertEquals("123", pl.getCurrentRec(GBACat.PERSOON).map(rec -> rec.getElemValue(GBAElem.BSN)).get());
    assertEquals(2, pl.getCat(GBACat.HUW_GPS).map(cat -> cat.getSets().size()).get());

    assertEquals("Truusje", pl.getCat(GBACat.HUW_GPS)
        .map(cat -> cat.getSets().get(0))
        .flatMap(set -> set.getByStatus(GBARecStatus.HIST))
        .map(rec -> rec.getElemValue(GBAElem.VOORNAMEN)).get());

    assertEquals("Truus", pl.getCat(GBACat.HUW_GPS)
        .flatMap(cat -> cat.getSets().stream()
            .filter(set -> BooleanUtils.isTrue(set.getMostRecentMarriage()))
            .findFirst()
            .map(GbaWsPersonListSet::getCurrentRec)
            .map(Optional::get)
            .map(rec -> rec.getElemValue(GBAElem.VOORNAMEN)))
        .get());

    assertEquals("Linda", pl.getCat(GBACat.HUW_GPS)
        .flatMap(cat -> cat.getSets().stream()
            .filter(set -> BooleanUtils.isFalse(set.getMostRecentMarriage()))
            .findFirst()
            .map(GbaWsPersonListSet::getCurrentRec)
            .map(Optional::get)
            .map(rec -> rec.getElemValue(GBAElem.VOORNAMEN)))
        .get());

  }
}
