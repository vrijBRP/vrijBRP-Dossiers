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

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.relocations.IntraMunicipalRelocation;

@Primary
@Service
public class TestIntraRelocationService implements IntraRelocationService {

  private IntraMunicipalRelocation dossier;

  @Override
  public void addConsent(String dossierId, Person consenter, String consentType) {
  }

  @Override
  public IntraMunicipalRelocation add(IntraMunicipalRelocation relocation) {
    this.dossier = relocation;
    relocation.getDossier().setCaseNumber("1234");
    return this.dossier;
  }

  @Override
  public IntraMunicipalRelocation update(IntraMunicipalRelocation dossier) {
    this.dossier = dossier;
    return this.dossier;
  }

  @Override
  public IntraMunicipalRelocation findByCaseNumber(String caseNumber) {
    return this.dossier;
  }
}
