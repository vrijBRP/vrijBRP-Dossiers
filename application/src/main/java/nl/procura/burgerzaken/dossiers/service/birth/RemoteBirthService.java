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

package nl.procura.burgerzaken.dossiers.service.birth;

import static nl.procura.burgerzaken.dossiers.converters.GbaRestBirthConverter.toGbaRestZaak;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.toLocalDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.converters.GbaRestBirthConverter;
import nl.procura.burgerzaken.dossiers.model.base.PersistableEnum;
import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;
import nl.procura.burgerzaken.dossiers.model.birth.Birth;
import nl.procura.burgerzaken.dossiers.model.birth.FamilySituationInfo;
import nl.procura.burgerzaken.dossiers.model.birth.NameSelectionInfo;
import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.burgerzaken.dossiers.service.BirthService;
import nl.procura.burgerzaken.dossiers.service.ProcuraWsService;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakToevoegenVraag;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;

@Service
public class RemoteBirthService implements BirthService {

  private final GbaClient             client;
  private final ProcuraWsService      personWsService;
  private final GbaRestBirthConverter converter;

  public static final String REASON_IS_DECEASED = "O";
  private static final long  MAX_LEGAL_DAYS     = 306L;

  public RemoteBirthService(GbaClient client,
      ProcuraWsService personWsService,
      GbaRestBirthConverter converter) {
    this.client = client;
    this.personWsService = personWsService;
    this.converter = converter;
  }

  @Override
  public Birth add(Birth birth) {
    GbaRestZaakToevoegenVraag request = new GbaRestZaakToevoegenVraag();
    request.setZaak(toGbaRestZaak(birth));
    return converter.toDomainModel(client.zaken()
        .addZaak(request)
        .getInhoud());
  }

  @Override
  public Birth update(Birth relocation) {
    throw new UnsupportedOperationException("BirthService.update is not supported yet");
  }

  @Override
  public Birth findByCaseNumber(String caseNumber) {
    return converter.toDomainModel(client.zaken()
        .getZaakByZaakId(caseNumber)
        .getInhoud());
  }

  @Override
  public NameSelectionInfo getNameSelectionInfo(Bsn bsnMother, Bsn bsnFather) {
    GbaWsPersonList mother = getRegistered(bsnMother, "mother");
    GbaWsPersonList father = getRegistered(bsnFather, "father / duomother");
    return getFirstMatchingChild(mother, father)
        .map(this::toNameSelectionInfo)
        .orElseGet(NameSelectionInfo::new);
  }

  @Override
  public FamilySituationInfo getFamilySituationInfo(LocalDate birthDate, Bsn bsnMother) {
    GbaWsPersonList mother = getRegistered(bsnMother, "mother");
    return mother.getCurrentRecords(GBACat.HUW_GPS).stream()
        .map(rec -> getFamilySituationInfo(birthDate, rec))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(new FamilySituationInfo());
  }

  private FamilySituationInfo getFamilySituationInfo(LocalDate birthDate, GbaWsPersonListRec rec) {
    FamilySituationInfo info = null;
    int marriageDate = NumberUtils.toInt(rec.getElemValue(GBAElem.DATUM_VERBINTENIS), -1);
    int endDate = NumberUtils.toInt(rec.getElemValue(GBAElem.DATUM_ONTBINDING), -1);
    String endReason = rec.getElemValue(GBAElem.REDEN_ONTBINDING);
    Bsn bsnPartner = new Bsn(rec.getElemValue(GBAElem.BSN));
    if (marriageDate > 0) {
      boolean marriedBeforeBirth = toLocalDate(marriageDate).isBefore(birthDate);
      if (marriedBeforeBirth) {
        if (endDate > 0) {
          boolean endedAfterBirth = toLocalDate(endDate).isAfter(birthDate);
          if (endedAfterBirth) {
            info = new FamilySituationInfo();
            info.setBornInPartnership(true);
            info.setBsnPartner(bsnPartner);
          } else if (REASON_IS_DECEASED.equalsIgnoreCase(endReason)) {
            boolean diedWithinLegalDeceasedPeriod = toLocalDate(endDate)
                .isAfter(birthDate.minusDays(MAX_LEGAL_DAYS + 1L));
            if (diedWithinLegalDeceasedPeriod) {
              info = new FamilySituationInfo();
              info.setBornInPartnership(true);
              info.setLegalDeceasedPeriodRuleApplicable(true);
              info.setBsnPartner(bsnPartner);
            }
          }
        } else {
          info = new FamilySituationInfo();
          info.setBornInPartnership(true);
          info.setBsnPartner(bsnPartner);
        }
      }
    }
    return info;
  }

  private NameSelectionInfo toNameSelectionInfo(BirthMatchChild child) {
    NameSelectionInfo info = new NameSelectionInfo();
    info.setJointChildren(true);
    info.setLastname(child.getLastName());
    info.setPrefix(child.getPrefix());
    info.setTitle(PersistableEnum.valueOfCode(TitlePredicateType.values(), child.getTitle()));
    return info;
  }

  private Optional<BirthMatchChild> getFirstMatchingChild(GbaWsPersonList mother,
      GbaWsPersonList father) {
    Set<BirthMatchChild> childrenMother = mother.getCurrentRecords(GBACat.KINDEREN)
        .stream()
        .map(BirthMatchChild::new)
        .filter(BirthMatchChild::isNotStillborn)
        .collect(Collectors.toSet());
    Set<BirthMatchChild> childrenFather = father.getCurrentRecords(GBACat.KINDEREN)
        .stream()
        .map(BirthMatchChild::new)
        .filter(BirthMatchChild::isNotStillborn)
        .collect(Collectors.toSet());

    return childrenMother.stream()
        .filter(child -> childrenFather
            .stream()
            .anyMatch(Predicate.isEqual(child)))
        .findFirst();
  }

  private GbaWsPersonList getRegistered(Bsn bsn, String personType) {
    List<GbaWsPersonList> personLists = personWsService.get(bsn.toLong());
    if (personLists.isEmpty()) {
      throw new ApiException(ApiErrorType.BAD_REQUEST, "No person found with the BSN of the " + personType);
    }
    if (personLists.size() > 1) {
      throw new ApiException(ApiErrorType.BAD_REQUEST, "Multiple people found with the BSN of the " + personType);
    }
    return personLists.get(0);
  }
}
