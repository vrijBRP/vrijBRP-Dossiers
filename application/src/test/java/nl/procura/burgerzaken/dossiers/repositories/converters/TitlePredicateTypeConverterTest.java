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

package nl.procura.burgerzaken.dossiers.repositories.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;
import nl.procura.burgerzaken.dossiers.repositories.TitlePredicateTypeConverter;

class TitlePredicateTypeConverterTest {

  @Test
  public void mustReturn() {
    assertEquals("B", new TitlePredicateTypeConverter().convertToDatabaseColumn(TitlePredicateType.B));
    assertEquals("", new TitlePredicateTypeConverter().convertToDatabaseColumn(null));
    assertNull(new TitlePredicateTypeConverter().convertToEntityAttribute(""));
    assertEquals(TitlePredicateType.B, new TitlePredicateTypeConverter().convertToEntityAttribute("B"));
    String exMsg = "";
    try {
      new TitlePredicateTypeConverter().convertToEntityAttribute("ABC");
    } catch (IllegalArgumentException e) {
      exMsg = e.getMessage();
    }
    assertEquals("No value with code 'ABC' for type 'TitlePredicateType'", exMsg);
  }
}
