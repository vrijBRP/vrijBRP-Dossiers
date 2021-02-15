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

package nl.procura.burgerzaken.dossiers.service;

import static java.util.Collections.singletonList;
import static nl.procura.burgerzaken.dossiers.components.BrpDate.ADJUSTER.TO_START_PERIOD;
import static nl.procura.burgerzaken.dossiers.model.relocations.info.RelocationObstructionType.*;
import static nl.procura.burgerzaken.gba.core.enums.GBACat.*;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.data.domain.Page;

import nl.procura.burgerzaken.dossiers.components.BrpDate;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.relocations.Relocation;
import nl.procura.burgerzaken.dossiers.model.relocations.info.RelationshipType;
import nl.procura.burgerzaken.dossiers.model.relocations.info.RelocationObstructionType;
import nl.procura.burgerzaken.dossiers.model.relocations.info.RelocationRelative;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;

import lombok.Getter;

@Getter
public class ProcuraRelocationRelative {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");

  private final String             RNI_CODE = "1999";
  private final RelocationRelative relative;
  private GbaWsPersonList          pl;

  public ProcuraRelocationRelative(String bsn, RelationshipType relationshipType) {
    relative = new RelocationRelative(bsn, relationshipType);
  }

  public void addPersonList(GbaWsPersonList pl) {
    this.pl = pl;
    relative.setAge(getAge(pl));
    checkDeceased();
    checkSuspended();
    checkBlocked();
    checkRNI();
    checkCurator();
  }

  public void addObstruction(RelocationObstructionType obstructionType) {
    relative.addObstruction(obstructionType);
  }

  public void checkExistingRelocation(
      DossierService dossierService) {

    Long bsn = pl.getCurrentRec(PERSOON)
        .map(rec -> rec.getElemValue(BSN))
        .map(bsnValue -> new Bsn(bsnValue).toLong())
        .orElseThrow(() -> new IllegalStateException("Person has no BSN"));

    Page<Dossier> dossiers = dossierService.find(DossierSearchRequest.builder()
        .bsns(singletonList(bsn))
        .types(Arrays.asList(
            DossierType.INTRA_MUNICIPAL_RELOCATION.getCode(),
            DossierType.INTER_MUNICIPAL_RELOCATION.getCode()))
        .statusses(Arrays.asList(
            DossierStatus.CREATED.getCode(),
            DossierStatus.PROCESSING.getCode(),
            DossierStatus.INCOMPLETE.getCode(),
            DossierStatus.ON_HOLD.getCode()))
        .build());
    checkRelocationDossiers(dossierService, bsn, dossiers);
  }

  private void checkRelocationDossiers(
      DossierService dossierService,
      Long bsn, Page<Dossier> dossiers) {

    dossiers.stream()
        .filter(dossier -> {
          RelocationService<Relocation> dossierTypeService = dossierService
              .getDossierTypeService(dossier.getDossierType(), RelocationService.class);
          return dossierTypeService
              .findByCaseNumber(dossier.getCaseNumber())
              .getRelocators()
              .stream()
              .anyMatch(relocator -> bsn.equals(relocator.getPerson().getBsn()));
        }).findAny()
        .ifPresent(d -> relative.addObstruction(EXISTING_RELOCATION));
  }

  public void checkAddress(ProcuraRelocationRelative relativePl) {
    if (!isAddress(relativePl)) {
      relative.addObstruction(DIFFERENT_ADDRESS);
    }
  }

  private void checkDeceased() {
    if (isElems(OVERL, DATUM_OVERL, PLAATS_OVERL, LAND_OVERL)) {
      relative.addObstruction(PERSON_IS_DECEASED);
    }
  }

  private void checkBlocked() {
    if (isElems(INSCHR, GEM_BLOKK_PL, DATUM_INGANG_BLOK_PL)) {
      relative.addObstruction(PERSON_RECORD_IS_BLOCKED);
    }
  }

  private void checkRNI() {
    if (pl.getCurrentRec(VB)
        .map(rec -> rec.getElemValue(GEM_INSCHR_CODE))
        .filter(RNI_CODE::equals).isPresent()) {
      relative.addObstruction(PERSON_IS_EMIGRATED);
    }
  }

  private void checkSuspended() {
    if (isElems(INSCHR, OMSCHR_REDEN_OPSCH_BIJHOUD)) {
      relative.addObstruction(PERSON_RECORD_IS_SUSPENDED);
    }
  }

  private void checkCurator() {
    if (isElems(GEZAG, IND_CURATELE_REG)) {
      relative.addObstruction(PERSON_HAS_CURATOR);
    }
  }

  private boolean isAddress(ProcuraRelocationRelative relativePl) {
    return relativePl == null || Objects.equals(relativePl.getAddress(), getAddress());
  }

  private String getAddress() {
    return pl.getCurrentRec(VB)
        .map(rec -> rec.getElemValue(POSTCODE)
            + rec.getElemValue(HNR)
            + rec.getElemValue(HNR_L)
            + rec.getElemValue(HNR_T))
        .orElse(null);
  }

  private Integer getAge(GbaWsPersonList pl) {
    return pl.getCurrentRec(GBACat.PERSOON)
        .map(person -> person.getElemValue(GEBOORTEDATUM))
        .flatMap(birthday -> BrpDate.from(birthday).getLocalDate(TO_START_PERIOD)
            .map(date -> Period.between(date, LocalDate.now()).getYears()))
        .orElse(null);
  }

  private boolean isElems(GBACat cat, GBAElem... gbaElem) {
    return pl.getCurrentRec(cat)
        .map(rec -> rec.getElems(null, gbaElem))
        .filter(elems -> !elems.isEmpty())
        .isPresent();
  }
}
