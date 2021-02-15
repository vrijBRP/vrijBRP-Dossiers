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
import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.*;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter.toGbaRestZaakAlgemeen;
import static nl.procura.burgerzaken.dossiers.model.base.PersistableEnum.valueOfCode;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.COMMITMENT;
import static nl.procura.gba.web.rest.v2.model.base.GbaRestEnum.toEnum;
import static nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType.HUWELIJK_GPS_GEMEENTE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.*;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.base.persoon.GbaRestPersoon;
import nl.procura.gba.web.rest.v2.model.zaken.huwelijk.*;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestContactgegevens;

@Component
public class GbaRestCommitmentConverter implements GbaConverter<Commitment> {

  @Override
  public DossierType dossierType() {
    return COMMITMENT;
  }

  @Override
  public GbaRestZaakType zaakType() {
    return HUWELIJK_GPS_GEMEENTE;
  }

  @Override
  public Commitment toDomainModel(GbaRestZaak zaak) {
    Dossier dossier = toDossier(zaak);
    Commitment commitment = new Commitment(dossier);
    GbaRestHuwelijk huw = zaak.getHuwelijk();

    // Partners
    commitment.setPartner1(getPartner(huw.getPartner1()));
    commitment.setPartner2(getPartner(huw.getPartner2()));

    // Planning
    GbaRestHuwelijkPlanning gbaPlanning = huw.getPlanning();
    if (gbaPlanning != null) {
      CommitmentPlanning planning = new CommitmentPlanning()
          .setCommitmentDate(toLocalDate(gbaPlanning.getDatumVerbintenis()))
          .setCommitmentTime(toLocalTime(gbaPlanning.getTijdVerbintenis()))
          .setIntentionDate(toLocalDate(gbaPlanning.getDatumVoornemen()))
          .setRemarks(gbaPlanning.getToelichting());

      if (gbaPlanning.getSoort() != null) {
        planning.setCommitmentType(valueOfCode(CommitmentType.values(),
            gbaPlanning.getSoort().getCode()));
      }

      commitment.setPlanning(planning);
    }
    // Location
    if (huw.getLocatie() != null) {
      commitment.setLocation(new CommitmentLocation()
          .setName(huw.getLocatie().getNaam())
          .setAliases(huw.getLocatie().getAliassen())
          .setOptions(toLocationOptions(huw.getLocatie().getOpties())));
    }
    // Officials
    GbaRestHuwelijkAmbtenaren ambtenaren = huw.getAmbtenaren();
    if (ambtenaren != null) {
      CommitmentOfficial result = null;
      GbaRestHuwelijkAmbtenaar ambtenaar = ambtenaren.getToegekend();
      if (ambtenaar != null && !isBlank(ambtenaar.getNaam())) {
        result = new CommitmentOfficial()
            .setName(ambtenaar.getNaam())
            .setTelephoneNumber(ambtenaar.getTelefoon())
            .setEmail(ambtenaar.getEmail())
            .setAliases(ambtenaar.getAliassen());
      }
      commitment.setOfficials(new CommitmentOfficials()
          .setAssignedOfficial(result)
          .setPreferedOfficials(toPreferedOfficials(ambtenaren)));
    }
    // Witnesses
    if (huw.getGetuigen() != null) {
      commitment.setWitnesses(new CommitmentWitnesses()
          .setNumberOfMunicipalWitnesses(huw.getGetuigen().getAantalGemeenteGetuigen()));
      List<GbaRestHuwelijkGetuige> eigenGetuigen = huw.getGetuigen().getEigenGetuigen();
      if (eigenGetuigen != null) {
        commitment.getWitnesses().setChosenWitnesses(getChosenWitnesses(eigenGetuigen));
      }
    }
    return commitment;
  }

  public static GbaRestZaak toGbaRestZaak(Commitment commitment) {
    GbaRestHuwelijk huwelijk = new GbaRestHuwelijk();
    huwelijk.setPartner1(toGbaPartner(commitment.getPartner1()));
    huwelijk.setPartner2(toGbaPartner(commitment.getPartner2()));
    huwelijk.setPlanning(toGbaPlanning(commitment.getPlanning()));
    huwelijk.setLocatie(toGbaLocation(commitment.getLocation()));
    huwelijk.setAmbtenaren(toGbaAmbtenaren(commitment.getOfficials()));
    huwelijk.setGetuigen(toGbaGetuigen(commitment.getWitnesses()));

    GbaRestZaak zaak = new GbaRestZaak();
    zaak.setAlgemeen(toGbaRestZaakAlgemeen(commitment.getDossier(), HUWELIJK_GPS_GEMEENTE));
    zaak.setHuwelijk(huwelijk);
    return zaak;
  }

  private List<CommitmentWitness> getChosenWitnesses(List<GbaRestHuwelijkGetuige> eigenGetuigen) {
    if (!isEmpty(eigenGetuigen)) {
      return eigenGetuigen.stream()
          .filter(getuige -> {
            GbaRestPersoon persoon = getuige.getPersoon();
            return persoon.getBsn() != null || isNotBlank(persoon.getGeslachtsnaam());
          })
          .map(getuige -> {
            GbaRestPersoon persoon = getuige.getPersoon();
            Long bsn = persoon.getBsn();
            return new CommitmentWitness()
                .setBsn(bsn != null && bsn > 0 ? bsn.toString() : null)
                .setFirstname(persoon.getVoornamen())
                .setLastname(persoon.getGeslachtsnaam())
                .setBirthdate(persoon.getGeboortedatum())
                .setPrefix(persoon.getVoorvoegsel())
                .setRemarks(persoon.getToelichting());
          }).collect(toList());
    }
    return new ArrayList<>();
  }

  private List<CommitmentLocationOption> toLocationOptions(List<GbaRestHuwelijkOptie> opties) {
    if (!CollectionUtils.isEmpty(opties)) {
      return opties.stream()
          .map(opt -> new CommitmentLocationOption()
              .setName(opt.getNaam())
              .setAliases(opt.getAliassen())
              .setDescription(opt.getOmschrijving())
              .setValue(opt.getWaarde())
              .setType(valueOfCode(CommitmentLocationOptionType.values(),
                  opt.getType().getCode())))
          .collect(toList());
    }
    return new ArrayList<>();
  }

  private List<CommitmentOfficial> toPreferedOfficials(GbaRestHuwelijkAmbtenaren ambtenaren) {
    if (ambtenaren != null && !ambtenaren.getVoorkeuren().isEmpty()) {
      return ambtenaren.getVoorkeuren().stream()
          .filter(Objects::nonNull)
          .filter(p -> isNotBlank(p.getNaam()))
          .map(ambtenaar -> new CommitmentOfficial()
              .setName(ambtenaar.getNaam())
              .setTelephoneNumber(ambtenaar.getTelefoon())
              .setEmail(ambtenaar.getEmail())
              .setAliases(ambtenaar.getAliassen()))
          .collect(toList());
    }
    return new ArrayList<>();
  }

  private CommitmentPartner getPartner(GbaRestHuwelijkPartner gbaPartner) {
    GbaRestPersoon persoon = gbaPartner.getPersoon();
    CommitmentPartner partner = new CommitmentPartner()
        .setBsn(persoon.getBsn().toString());

    if (persoon.getContactgegevens() != null) {
      partner.setTelephoneNumber(getTelephoneNumber(persoon.getContactgegevens()))
          .setEmail(persoon.getContactgegevens().getEmail());
    }

    if (gbaPartner.getNaamgebruik() != null) {
      partner.setNameUse(getCommitmentNameUse(gbaPartner.getNaamgebruik()));
    }
    return partner;
  }

  private CommitmentNameUse getCommitmentNameUse(GbaRestHuwelijkNaamgebruik naamgebruik) {
    if (naamgebruik != null) {
      CommitmentNameUse commitmentNameUse = new CommitmentNameUse()
          .setTitle(naamgebruik.getTitelPredikaat())
          .setPrefix(naamgebruik.getVoorvoegsel())
          .setLastname(naamgebruik.getGeslachtsnaam());

      if (naamgebruik.getType() != null) {
        commitmentNameUse.setType(valueOfCode(CommitmentNameUseType.values(),
            naamgebruik.getType().getCode()));
      }
      return commitmentNameUse;
    }
    return null;
  }

  private static GbaRestHuwelijkPartner toGbaPartner(CommitmentPartner partner) {
    if (ModelValidation.isValid(partner)) {
      GbaRestContactgegevens contactgegevens = new GbaRestContactgegevens();
      contactgegevens.setEmail(partner.getEmail());
      contactgegevens.setTelefoonThuis(partner.getTelephoneNumber());

      GbaRestPersoon persoon = new GbaRestPersoon();
      persoon.setBsn(Long.valueOf(partner.getBsn()));
      persoon.setContactgegevens(contactgegevens);

      GbaRestHuwelijkPartner gbaPartner = new GbaRestHuwelijkPartner();
      gbaPartner.setPersoon(persoon);
      gbaPartner.setNaamgebruik(toGbaPartnerNaamgebruik(partner));
      return gbaPartner;
    }
    return null;
  }

  private static GbaRestHuwelijkNaamgebruik toGbaPartnerNaamgebruik(CommitmentPartner partner) {
    CommitmentNameUse nameUse = partner.getNameUse();
    if (ModelValidation.isValid(nameUse)) {
      GbaRestHuwelijkNaamgebruik gbaNaamgebruik = new GbaRestHuwelijkNaamgebruik();
      gbaNaamgebruik.setType(toEnum(GbaRestHuwelijkNaamgebruikType.values(), nameUse.getType().getCode()));
      gbaNaamgebruik.setGeslachtsnaam(nameUse.getLastname());
      gbaNaamgebruik.setTitelPredikaat(nameUse.getTitle());
      gbaNaamgebruik.setVoorvoegsel(nameUse.getPrefix());
      return gbaNaamgebruik;
    }
    return null;
  }

  private static GbaRestHuwelijkPlanning toGbaPlanning(CommitmentPlanning planning) {
    if (ModelValidation.isValid(planning)) {
      GbaRestHuwelijkPlanning gbaPlanning = new GbaRestHuwelijkPlanning();
      gbaPlanning.setSoort(toEnum(GbaRestHuwelijkVerbintenisType.values(),
          planning.getCommitmentType().getCode()));
      gbaPlanning.setDatumVerbintenis(toIntegerDate(planning.getCommitmentDate()));
      gbaPlanning.setTijdVerbintenis(toIntegerTime(planning.getCommitmentTime()));
      gbaPlanning.setDatumVoornemen(toIntegerDate(planning.getIntentionDate()));
      gbaPlanning.setToelichting(planning.getRemarks());
      return gbaPlanning;
    }
    return null;
  }

  private static GbaRestHuwelijkLocatie toGbaLocation(CommitmentLocation location) {
    if (ModelValidation.isValid(location)) {
      GbaRestHuwelijkLocatie gbaLocatie = new GbaRestHuwelijkLocatie();
      gbaLocatie.setNaam(location.getName());
      gbaLocatie.setAliassen(location.getAliases());
      if (location.getOptions() != null) {
        gbaLocatie.setOpties(location.getOptions().stream()
            .map(o -> {
              GbaRestHuwelijkOptie opt = new GbaRestHuwelijkOptie();
              opt.setNaam(o.getName());
              opt.setAliassen(o.getAliases());
              opt.setWaarde(o.getValue());
              opt.setType(toEnum(GbaRestHuwelijkOptieType.values(), o.getType().getCode()));
              opt.setOmschrijving(o.getDescription());
              return opt;
            })
            .collect(toList()));
      }
      return gbaLocatie;
    }
    return null;
  }

  private static GbaRestHuwelijkAmbtenaren toGbaAmbtenaren(CommitmentOfficials officials) {
    if (ModelValidation.isValid(officials)) {
      GbaRestHuwelijkAmbtenaren gbaAmbtenaren = new GbaRestHuwelijkAmbtenaren();
      if (officials.getPreferedOfficials().size() > 2) {
        throw new IllegalArgumentException("The maximum of preferred officials is 2");
      }
      gbaAmbtenaren.setVoorkeuren(officials.getPreferedOfficials().stream()
          .map(official -> {
            GbaRestHuwelijkAmbtenaar gbaAmbtenaar = new GbaRestHuwelijkAmbtenaar();
            gbaAmbtenaar.setNaam(official.getName());
            gbaAmbtenaar.setTelefoon(official.getTelephoneNumber());
            gbaAmbtenaar.setEmail(official.getEmail());
            gbaAmbtenaar.setAliassen(official.getAliases());
            return gbaAmbtenaar;
          })
          .collect(toList()));
      return gbaAmbtenaren;
    }
    return null;
  }

  private static GbaRestHuwelijkGetuigen toGbaGetuigen(CommitmentWitnesses witnesses) {
    if (ModelValidation.isValid(witnesses)) {
      GbaRestHuwelijkGetuigen gbaGetuigen = new GbaRestHuwelijkGetuigen();
      gbaGetuigen.setAantalGemeenteGetuigen(witnesses.getNumberOfMunicipalWitnesses());
      if (!isEmpty(witnesses.getChosenWitnesses())) {
        gbaGetuigen.setEigenGetuigen(witnesses.getChosenWitnesses().stream().map(p -> {
          GbaRestHuwelijkGetuige getuige = new GbaRestHuwelijkGetuige();
          GbaRestPersoon persoon = new GbaRestPersoon();
          long bsn = NumberUtils.toLong(p.getBsn());
          persoon.setBsn(bsn > 0 ? bsn : null);
          persoon.setVoornamen(p.getFirstname());
          persoon.setGeslachtsnaam(p.getLastname());
          persoon.setVoorvoegsel(p.getPrefix());
          persoon.setGeboortedatum(p.getBirthdate());
          persoon.setToelichting(p.getRemarks());
          getuige.setPersoon(persoon);
          return getuige;
        }).collect(Collectors.toList()));
      }
      return gbaGetuigen;
    }
    return null;
  }
}
