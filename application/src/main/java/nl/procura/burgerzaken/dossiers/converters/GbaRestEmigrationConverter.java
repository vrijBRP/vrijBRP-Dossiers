/*
 * Copyright 2022 - 2023 Procura B.V.
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
import static nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter.toGbaRestZaakAlgemeen;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestRelocationConverter.toRelocator;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.EMIGRATION;
import static nl.procura.burgerzaken.dossiers.model.dossier.PersonRole.DECLARANT;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.EMIGRATIE;

import java.util.List;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.relocations.Emigration;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestVerhuizing;

@Component
public class GbaRestEmigrationConverter implements GbaConverter<Emigration> {

  @Override
  public DossierType dossierType() {
    return EMIGRATION;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return EMIGRATIE;
  }

  @Override
  public Emigration toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    Emigration relocation = new Emigration(dossier);
    GbaRestVerhuizing verh = zaak.getVerhuizing();
    toPersonWithContactinfo(verh.getAangever(), DECLARANT).ifPresent(relocation::setDeclarant);
    verh.getVerhuizers().forEach(verhuizer -> relocation.addRelocator(toRelocator(verhuizer)));

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

    return relocation;
  }

  public static GbaRestZaak toGbaRestZaak(Emigration emigration) {
    Dossier dossier = emigration.getDossier();

    //    GbaRestVerhuizing verhuizing = new GbaRestVerhuizing();
    //    verhuizing.setType(GbaRestVerhuisType.BINNENGEMEENTELIJK);
    //    verhuizing.setBestemmmingHuidigeBewoners(emigration.getDestCurrResidents());
    //    emigration.getDeclarant().ifPresent(p -> verhuizing.setAangever(toGbaAangever(p)));
    //    emigration.getMainOccupant().ifPresent(p -> verhuizing.setHoofdbewoner(toGbaHoofdbewoner(p)));
    //    verhuizing.setVerhuizers(emigration.getRelocators().stream()
    //        .map(GbaRestRelocationConverter::toGbaVerhuizer)
    //        .collect(toList()));
    //
    //    // address
    //    GbaRestVerhuizingBinnenlandsAdres adres = new GbaRestVerhuizingBinnenlandsAdres();
    //    adres.setPostcode(emigration.getPostalCode());
    //    adres.setStraat(emigration.getStreet());
    //    adres.setHnr(emigration.getHouseNumber());
    //    adres.setHnrL(emigration.getHouseNumberLetter());
    //    adres.setHnrT(emigration.getHouseNumberAddition());
    //    adres.setWoonplaats(emigration.getResidence());
    //    adres.setGemeente(new GbaRestTabelWaarde(emigration.getMunicipality()));
    //    adres.setLocatie("");
    //    adres.setFunctieAdres(toGbaFunctieAdres(emigration.getAddressFunction()));
    //    adres.setAantalPersonen(emigration.getResidentsCount());
    //
    //    GbaRestBinnenverhuizing binnenverhuizing = new GbaRestBinnenverhuizing();
    //    binnenverhuizing.setNieuwAdres(adres);
    //    verhuizing.setBinnenverhuizing(binnenverhuizing);
    //
    //    verhuizing.setInwoning(
    //        toGbaRestInwoning(emigration.getLiveIn(),
    //            emigration.getConsenter().orElse(null),
    //            emigration.getConsent()));

    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(dossier, EMIGRATIE));
    //zaak.setVerhuizing(verhuizing);
    return zaak;
  }

  @Override
  public boolean isRelevantForBsn(GbaRestZaak zaak, List<Bsn> bsns) {
    return bsns.stream().anyMatch(bsn -> GbaRestConverter.isBsnMatch(bsn, zaak.getVerhuizing().getAangever()));
  }
}
