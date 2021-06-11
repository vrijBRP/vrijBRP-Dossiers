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

package nl.procura.burgerzaken.dossiers.repositories;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

@Converter(autoApply = true)
public class LocalTimeConverter implements AttributeConverter<LocalTime, Integer> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

  @Override
  public Integer convertToDatabaseColumn(LocalTime attribute) {
    return Integer.valueOf(attribute.format(FORMATTER));
  }

  @Override
  public LocalTime convertToEntityAttribute(Integer dbData) {
    return LocalTime.parse(StringUtils.leftPad(dbData.toString(), 6, '0'), FORMATTER);
  }
}
