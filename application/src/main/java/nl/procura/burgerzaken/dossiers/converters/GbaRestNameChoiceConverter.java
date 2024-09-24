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

import static nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter.toGbaRestZaakAlgemeen;
import static nl.procura.burgerzaken.dossiers.model.base.PersistableEnum.valueOfCode;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.NAMECHOICE;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.NAAMSKEUZE;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.base.NameSelection;
import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.namechoice.NameChoice;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.base.namenrecht.GbaRestNamenrecht;

@Component
public class GbaRestNameChoiceConverter implements GbaConverter<NameChoice> {

  @Override
  public DossierType dossierType() {
    return NAMECHOICE;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return NAAMSKEUZE;
  }

  @Override
  public NameChoice toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    NameChoice nameChoice = new NameChoice(dossier);
    GbaRestNamenrecht namenrecht = zaak.getNaamskeuze().getNamenrecht();
    String lastName = namenrecht.getGeslachtsnaam();
    String prefix = namenrecht.getVoorvoegsel();
    TitlePredicateType title = Optional.ofNullable(namenrecht.getTitelPredikaat())
        .map(s -> valueOfCode(TitlePredicateType.values(), s))
        .orElse(null);
    nameChoice.setNameSelection(new NameSelection(lastName, prefix, title));
    return nameChoice;
  }

  public static GbaRestZaak toGbaRestZaak(NameChoice nameChoice) {
    Dossier dossier = nameChoice.getDossier();

    // ADD Name use fields

    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(dossier, NAAMSKEUZE));

    // zaak.setNaamskeuze(...);

    return zaak;
  }

  @Override
  public boolean isRelevantForBsn(GbaRestZaak zaak, List<Bsn> bsns) {
    return true; // TODO Add isDeclarant code
  }
}
