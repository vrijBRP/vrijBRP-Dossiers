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

package nl.procura.burgerzaken.dossiers.service.birth;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;

import lombok.Getter;

@Getter
public class BirthMatchChild {

  private final String             bsn;
  private final String             firstName;
  private final String             lastName;
  private final String             birthdate;
  private final String             title;
  private final String             prefix;
  private final String             gender;
  private final GbaWsPersonListRec rec;

  public BirthMatchChild(GbaWsPersonListRec rec) {
    bsn = rec.getElemValue(GBAElem.BSN);
    birthdate = rec.getElemValue(GBAElem.GEBOORTEDATUM);
    firstName = rec.getElemValue(GBAElem.VOORNAMEN);
    lastName = rec.getElemValue(GBAElem.GESLACHTSNAAM);
    title = rec.getElemValue(GBAElem.TITEL_PREDIKAAT);
    prefix = rec.getElemValue(GBAElem.VOORV_GESLACHTSNAAM);
    gender = rec.getElemValue(GBAElem.GESLACHTSAAND);
    this.rec = rec;
  }

  public boolean isNotStillborn() {
    return !"L".equalsIgnoreCase(rec.getElemValue(GBAElem.REG_BETREKK));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EqualsBuilder builder = new EqualsBuilder();
    BirthMatchChild that = (BirthMatchChild) o;
    if (StringUtils.isNoneBlank(bsn, that.bsn)) {
      builder.append(bsn, that.bsn);
    } else {
      builder.append(birthdate, that.birthdate);
      builder.append(firstName, that.firstName);
      builder.append(lastName, that.lastName);
      builder.append(prefix, that.prefix);
      builder.append(title, that.title);
      builder.append(gender, that.gender);
    }
    return builder.build();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();
    if (StringUtils.isNotBlank(bsn)) {
      builder.append(bsn);
    } else {
      builder.append(birthdate);
      builder.append(firstName);
      builder.append(lastName);
      builder.append(prefix);
      builder.append(title);
      builder.append(gender);
    }
    return builder.build();
  }

}
