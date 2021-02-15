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

package examples;

import static nl.procura.burgerzaken.gba.numbers.Bsn.TEST_BSN_1;

import java.util.ArrayList;
import java.util.List;

import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakToevoegenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.base.*;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.*;

public class RelocationExampleData {

  public GbaRestZaakToevoegenVraag getIntraMunicipalRequest() {

    GbaRestZaakToevoegenVraag request = new GbaRestZaakToevoegenVraag();
    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(getZaakAlgemeen());

    GbaRestVerhuizing verhuizing = new GbaRestVerhuizing();
    verhuizing.setType(GbaRestVerhuisType.BINNENGEMEENTELIJK);
    verhuizing.setBestemmmingHuidigeBewoners("Geen idee");
    verhuizing.setAangever(getAangever(getContactgegevens()));
    verhuizing.setHoofdbewoner(new GbaRestHoofdbewoner(TEST_BSN_1));
    verhuizing.setInwoning(getInwoning());

    // Vul één van de 4. Niet allemaal. Moet nog een check
    verhuizing.setBinnenverhuizing(getBinnenverhuizing());
    //    verhuizing.setBuitenverhuizing(getIntergemeentelijk());
    //    verhuizing.setEmigratie(getEmigratie());
    //    verhuizing.setHervestiging(getHervestiging());

    verhuizing.setVerhuizers(getVerhuizers());
    zaak.setVerhuizing(verhuizing);
    request.setZaak(zaak);

    return request;
  }

  private static GbaRestZaakAlgemeen getZaakAlgemeen() {

    GbaRestZaakAlgemeen algemeen = new GbaRestZaakAlgemeen();
    algemeen.setZaakId("");
    algemeen.setType(GbaRestZaakType.BINNENVERHUIZING);

    algemeen.setStatus(GbaRestZaakStatusType.WACHTKAMER);
    algemeen.setBron("PROWEB Personen");
    algemeen.setLeverancier("PROCURA");
    algemeen.setDatumIngang(20200201);
    algemeen.setDatumInvoer(20200428);
    algemeen.setTijdInvoer(101112);

    //    algemeen.setMeestRelevanteZaakId("");
    //    algemeen.setSoort(GbaRestVerhuisType.BINNENGEMEENTELIJK.getDescr());
    //    algemeen.setOmschrijving("");

    //    algemeen.setLocatieInvoer(new GbaRestTabelWaarde());
    //    algemeen.setGebruikerInvoer(new GbaRestTabelWaarde());
    //    algemeen.setIds(Lists.newArrayList());
    //    algemeen.setStatussen(Lists.newArrayList());
    //    algemeen.setExtraAttributen(Lists.newArrayList());
    //    algemeen.setGekoppeldeZaken(Lists.newArrayList());

    return algemeen;
  }

  private static GbaRestContactgegevens getContactgegevens() {
    GbaRestContactgegevens cg = new GbaRestContactgegevens();
    cg.setEmail("burgerzaken@procura.nl");
    cg.setTelefoonThuis("1234");
    cg.setTelefoonWerk("5678");
    cg.setTelefoonMobiel("9876");

    GbaRestTelefoonBuitenland bl = new GbaRestTelefoonBuitenland();
    bl.setTelefoon("3232");
    bl.setLandCode(5010);
    cg.setTelefoonBuitenland(bl);
    return cg;
  }

  private static GbaRestAangever getAangever(GbaRestContactgegevens cg) {
    GbaRestAangever aangever = new GbaRestAangever();
    aangever.setBsn(TEST_BSN_1);
    aangever.setContactgegevens(cg);
    aangever.setAmbtshalve(false);
    aangever.setHoofdInstelling(false);
    aangever.setToelichting("Toelichting bij de aangever");
    return aangever;
  }

  private static List<GbaRestVerhuizer> getVerhuizers() {
    List<GbaRestVerhuizer> verhuizers = new ArrayList<>();
    GbaRestVerhuizer verhuizer = new GbaRestVerhuizer();
    verhuizer.setBsn(TEST_BSN_1);
    verhuizer.setAangifte(GbaRestAangifteSoort.INGESCHREVENE);
    verhuizer.setVerwerken(true);
    verhuizers.add(verhuizer);
    return verhuizers;
  }

  private static GbaRestBinnenverhuizing getBinnenverhuizing() {
    GbaRestBinnenverhuizing v = new GbaRestBinnenverhuizing();
    v.setNieuwAdres(getNieuwBinnenlandsAdres());
    return v;
  }

  private static GbaRestBuitenverhuizing getIntergemeentelijk() {
    GbaRestBuitenverhuizing v = new GbaRestBuitenverhuizing();
    v.setGemeenteVanHerkomst(new GbaRestTabelWaarde("0392", "Haarlem"));
    v.setNieuwAdres(getNieuwBinnenlandsAdres());
    return v;
  }

  private static GbaRestEmigratie getEmigratie() {
    GbaRestEmigratie emigratie = new GbaRestEmigratie();
    emigratie.setAdres1("Adres 1");
    emigratie.setAdres2("Adres 2");
    emigratie.setAdres3("Adres 3");
    emigratie.setDatumVertrek(20200429);
    emigratie.setDuur(GbaRestVerhuisduurType.LANGER);
    emigratie.setLand(new GbaRestTabelWaarde("6047", "België"));
    return emigratie;
  }

  private static GbaRestHervestiging getHervestiging() {
    GbaRestHervestiging hervestiging = new GbaRestHervestiging();
    hervestiging.setDatum(20200429);
    hervestiging.setDuur(GbaRestVerhuisduurType.LANGER);
    hervestiging.setLand(new GbaRestTabelWaarde("6047", "België"));
    hervestiging.setRechtsfeiten("In gevangenis gezeten ...");
    hervestiging.setNieuwAdres(new GbaRestVerhuizingBinnenlandsAdres());
    return hervestiging;
  }

  private static GbaRestInwoning getInwoning() {
    GbaRestInwoning inwoning = new GbaRestInwoning();
    inwoning.setSprakeVanInwoning(false);
    GbaRestToestemminggever tg = new GbaRestToestemminggever();
    tg.setBsn(TEST_BSN_1);
    tg.setAnders("Anders...");
    inwoning.setToestemminggever(tg);
    inwoning.setAangifteStatus(GbaRestAangifteStatus.NIET_INGEVULD);
    inwoning.setToestemmingStatus(GbaRestToestemmingStatus.NIET_VAN_TOEPASSING);
    return inwoning;
  }

  private static GbaRestVerhuizingBinnenlandsAdres getNieuwBinnenlandsAdres() {
    GbaRestVerhuizingBinnenlandsAdres adres = new GbaRestVerhuizingBinnenlandsAdres();
    adres.setGemeente(new GbaRestTabelWaarde("0361", "Alkmaar"));
    adres.setLocatie(null);
    adres.setPostcode("1234AA");
    adres.setStraat("Dorpstraat");
    adres.setWoonplaats("Oudorp");
    adres.setAantalPersonen(1);
    adres.setFunctieAdres(GbaRestFunctieAdres.WOONADRES);
    adres.setHnr(1);
    adres.setHnrL("L");
    adres.setHnrT("T");
    return adres;
  }
}
