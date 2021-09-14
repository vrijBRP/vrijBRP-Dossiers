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
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus.*;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.*;
import static nl.procura.burgerzaken.dossiers.model.relatives.ObstructionType.*;
import static nl.procura.burgerzaken.dossiers.model.relatives.RelationshipType.CHILD;
import static nl.procura.burgerzaken.dossiers.model.relatives.SuitableForType.*;
import static nl.procura.burgerzaken.gba.core.enums.GBACat.*;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import nl.procura.burgerzaken.dossiers.components.BrpDate;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.relatives.DeclarationType;
import nl.procura.burgerzaken.dossiers.model.relatives.ObstructionType;
import nl.procura.burgerzaken.dossiers.model.relatives.RelationshipType;
import nl.procura.burgerzaken.dossiers.model.relatives.Relative;
import nl.procura.burgerzaken.dossiers.model.relocations.Relocation;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.burgerzaken.gba.NumberUtils;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;

import lombok.Getter;

@Getter
public class ProcuraRelative {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");

  private final String    RNI_CODE = "1999";
  private final Relative  relative;
  private GbaWsPersonList pl;

  public ProcuraRelative(Bsn bsn, RelationshipType relationshipType) {
    relative = new Relative(bsn, relationshipType);
  }

  public void addPersonList(GbaWsPersonList pl) {
    this.pl = pl;
    relative.setAge(getAge(pl));
    relative.setDeclarationType(getDeclarationType(relative.getRelationshipType(), relative.getAge()));
  }

  public void checkObstructions() {
    if (isDeceased()) {
      relative.addObstruction(PERSON_IS_DECEASED);
    }
    if (isSuspended()) {
      relative.addObstruction(PERSON_RECORD_IS_SUSPENDED);
    }
    if (isBlocked()) {
      relative.addObstruction(PERSON_RECORD_IS_BLOCKED);
    }
    if (isRNI()) {
      relative.addObstruction(PERSON_IS_EMIGRATED);
    }
    if (hasCurator()) {
      relative.addObstruction(PERSON_HAS_CURATOR);
    }
    if (hasConfidentiality()) {
      relative.addObstruction(PERSON_HAS_CONFIDENTIALITY);
    }
  }

  public Relative getRelative() {
    checkSuitabilities();
    return relative;
  }

  public void checkSuitabilities() {
    // Always applicable
    if (relative.hasObstructions(
        NO_PERSON_RECORD_FOUND,
        MULTIPLE_PERSON_RECORDS_FOUND)) {
      return;
    }

    relative.addSuitableFor(GENERAL_USE_CASE);

    // None of the cases are allowed
    if (relative.hasObstructions(
        PERSON_IS_DECEASED,
        PERSON_RECORD_IS_SUSPENDED,
        PERSON_RECORD_IS_BLOCKED,
        PERSON_IS_EMIGRATED,
        PERSON_HAS_CURATOR,
        RELATIONSHIP_HAS_ENDED,
        DIFFERENT_ADDRESS)) {
      return;
    }

    // Relocation
    if (!relative.hasObstructions(EXISTING_RELOCATION_CASE)) {
      relative.addSuitableFor(NEW_RELOCATION_CASE);
    }

    boolean childUnder16 = CHILD.matches(relative.getRelationshipType()) && relative.getAge() < 16;

    // BRP Extract
    if (childUnder16) {
      relative.addSuitableFor(NEW_BRP_EXTRACT_CASE);

      // Confidentiality
      if (!relative.hasObstructions(EXISTING_CONFIDENTIALITY_CASE)) {
        relative.addSuitableFor(NEW_CONFIDENTIALITY_CASE);
      }
    }
  }

  public DeclarationType getDeclarationType(RelationshipType relationshipType, Integer age) {
    switch (relationshipType) {
      case REGISTERED:
        return DeclarationType.REGISTERED;
      case PARTNER:
        return DeclarationType.PARTNER;
      case PARENT:
        return DeclarationType.ADULT_CHILD_LIVING_WITH_PARENTS;
      case CHILD:
        if (age == null || age < 18) {
          return DeclarationType.AUTHORITY_HOLDER;
        } else {
          return DeclarationType.PARENT_LIVING_WITH_ADULT_CHILD;
        }
      case EX_PARTNER:
        return null;
    }
    throw new IllegalArgumentException("Unknown relationship type: " + relationshipType);
  }

  public void addObstruction(ObstructionType obstructionType) {
    relative.addObstruction(obstructionType);
  }

  public void checkExistingDossiers(
      DossierService dossierService,
      List<DossierType> dossierTypes,
      DossierCheck dossierCheck) {

    Bsn bsn = pl.getCurrentRec(PERSOON)
        .map(rec -> rec.getElemValue(BSN))
        .map(Bsn::new)
        .orElseThrow(() -> new IllegalStateException("Person has no BSN"));

    Page<Dossier> dossiers = dossierService.find(DossierSearchRequest.builder()
        .bsns(singletonList(bsn))
        .types(dossierTypes.stream()
            .map(DossierType::getCode)
            .collect(Collectors.toList()))
        .statusses(Arrays.asList(
            CREATED.getCode(),
            PROCESSING.getCode(),
            INCOMPLETE.getCode(),
            ON_HOLD.getCode()))
        .build());
    dossierCheck.check(bsn, dossiers);
  }

  public void checkExistingDossiers(DossierService dossierService) {
    // Relocations
    checkExistingDossiers(dossierService,
        List.of(INTRA_MUNICIPAL_RELOCATION, INTER_MUNICIPAL_RELOCATION,
            CONFIDENTIALITY),
        (bsn, dossiers) -> {
          filterRelocationCases(dossierService, bsn, dossiers);
          filterConfidentialityCases(dossiers);
        });
  }

  private boolean hasRelocatorBsn(DossierService dossierService, Bsn bsn, Dossier dossier) {
    RelocationService<Relocation> dossierTypeService = dossierService
        .getDossierTypeService(dossier.getDossierType(), RelocationService.class);
    return dossierTypeService
        .findByCaseNumber(dossier.getCaseNumber())
        .getRelocators()
        .stream()
        .anyMatch(relocator -> bsn.equals(relocator.getPerson().getBsn()));
  }

  private void filterRelocationCases(DossierService dossierService, Bsn bsn, Page<Dossier> dossiers) {
    dossiers.stream()
        .filter(dossier -> dossier.getDossierType().matches(
            INTRA_MUNICIPAL_RELOCATION, INTER_MUNICIPAL_RELOCATION))
        .filter(dossier -> hasRelocatorBsn(dossierService, bsn, dossier)).findAny()
        .ifPresent(d -> relative.addObstruction(EXISTING_RELOCATION_CASE));
  }

  private void filterConfidentialityCases(Page<Dossier> dossiers) {
    dossiers.stream()
        .filter(dossier -> dossier.getDossierType() == CONFIDENTIALITY).findAny()
        .ifPresent(d -> relative.addObstruction(EXISTING_CONFIDENTIALITY_CASE));
  }

  public interface DossierCheck {

    void check(Bsn bsn, Page<Dossier> dossiers);
  }

  public void checkAddress(ProcuraRelative relativePl) {
    if (!isAddress(relativePl)) {
      relative.addObstruction(DIFFERENT_ADDRESS);
    }
  }

  private boolean isDeceased() {
    return isElems(OVERL, DATUM_OVERL, PLAATS_OVERL, LAND_OVERL);
  }

  private boolean isBlocked() {
    return isElems(INSCHR, GEM_BLOKK_PL, DATUM_INGANG_BLOK_PL);
  }

  private boolean isRNI() {
    return pl.getCurrentRec(VB)
        .map(rec -> rec.getElemValue(GEM_INSCHR_CODE))
        .filter(RNI_CODE::equals).isPresent();
  }

  private boolean isSuspended() {
    return isElems(INSCHR, OMSCHR_REDEN_OPSCH_BIJHOUD);
  }

  private boolean hasCurator() {
    return isElems(GEZAG, IND_CURATELE_REG);
  }

  private boolean hasConfidentiality() {
    return getElemValue(INSCHR, IND_GEHEIM).filter(NumberUtils::isPos).isPresent();
  }

  private boolean isAddress(ProcuraRelative relativePl) {
    return relativePl == null || Objects.equals(relativePl.getAddress(), getAddress());
  }

  private String getAddress() {
    return pl.getCurrentRec(VB)
        .map(rec -> rec.getElemValue(GEM_INSCHR_CODE)
            + rec.getElemValue(POSTCODE)
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

  private Optional<String> getElemValue(GBACat cat, GBAElem gbaElem) {
    return pl.getCurrentRec(cat)
        .map(rec -> rec.getElemValue(gbaElem));
  }
}
