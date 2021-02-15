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
import static nl.procura.burgerzaken.dossiers.model.base.PersistableEnum.valueOfCode;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.DISCOVERED_BODY;
import static nl.procura.gba.web.rest.v2.model.base.GbaRestEnum.toEnum;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.LIJKVINDING;
import static nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestDocumentType.NATUURLIJK_DOOD;
import static nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestDocumentType.NIET_NATUURLIJK_DOOD;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.base.TableValue;
import nl.procura.burgerzaken.dossiers.model.deaths.DiscoveredBody;
import nl.procura.burgerzaken.dossiers.model.deaths.WrittenDeclarantType;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.GbaRestOverlijden;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.GbaRestVerzoek;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.lijkvinding.GbaRestLijkvinding;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.lijkvinding.GbaRestLijkvindingAangifte;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.lijkvinding.GbaRestSchriftelijkeAangeverType;

@Component
public class GbaRestDiscoveredBodyConverter implements GbaConverter<DiscoveredBody> {

  @Override
  public DossierType dossierType() {
    return DISCOVERED_BODY;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return LIJKVINDING;
  }

  @Override
  public DiscoveredBody toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);

    DiscoveredBody discoveredBody = new DiscoveredBody(dossier);
    GbaRestLijkvinding lijkv = zaak.getOverlijden().getLijkvinding();
    GbaRestLijkvindingAangifte aangifte = lijkv.getAangifte();
    discoveredBody.setWrittenDeclarantType(valueOfCode(WrittenDeclarantType.values(),
        aangifte.getSchriftelijkeAangever().getCode()));
    discoveredBody.setDeathByNaturalCauses(toNaturalCauses(aangifte.getDocumentType()));
    discoveredBody.setMunicipality(toTableValue(aangifte.getPlaats()));
    discoveredBody.setDateOfFinding(toLocalDate(aangifte.getDatum()));
    discoveredBody.setTimeOfFinding(toStringTimeWithoutSeconds(aangifte.getTijd()));
    discoveredBody.setExplanation(aangifte.getToevoeging());
    discoveredBody.setFuneralServices(toFuneralServices(lijkv.getLijkbezorging()));
    discoveredBody.setDeceased(toDeceased(lijkv.getOverledene()));
    discoveredBody.setCorrespondence(toCorrespondence(lijkv.getVerzoek().getCorrespondentie()));
    discoveredBody.setExtracts(toExtracts(lijkv.getVerzoek().getUittreksels()));
    return discoveredBody;
  }

  public static GbaRestZaak toGbaRestZaak(DiscoveredBody death) {
    GbaRestLijkvinding lijkv = new GbaRestLijkvinding();
    lijkv.setOverledene(toOverledene(death.getDeceased()));
    lijkv.setAangifte(toAangifte(death));
    lijkv.setLijkbezorging(toLijkbezorging(death.getFuneralServices()));
    lijkv.setVerzoek(toVerzoek(death));

    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(death.getDossier(), LIJKVINDING));

    GbaRestOverlijden overl = new GbaRestOverlijden();
    overl.setLijkvinding(lijkv);
    zaak.setOverlijden(overl);
    return zaak;
  }

  private static GbaRestVerzoek toVerzoek(DiscoveredBody death) {
    GbaRestVerzoek verzoek = new GbaRestVerzoek();
    verzoek.setCorrespondentie(toCorrespondentie(death.getCorrespondence()));
    verzoek.setUittreksels(toUittreksels(death.getExtracts()));
    return verzoek;
  }

  private static GbaRestLijkvindingAangifte toAangifte(DiscoveredBody discoveredBody) {
    TableValue municipality = discoveredBody.getMunicipality();
    boolean naturalCauses = BooleanUtils.isTrue(discoveredBody.getDeathByNaturalCauses());
    GbaRestLijkvindingAangifte aangifte = new GbaRestLijkvindingAangifte();
    aangifte.setSchriftelijkeAangever(toEnum(GbaRestSchriftelijkeAangeverType.values(),
        discoveredBody.getWrittenDeclarantType().getCode()));
    aangifte.setDocumentType(naturalCauses ? NATUURLIJK_DOOD : NIET_NATUURLIJK_DOOD);
    aangifte.setPlaats(toGbaRestWaarde(municipality));
    aangifte.setDatum(toIntegerDate(discoveredBody.getDateOfFinding()));
    aangifte.setTijd(toIntegerTime(discoveredBody.getTimeOfFinding()));
    aangifte.setToevoeging(discoveredBody.getExplanation());
    return aangifte;
  }
}
