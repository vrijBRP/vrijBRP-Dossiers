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
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.EXTRACT;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.UITTREKSEL;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.extract.Extract;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;

@Component
public class GbaRestExtractConverter implements GbaConverter<Extract> {

  @Override
  public DossierType dossierType() {
    return EXTRACT;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return UITTREKSEL;
  }

  @Override
  public Extract toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    Extract extract = new Extract(dossier);

    //    GbaRestVerhuizing verh = zaak.getVerhuizing();
    //    toPersonWithContactinfo(verh.getAangever(), DECLARANT).ifPresent(confidentiality::setDeclarant);
    //    toPerson(verh.getInwoning().getToestemminggever(), CONSENTER).ifPresent(confidentiality::setConsenter);
    //    toPerson(verh.getHoofdbewoner(), MAIN_OCCUPANT).ifPresent(confidentiality::setMainOccupant);
    //    verh.getVerhuizers()
    //        .forEach(verhuizer -> confidentiality.addRelocator(toRelocator(verhuizer)));

    // Address
    //    GbaRestBinnenverhuizing bvh = verh.getBinnenverhuizing();
    //    GbaRestVerhuizingBinnenlandsAdres newAddress = bvh.getNieuwAdres();
    //
    //    confidentiality.setStreet(newAddress.getStraat());
    //    confidentiality.setHouseNumber(newAddress.getHnr());
    //    confidentiality.setHouseNumberLetter(newAddress.getHnrL());
    //    confidentiality.setHouseNumberAddition(newAddress.getHnrT());
    //    confidentiality.setPostalCode(newAddress.getPostcode());
    //    confidentiality.setResidence(newAddress.getWoonplaats());
    //    confidentiality.setMunicipality(newAddress.getGemeente().getWaarde());
    //    confidentiality.setAddressFunction(newAddress.getFunctieAdres().getCode());
    //    confidentiality.setResidentsCount(newAddress.getAantalPersonen());

    // Live-in
    //    confidentiality.setDestCurrResidents(verh.getBestemmmingHuidigeBewoners());
    //    confidentiality.setLiveIn(BooleanUtils.isTrue(verh.getInwoning().getSprakeVanInwoning()));
    //    confidentiality.setConsent(toConsent(verh.getInwoning().getToestemmingStatus()));    

    return extract;
  }

  public static GbaRestZaak toGbaRestZaak(Extract extract) {
    Dossier dossier = extract.getDossier();

    //    GbaRestVerhuizing verhuizing = new GbaRestVerhuizing();
    //    verhuizing.setType(GbaRestVerhuisType.BINNENGEMEENTELIJK);
    //    verhuizing.setBestemmmingHuidigeBewoners(extract.getDestCurrResidents());
    //    extract.getDeclarant().ifPresent(p -> verhuizing.setAangever(toGbaAangever(p)));
    //    extract.getMainOccupant().ifPresent(p -> verhuizing.setHoofdbewoner(toGbaHoofdbewoner(p)));
    //    verhuizing.setVerhuizers(extract.getRelocators().stream()
    //        .map(GbaRestRelocationConverter::toGbaVerhuizer)
    //        .collect(toList()));
    //
    //    // address
    //    GbaRestVerhuizingBinnenlandsAdres adres = new GbaRestVerhuizingBinnenlandsAdres();
    //    adres.setPostcode(extract.getPostalCode());
    //    adres.setStraat(extract.getStreet());
    //    adres.setHnr(extract.getHouseNumber());
    //    adres.setHnrL(extract.getHouseNumberLetter());
    //    adres.setHnrT(extract.getHouseNumberAddition());
    //    adres.setWoonplaats(extract.getResidence());
    //    adres.setGemeente(new GbaRestTabelWaarde(extract.getMunicipality()));
    //    adres.setLocatie("");
    //    adres.setFunctieAdres(toGbaFunctieAdres(extract.getAddressFunction()));
    //    adres.setAantalPersonen(extract.getResidentsCount());
    //
    //    GbaRestBinnenverhuizing binnenverhuizing = new GbaRestBinnenverhuizing();
    //    binnenverhuizing.setNieuwAdres(adres);
    //    verhuizing.setBinnenverhuizing(binnenverhuizing);
    //
    //    verhuizing.setInwoning(
    //        toGbaRestInwoning(extract.getLiveIn(),
    //            extract.getConsenter().orElse(null),
    //            extract.getConsent()));

    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(dossier, UITTREKSEL));
    //zaak.setVerhuizing(verhuizing);
    return zaak;
  }
}
