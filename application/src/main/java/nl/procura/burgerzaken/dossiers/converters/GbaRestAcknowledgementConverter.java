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

import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.toPersonWithContactinfo;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestNamenrechtConverter.toNameSelection;
import static nl.procura.burgerzaken.dossiers.model.dossier.PersonRole.ACKNOWLEDGER;
import static nl.procura.burgerzaken.dossiers.model.dossier.PersonRole.MOTHER;

import java.util.Map;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.birth.Acknowledgement;
import nl.procura.burgerzaken.dossiers.model.birth.BirthAcknowledgementType;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.erkenning.GbaRestErkenning;
import nl.procura.gba.web.rest.v2.model.zaken.erkenning.GbaRestErkenningsType;

@Component
public class GbaRestAcknowledgementConverter implements GbaConverter<Acknowledgement> {

  private static final Map<GbaRestErkenningsType, BirthAcknowledgementType> TYPE_MAP = Map.of(
      GbaRestErkenningsType.GEEN_ERKENNING, BirthAcknowledgementType.NO,
      GbaRestErkenningsType.ERKENNING_ONGEBOREN_VRUCHT, BirthAcknowledgementType.UNBORN,
      GbaRestErkenningsType.ERKENNING_BESTAAND_KIND, BirthAcknowledgementType.EXISTING_CHILD,
      GbaRestErkenningsType.ERKENNING_BIJ_AANGIFTE, BirthAcknowledgementType.AT_DECLARATION,
      GbaRestErkenningsType.ONBEKEND, BirthAcknowledgementType.UNKNOWN);

  @Override
  public DossierType dossierType() {
    return DossierType.ACKNOWLEDGEMENT;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return GbaRestZaakType.ERKENNING;
  }

  @Override
  public Acknowledgement toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);

    GbaRestErkenning erkenning = zaak.getErkenning();
    GbaRestErkenningsType erkenningsType = erkenning.getErkenningsType();
    BirthAcknowledgementType type = TYPE_MAP.get(erkenningsType);
    if (type == null) {
      throw new IllegalArgumentException("Illegal acknowledgement type " + erkenningsType);
    }
    Person mother = toPersonWithContactinfo(erkenning.getMoeder(), MOTHER)
        .orElseThrow(() -> new IllegalArgumentException("Mother is required"));
    // acknowledger might be null in case dossier not complete yet
    Person acknowledger = toPersonWithContactinfo(erkenning.getErkenner(), ACKNOWLEDGER).orElse(null);
    return Acknowledgement.builder()
        .dossier(dossier)
        .type(type)
        .mother(mother)
        .acknowledger(acknowledger)
        .nameSelection(toNameSelection(erkenning.getNamenrecht()))
        .build();
  }
}
