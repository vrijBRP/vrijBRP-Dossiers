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

import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.*;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestDeathConverter.*;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter.toGbaRestZaakAlgemeen;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.DEATH_IN_MUNICIPALITY;
import static nl.procura.burgerzaken.dossiers.model.dossier.PersonRole.DECLARANT;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.OVERLIJDEN_IN_GEMEENTE;
import static nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestDocumentType.NATUURLIJK_DOOD;
import static nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestDocumentType.NIET_NATUURLIJK_DOOD;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.deaths.DeathInMunicipality;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.base.persoon.GbaRestPersoon;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.GbaRestOverlijden;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.GbaRestVerzoek;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestOverlijdenAangifte;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestOverlijdenInGemeente;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestContactgegevens;

@Component
public class GbaRestDeathInMunicipalityConverter implements GbaConverter<DeathInMunicipality> {

  @Override
  public DossierType dossierType() {
    return DEATH_IN_MUNICIPALITY;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return OVERLIJDEN_IN_GEMEENTE;
  }

  @Override
  public DeathInMunicipality toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    GbaRestOverlijdenInGemeente overl = zaak.getOverlijden().getOverlijdenInGemeente();
    DeathInMunicipality death = new DeathInMunicipality(dossier);
    GbaRestOverlijdenAangifte aangifte = overl.getAangifte();
    death.setDeathByNaturalCauses(toNaturalCauses(aangifte.getDocumentType()));
    death.setMunicipality(toTableValue(aangifte.getPlaats()));
    death.setDateOfDeath(toLocalDate(aangifte.getDatum()));
    death.setTimeOfDeath(toStringTimeWithoutSeconds(aangifte.getTijd()));
    death.setFuneralServices(toFuneralServices(overl.getLijkbezorging()));
    death.setDeceased(toDeceased(overl.getOverledene()));
    toPersonWithContactinfo(overl.getAangever(), DECLARANT).ifPresent(death::setDeclarant);
    death.setCorrespondence(toCorrespondence(overl.getVerzoek().getCorrespondentie()));
    death.setExtracts(toExtracts(overl.getVerzoek().getUittreksels()));
    return death;
  }

  public static GbaRestZaak toGbaRestZaak(DeathInMunicipality death) {
    GbaRestOverlijden overl = new GbaRestOverlijden();
    GbaRestOverlijdenInGemeente overlGem = new GbaRestOverlijdenInGemeente();
    overlGem.setAangever(toAangever(death.getDeclarant()));
    overlGem.setOverledene(toOverledene(death.getDeceased()));
    overlGem.setAangifte(toAangifte(death));
    overlGem.setVerzoek(toVerzoek(death));
    overlGem.setLijkbezorging(toLijkbezorging(death.getFuneralServices()));
    overl.setOverlijdenInGemeente(overlGem);
    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(death.getDossier(), OVERLIJDEN_IN_GEMEENTE));
    zaak.setOverlijden(overl);
    return zaak;
  }

  private static GbaRestVerzoek toVerzoek(DeathInMunicipality death) {
    GbaRestVerzoek verzoek = new GbaRestVerzoek();
    verzoek.setCorrespondentie(toCorrespondentie(death.getCorrespondence()));
    verzoek.setUittreksels(toUittreksels(death.getExtracts()));
    return verzoek;
  }

  private static GbaRestOverlijdenAangifte toAangifte(DeathInMunicipality death) {
    boolean naturalCauses = BooleanUtils.isTrue(death.getDeathByNaturalCauses());
    GbaRestOverlijdenAangifte aangifte = new GbaRestOverlijdenAangifte();
    aangifte.setDocumentType(naturalCauses ? NATUURLIJK_DOOD : NIET_NATUURLIJK_DOOD);
    aangifte.setPlaats(toGbaRestWaarde(death.getMunicipality()));
    aangifte.setDatum(toIntegerDate(death.getDateOfDeath()));
    aangifte.setTijd(toIntegerTime(death.getTimeOfDeath()));
    return aangifte;
  }

  private static GbaRestPersoon toAangever(Person person) {
    GbaRestContactgegevens contactgegevens = new GbaRestContactgegevens();
    contactgegevens.setEmail(person.getEmail());
    contactgegevens.setTelefoonThuis(person.getPhoneNumber());
    GbaRestPersoon persoon = new GbaRestPersoon();
    persoon.setBsn(person.getBsn().toLong());
    persoon.setContactgegevens(contactgegevens);
    return persoon;
  }
}
