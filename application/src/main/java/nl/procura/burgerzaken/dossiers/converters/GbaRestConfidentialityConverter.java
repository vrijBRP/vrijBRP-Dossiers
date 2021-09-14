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
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.CONFIDENTIALITY;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.VERSTREKKINGSBEPERKING;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.confidentiality.Confidentiality;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;

@Component
public class GbaRestConfidentialityConverter implements GbaConverter<Confidentiality> {

  @Override
  public DossierType dossierType() {
    return CONFIDENTIALITY;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return VERSTREKKINGSBEPERKING;
  }

  @Override
  public Confidentiality toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    Confidentiality confidentiality = new Confidentiality(dossier);

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

    return confidentiality;
  }

  public static GbaRestZaak toGbaRestZaak(Confidentiality confidentiality) {
    Dossier dossier = confidentiality.getDossier();

    //    GbaRestVerhuizing verhuizing = new GbaRestVerhuizing();
    //    verhuizing.setType(GbaRestVerhuisType.BINNENGEMEENTELIJK);
    //    verhuizing.setBestemmmingHuidigeBewoners(confidentiality.getDestCurrResidents());
    //    confidentiality.getDeclarant().ifPresent(p -> verhuizing.setAangever(toGbaAangever(p)));
    //    confidentiality.getMainOccupant().ifPresent(p -> verhuizing.setHoofdbewoner(toGbaHoofdbewoner(p)));
    //    verhuizing.setVerhuizers(confidentiality.getRelocators().stream()
    //        .map(GbaRestRelocationConverter::toGbaVerhuizer)
    //        .collect(toList()));
    //
    //    // address
    //    GbaRestVerhuizingBinnenlandsAdres adres = new GbaRestVerhuizingBinnenlandsAdres();
    //    adres.setPostcode(confidentiality.getPostalCode());
    //    adres.setStraat(confidentiality.getStreet());
    //    adres.setHnr(confidentiality.getHouseNumber());
    //    adres.setHnrL(confidentiality.getHouseNumberLetter());
    //    adres.setHnrT(confidentiality.getHouseNumberAddition());
    //    adres.setWoonplaats(confidentiality.getResidence());
    //    adres.setGemeente(new GbaRestTabelWaarde(confidentiality.getMunicipality()));
    //    adres.setLocatie("");
    //    adres.setFunctieAdres(toGbaFunctieAdres(confidentiality.getAddressFunction()));
    //    adres.setAantalPersonen(confidentiality.getResidentsCount());
    //
    //    GbaRestBinnenverhuizing binnenverhuizing = new GbaRestBinnenverhuizing();
    //    binnenverhuizing.setNieuwAdres(adres);
    //    verhuizing.setBinnenverhuizing(binnenverhuizing);
    //
    //    verhuizing.setInwoning(
    //        toGbaRestInwoning(confidentiality.getLiveIn(),
    //            confidentiality.getConsenter().orElse(null),
    //            confidentiality.getConsent()));

    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(dossier, VERSTREKKINGSBEPERKING));
    //zaak.setVerhuizing(verhuizing);
    return zaak;
  }
}
