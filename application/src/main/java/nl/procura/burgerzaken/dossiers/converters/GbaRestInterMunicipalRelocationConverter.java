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

import static java.util.stream.Collectors.toList;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.toPerson;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.toPersonWithContactinfo;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter.toGbaRestZaakAlgemeen;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestRelocationConverter.*;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestToestemmingStatusConverter.toConsent;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.INTER_MUNICIPAL_RELOCATION;
import static nl.procura.burgerzaken.dossiers.model.dossier.PersonType.*;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.BUITENVERHUIZING;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.relocations.InterMunicipalRelocation;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestTabelWaarde;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestBuitenverhuizing;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestVerhuisType;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestVerhuizing;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestVerhuizingBinnenlandsAdres;

@Component
public class GbaRestInterMunicipalRelocationConverter implements GbaConverter<InterMunicipalRelocation> {

  @Override
  public DossierType dossierType() {
    return INTER_MUNICIPAL_RELOCATION;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return BUITENVERHUIZING;
  }

  @Override
  public InterMunicipalRelocation toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    InterMunicipalRelocation relocation = new InterMunicipalRelocation(dossier);
    GbaRestVerhuizing verh = zaak.getVerhuizing();
    toPersonWithContactinfo(verh.getAangever(), DECLARANT).ifPresent(relocation::setDeclarant);
    toPerson(verh.getInwoning().getToestemminggever(), CONSENTER).ifPresent(relocation::setConsenter);
    toPerson(verh.getHoofdbewoner(), MAIN_OCCUPANT).ifPresent(relocation::setMainOccupant);
    verh.getVerhuizers()
        .forEach(verhuizer -> relocation.addRelocator(toRelocator(verhuizer)));

    // Address
    GbaRestBuitenverhuizing bvh = verh.getBuitenverhuizing();
    GbaRestVerhuizingBinnenlandsAdres newAddress = bvh.getNieuwAdres();

    relocation.setStreet(newAddress.getStraat());
    relocation.setHouseNumber(newAddress.getHnr());
    relocation.setHouseNumberLetter(newAddress.getHnrL());
    relocation.setHouseNumberAddition(newAddress.getHnrT());
    relocation.setPostalCode(newAddress.getPostcode());
    relocation.setResidence(newAddress.getWoonplaats());
    relocation.setMunicipality(newAddress.getGemeente().getWaarde());
    relocation.setAddressFunction(newAddress.getFunctieAdres().getCode());
    relocation.setResidentsCount(newAddress.getAantalPersonen());
    relocation.setPreviousMunicipality(bvh.getGemeenteVanHerkomst().getWaarde());

    // Live-in
    relocation.setDestCurrResidents(verh.getBestemmmingHuidigeBewoners());
    relocation.setLiveIn(BooleanUtils.isTrue(verh.getInwoning().getSprakeVanInwoning()));
    relocation.setConsent(toConsent(verh.getInwoning().getToestemmingStatus()));

    return relocation;
  }

  public static GbaRestZaak toGbaRestZaak(InterMunicipalRelocation relocation) {
    Dossier dossier = relocation.getDossier();

    GbaRestVerhuizing verhuizing = new GbaRestVerhuizing();
    verhuizing.setType(GbaRestVerhuisType.INTERGEMEENTELIJK);
    verhuizing.setBestemmmingHuidigeBewoners(relocation.getDestCurrResidents());
    relocation.getDeclarant().ifPresent(p -> verhuizing.setAangever(toGbaAangever(p)));
    relocation.getMainOccupant().ifPresent(p -> verhuizing.setHoofdbewoner(toGbaHoofdbewoner(p)));
    verhuizing.setVerhuizers(relocation.getRelocators().stream()
        .map(GbaRestRelocationConverter::toGbaVerhuizer)
        .collect(toList()));

    // address
    GbaRestVerhuizingBinnenlandsAdres adres = new GbaRestVerhuizingBinnenlandsAdres();
    adres.setPostcode(relocation.getPostalCode());
    adres.setStraat(relocation.getStreet());
    adres.setHnr(relocation.getHouseNumber());
    adres.setHnrL(relocation.getHouseNumberLetter());
    adres.setHnrT(relocation.getHouseNumberAddition());
    adres.setWoonplaats(relocation.getResidence());
    adres.setGemeente(new GbaRestTabelWaarde(relocation.getMunicipality()));
    adres.setLocatie("");
    adres.setFunctieAdres(toGbaFunctieAdres(relocation.getAddressFunction()));
    adres.setAantalPersonen(relocation.getResidentsCount());

    GbaRestBuitenverhuizing buitenverhuizing = new GbaRestBuitenverhuizing();
    buitenverhuizing.setGemeenteVanHerkomst(new GbaRestTabelWaarde(relocation.getPreviousMunicipality()));
    buitenverhuizing.setNieuwAdres(adres);
    verhuizing.setBuitenverhuizing(buitenverhuizing);

    verhuizing.setInwoning(
        toGbaRestInwoning(relocation.getLiveIn(),
            relocation.getConsenter().orElse(null),
            relocation.getConsent()));

    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(dossier, BUITENVERHUIZING));
    zaak.setVerhuizing(verhuizing);
    return zaak;
  }
}
