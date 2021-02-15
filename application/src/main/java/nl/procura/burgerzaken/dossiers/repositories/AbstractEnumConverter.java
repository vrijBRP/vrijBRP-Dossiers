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

import java.util.Objects;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import nl.procura.burgerzaken.dossiers.model.base.PersistableEnum;

@Converter
public abstract class AbstractEnumConverter<T extends Enum<T> & PersistableEnum<E>, E>
    implements AttributeConverter<T, E> {

  private final Class<T> clazz;
  private E              defaultValue;

  public AbstractEnumConverter(Class<T> clazz, E defaultValue) {
    this.clazz = clazz;
    this.defaultValue = defaultValue;
  }

  @Override
  public E convertToDatabaseColumn(T attribute) {
    return attribute != null ? attribute.getCode() : defaultValue;
  }

  @Override
  public T convertToEntityAttribute(E dbData) {
    if (dbData != null && defaultValue != null
        && Objects.equals(dbData.toString().trim(),
            defaultValue.toString().trim())) {
      return null;
    } else {
      T[] enums = clazz.getEnumConstants();
      for (T e : enums) {
        if (e.getCode().equals(dbData)) {
          return e;
        }
      }

      throw new IllegalArgumentException(String.format("No value with code '%s' for type '%s'",
          dbData, clazz.getSimpleName()));
    }
  }
}
