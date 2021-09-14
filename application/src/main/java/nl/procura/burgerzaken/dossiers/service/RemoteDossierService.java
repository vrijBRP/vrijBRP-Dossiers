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

import static java.util.stream.Collectors.toList;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.toGbaRestPeriode;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestZaakStatusConverter.toGbaStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierStatus;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierSearchRequest;
import nl.procura.burgerzaken.dossiers.service.dossier.DossierService;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakZoekenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakZoekGegeven;

@Service
public class RemoteDossierService implements DossierService {

  private final GbaClient               client;
  private final GbaRestDossierConverter dossierConverter;
  private IntraRelocationService        intraRelocationService;
  private InterRelocationService        interRelocationService;
  private BirthService                  birthService;
  private ConfidentialityService        confidentialityService;

  public RemoteDossierService(GbaClient client,
      GbaRestDossierConverter dossierConverter,
      IntraRelocationService intraRelocationService,
      InterRelocationService interRelocationService,
      BirthService birthService,
      ConfidentialityService confidentialityService) {

    this.client = client;
    this.dossierConverter = dossierConverter;
    this.intraRelocationService = intraRelocationService;
    this.interRelocationService = interRelocationService;
    this.birthService = birthService;
    this.confidentialityService = confidentialityService;
  }

  @Override
  public Page<Dossier> find(DossierSearchRequest request) {
    return new PageImpl<>(client.zaken()
        .findZaken(toGbaRestZaakZoekenVraag(request))
        .getInhoud()
        .getZaken()
        .stream()
        .map(dossierConverter::toDossier)
        .collect(Collectors.toList()));
  }

  @Override
  public void delete(String clientId, String caseNumber) {
    client.zaken().deleteZaakByZaakId(caseNumber);
  }

  @Override
  public <T extends DossierTypeService> T getDossierTypeService(DossierType dossierType, Class<T> clazz) {
    switch (dossierType) {
      case INTRA_MUNICIPAL_RELOCATION:
        return (T) intraRelocationService;
      case INTER_MUNICIPAL_RELOCATION:
        return (T) interRelocationService;
      case BIRTH:
        return (T) birthService;
      case CONFIDENTIALITY:
        return (T) confidentialityService;
      default:
        throw new UnsupportedOperationException("dossier type '" + dossierType + "' is not supported");
    }
  }

  private GbaRestZaakZoekenVraag toGbaRestZaakZoekenVraag(DossierSearchRequest request) {
    GbaRestZaakZoekenVraag vraag = new GbaRestZaakZoekenVraag();
    vraag.setZaakIds(request.getDossierIds());
    vraag.setZoekGegevens(Collections.singletonList(GbaRestZaakZoekGegeven.ALGEMENE_ZAAKGEGEVENS));

    vraag.setZaakTypes(Optional.ofNullable(request.getTypes())
        .map(this::toGbaZaakTypes)
        .orElse(dossierConverter.supportedTypes()));

    if (request.getStatusses() != null) {
      vraag.setZaakStatussen(request
          .getStatusses()
          .stream()
          .map(s -> toGbaStatus(DossierStatus.valueOfCode(s)))
          .collect(Collectors.toUnmodifiableList()));
    }

    // The front-desk api only support one personId, not a list
    if (request.getBsns() != null) {
      if (request.getBsns().size() > 1) {
        throw new IllegalArgumentException("Only one BSN is supported, not multiple.");
      }
      vraag.setPersoonId(String.valueOf(request.getBsns().get(0)));
    }

    vraag.setIngangsDatum(toGbaRestPeriode(request.getStartDatePeriod()).orElse(null));
    vraag.setInvoerDatum(toGbaRestPeriode(request.getEntryDateTimePeriod()).orElse(null));

    if (request.getPageRequest() != null) {
      vraag.setMax(request.getPageRequest().getPageSize());
    }

    // NOT SUPPORTED SEARCH OPTIONS AT THIS MOMENT
    // -------------------------------------------
    // ZaakAttributen
    // MutatieDatum
    // Sortering

    return vraag;
  }

  private List<GbaRestZaakType> toGbaZaakTypes(List<String> types) {
    return types.stream()
        .map(DossierType::valueOfCode)
        .map(dossierConverter::toGbaZaakType)
        .collect(toList());
  }
}
