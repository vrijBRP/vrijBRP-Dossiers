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

import static nl.procura.burgerzaken.dossiers.model.relatives.ObstructionType.*;
import static nl.procura.burgerzaken.dossiers.model.relatives.RelationshipType.*;
import static nl.procura.burgerzaken.gba.core.enums.GBACat.*;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.model.relatives.RelationshipType;
import nl.procura.burgerzaken.dossiers.model.relatives.Relative;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListSet;

@Service
public class RelativesFactory {

  private final ProcuraWsService personWsService;
  private final DossierService   dossierService;

  public RelativesFactory(ProcuraWsService personWsService, DossierService dossierService) {
    this.personWsService = personWsService;
    this.dossierService = dossierService;
  }

  public List<Relative> getRelatives(Bsn bsn) {
    List<ProcuraRelative> relatives = new ArrayList<>();
    ProcuraRelative registered = getRegistered(bsn);
    relatives.add(registered);
    getPartner(registered).ifPresent(relatives::add);
    relatives.addAll(getChildren(registered));
    relatives.addAll(getParents(registered));

    return relatives.stream()
        .map(ProcuraRelative::getRelative)
        .collect(Collectors.toList());
  }

  private ProcuraRelative getRegistered(Bsn bsn) {
    List<GbaWsPersonList> personLists = personWsService.get(bsn.toLong());
    if (personLists.isEmpty()) {
      throw new ApiException(ApiErrorType.BAD_REQUEST, "No persons found");
    }
    if (personLists.size() > 1) {
      throw new ApiException(ApiErrorType.BAD_REQUEST, "Multiple people found");
    }
    ProcuraRelative registered = new ProcuraRelative(bsn, REGISTERED);
    registered.addPersonList(personLists.get(0));
    registered.checkExistingDossiers(dossierService);
    registered.checkObstructions();
    return registered;
  }

  private ProcuraRelative getRelative(ProcuraRelative registered, Bsn bsn, RelationshipType relationshipType) {
    ProcuraRelative relative = new ProcuraRelative(bsn, relationshipType);
    List<GbaWsPersonList> personsLists = personWsService.get(bsn.toLong());

    if (personsLists.size() == 1) {
      relative.addPersonList(personsLists.get(0));
      relative.checkExistingDossiers(dossierService);
      relative.checkAddress(registered);
      relative.checkObstructions();

    } else if (personsLists.isEmpty()) {
      relative.addObstruction(NO_PERSON_RECORD_FOUND);

    } else {
      relative.addObstruction(MULTIPLE_PERSON_RECORDS_FOUND);
    }
    return relative;
  }

  private List<ProcuraRelative> getChildren(ProcuraRelative registered) {
    return registered.getPl().getCurrentRecords(KINDEREN).stream()
        .map(this::getBsn)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(bsn -> getRelative(registered, bsn, CHILD))
        .collect(Collectors.toList());
  }

  private List<ProcuraRelative> getParents(ProcuraRelative registered) {
    return Stream.of(registered.getPl().getCurrentRec(OUDER_1), registered.getPl().getCurrentRec(OUDER_2))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(this::getBsn)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(bsn -> getRelative(registered, bsn, PARENT))
        .collect(Collectors.toList());
  }

  private Optional<ProcuraRelative> getPartner(ProcuraRelative registered) {
    Optional<GbaWsPersonListRec> partnerRec = registered.getPl().getCat(HUW_GPS)
        .flatMap(cat -> cat.getSets().stream()
            .filter(set -> BooleanUtils.isTrue(set.getMostRecentMarriage()))
            .findFirst()
            .flatMap(GbaWsPersonListSet::getCurrentRec));

    if (partnerRec.isPresent()) {
      boolean isRelationShipEnded = isRelationShipEnded(partnerRec.get());
      return partnerRec
          .flatMap(this::getBsn)
          .map(bsn -> {
            if (isRelationShipEnded) {
              ProcuraRelative partner = getRelative(registered, bsn, EX_PARTNER);
              partner.addObstruction(RELATIONSHIP_HAS_ENDED);
              return partner;
            } else {
              return getRelative(registered, bsn, PARTNER);
            }
          });
    }
    return Optional.empty();
  }

  private boolean isRelationShipEnded(GbaWsPersonListRec rec) {
    return !rec.getElems(DATUM_ONTBINDING, REDEN_ONTBINDING, PLAATS_ONTBINDING, LAND_ONTBINDING)
        .isEmpty();
  }

  private Optional<Bsn> getBsn(GbaWsPersonListRec rec) {
    return rec.getElem(BSN).map(e -> new Bsn(e.getValue().getVal()));
  }
}
