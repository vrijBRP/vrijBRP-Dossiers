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
import static nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestDocumentType.NATUURLIJK_DOOD;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;

import nl.procura.burgerzaken.dossiers.model.deaths.*;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestTabelWaarde;
import nl.procura.gba.web.rest.v2.model.zaken.base.persoon.GbaRestPersoon;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.*;
import nl.procura.gba.web.rest.v2.model.zaken.overlijden.gemeente.GbaRestDocumentType;

public final class GbaRestDeathConverter<T> {

  protected static Deceased toDeceased(GbaRestPersoon persoon) {
    if (persoon != null) {
      GbaRestTabelWaarde geboorteland = persoon.getGeboorteland();
      return new Deceased()
          .setBsn(persoon.getBsn().toString())
          .setFirstname(persoon.getVoornamen())
          .setLastname(persoon.getGeslachtsnaam())
          .setPrefix(persoon.getVoorvoegsel())
          .setTitle(persoon.getTitelPredikaat())
          .setBirthdate(persoon.getGeboortedatum())
          .setBirthplace(persoon.getGeboorteplaats().getOmschrijving())
          .setBirthcountry(toTableValue(geboorteland));
    }
    return null;
  }

  protected static GbaRestPersoon toOverledene(Deceased person) {
    GbaRestPersoon persoon = new GbaRestPersoon();
    long bsn = NumberUtils.toLong(person.getBsn());
    persoon.setBsn(bsn > 0 ? bsn : null);
    persoon.setVoornamen(person.getFirstname());
    persoon.setGeslachtsnaam(person.getLastname());
    persoon.setVoorvoegsel(person.getPrefix());
    persoon.setTitelPredikaat(person.getTitle());
    persoon.setGeboortedatum(person.getBirthdate());
    persoon.setGeboorteplaats(new GbaRestTabelWaarde(person.getBirthplace()));
    persoon.setGeboorteland(toGbaRestWaarde(person.getBirthcountry()));
    return persoon;
  }

  protected static GbaRestOverlijdenCorrespondentie toCorrespondentie(Correspondence corr) {
    GbaRestOverlijdenCorrespondentie gbaRest = new GbaRestOverlijdenCorrespondentie();
    gbaRest.setType(toGbaRestEnum(GbaRestCommunicatieType.values(), corr.getCommunicationType()));
    gbaRest.setOrganisatie(corr.getOrganization());
    gbaRest.setAfdeling(corr.getDepartement());
    gbaRest.setNaam(corr.getName());
    gbaRest.setEmail(corr.getEmail());
    gbaRest.setTelefoon(corr.getTelephoneNumber());
    gbaRest.setStraat(corr.getStreet());
    gbaRest.setHnr(corr.getHouseNumber());
    gbaRest.setHnrL(corr.getHouseNumberLetter());
    gbaRest.setHnrT(corr.getHouseNumberAddition());
    gbaRest.setPostcode(corr.getPostalCode());
    gbaRest.setPlaats(corr.getResidence());
    return gbaRest;
  }

  protected static Correspondence toCorrespondence(GbaRestOverlijdenCorrespondentie gbaRest) {
    Correspondence corr = new Correspondence();
    if (gbaRest.getType() != null) {
      corr.setCommunicationType(toDomainEnum(CommunicationType.values(), gbaRest.getType()));
      corr.setOrganization(gbaRest.getOrganisatie());
      corr.setDepartement(gbaRest.getAfdeling());
      corr.setName(gbaRest.getNaam());
      corr.setEmail(gbaRest.getEmail());
      corr.setTelephoneNumber(gbaRest.getTelefoon());
      corr.setStreet(gbaRest.getStraat());
      corr.setHouseNumber(gbaRest.getHnr());
      corr.setHouseNumberLetter(gbaRest.getHnrL());
      corr.setHouseNumberAddition(gbaRest.getHnrT());
      corr.setPostalCode(gbaRest.getPostcode());
      corr.setResidence(gbaRest.getPlaats());
    }
    return corr;
  }

  protected static GbaRestLijkbezorging toLijkbezorging(FuneralServices fs) {
    GbaRestLijkbezorging gbaRest = new GbaRestLijkbezorging();
    gbaRest.setLijkbezorgingType(toGbaRestEnum(GbaRestLijkbezorgingType.values(), fs.getServiceType()));
    gbaRest.setDatum(toIntegerDate(fs.getDate()));
    gbaRest.setTijd(toIntegerTime(fs.getTime()));
    gbaRest.setBuitenBenelux(fs.getOutsideBenelux());
    gbaRest.setDoodsoorzaakType(toGbaRestEnum(GbaRestDoodsoorzaakType.values(), fs.getCauseOfDeathType()));
    gbaRest.setLandVanBestemming(toGbaRestWaarde(fs.getCountryOfDestination()));
    gbaRest.setPlaatsVanBestemming(fs.getPlaceOfDestination());
    gbaRest.setVia(fs.getVia());
    gbaRest.setVervoermiddel(fs.getTransportation());
    return gbaRest;
  }

  protected static FuneralServices toFuneralServices(GbaRestLijkbezorging gbaRest) {
    FuneralServices fs = new FuneralServices();
    fs.setServiceType(toDomainEnum(FuneralServiceType.values(), gbaRest.getLijkbezorgingType()));
    fs.setDate(toLocalDate(gbaRest.getDatum()));
    fs.setTime(toStringTimeWithoutSeconds(gbaRest.getTijd()));
    fs.setOutsideBenelux(gbaRest.getBuitenBenelux());
    fs.setCauseOfDeathType(toDomainEnum(CauseOfDeathType.values(), gbaRest.getDoodsoorzaakType()));
    fs.setCountryOfDestination(toTableValue(gbaRest.getLandVanBestemming()));
    fs.setPlaceOfDestination(gbaRest.getPlaatsVanBestemming());
    fs.setVia(gbaRest.getVia());
    fs.setTransportation(gbaRest.getVervoermiddel());
    return fs;
  }

  protected static Boolean toNaturalCauses(GbaRestDocumentType documentType) {
    return documentType != null ? (NATUURLIJK_DOOD.equals(documentType)) : null;
  }

  protected static List<Extract> toExtracts(List<GbaRestOverlijdenUittreksel> uittreksels) {
    return Optional.ofNullable(uittreksels)
        .map(list -> list.stream()
            .map(GbaRestDeathConverter::toExtract)
            .collect(Collectors.toList()))
        .orElse(null);
  }

  protected static List<GbaRestOverlijdenUittreksel> toUittreksels(List<Extract> extracts) {
    return Optional.ofNullable(extracts)
        .map(list -> list.stream()
            .map(GbaRestDeathConverter::toUittreksel)
            .collect(Collectors.toList()))
        .orElse(null);
  }

  private static Extract toExtract(GbaRestOverlijdenUittreksel u) {
    return new Extract()
        .setCode(u.getCode())
        .setDescription(u.getOmschrijving())
        .setAmount(u.getAantal());
  }

  private static GbaRestOverlijdenUittreksel toUittreksel(Extract u) {
    GbaRestOverlijdenUittreksel uittreksel = new GbaRestOverlijdenUittreksel();
    uittreksel.setCode(u.getCode());
    uittreksel.setOmschrijving(u.getDescription());
    uittreksel.setAantal(u.getAmount());
    return uittreksel;
  }
}
