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

import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.toPerson;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestToestemmingStatusConverter.ofConsent;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestToestemmingStatusConverter.toGbaAangifteStatus;
import static nl.procura.burgerzaken.dossiers.model.dossier.PersonRole.RELOCATOR;

import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.burgerzaken.dossiers.model.relocations.Relocator;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.*;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.inwoning.GbaRestInwoningVraag;

public final class GbaRestRelocationConverter {

  private GbaRestRelocationConverter() {
  }

  public static GbaRestInwoningVraag toGbaRestInwoningVraag(
      String dossierId,
      Person consenter,
      String consentType) {

    GbaRestInwoningVraag request = new GbaRestInwoningVraag();
    request.setZaakId(dossierId);
    request.setInwoning(toGbaRestInwoning(true, consenter, consentType));
    return request;
  }

  public static GbaRestInwoning toGbaRestInwoning(boolean consent, Person consenter, String consentType) {
    GbaRestInwoning inwoning = new GbaRestInwoning();
    inwoning.setSprakeVanInwoning(consent);
    if (consenter != null) {
      inwoning.setToestemminggever(toGbaToestemminggever(consenter));
    }
    if (consent) {
      GbaRestToestemmingStatus toestemmingStatus = ofConsent(consentType);
      inwoning.setToestemmingStatus(toestemmingStatus);
      inwoning.setAangifteStatus(toGbaAangifteStatus(toestemmingStatus));
    }
    return inwoning;
  }

  public static Relocator toRelocator(GbaRestVerhuizer verhuizer) {
    Person person = toPerson(verhuizer, RELOCATOR).orElseThrow();
    Relocator relocator = new Relocator(person);
    relocator.setDeclaration(verhuizer.getAangifte().getCode());
    return relocator;
  }

  public static GbaRestAangever toGbaAangever(Person person) {
    GbaRestAangever aangever = new GbaRestAangever();
    aangever.setBsn(person.getBsn());
    GbaRestContactgegevens contactgegevens = new GbaRestContactgegevens();
    contactgegevens.setEmail(person.getEmail());
    contactgegevens.setTelefoonThuis(person.getPhoneNumber());
    aangever.setContactgegevens(contactgegevens);
    return aangever;
  }

  public static GbaRestToestemminggever toGbaToestemminggever(Person person) {
    GbaRestToestemminggever toestemminggever = new GbaRestToestemminggever();
    toestemminggever.setBsn(person.getBsn());
    return toestemminggever;
  }

  public static GbaRestHoofdbewoner toGbaHoofdbewoner(Person person) {
    GbaRestHoofdbewoner hoofdbewoner = new GbaRestHoofdbewoner();
    hoofdbewoner.setBsn(person.getBsn());
    return hoofdbewoner;
  }

  public static GbaRestVerhuizer toGbaVerhuizer(Relocator person) {
    GbaRestVerhuizer verhuizer = new GbaRestVerhuizer();
    verhuizer.setBsn(person.getPerson().getBsn());
    verhuizer.setAangifte(toGbaAangifteSoort(person.getDeclaration()));
    return verhuizer;
  }

  public static GbaRestAangifteSoort toGbaAangifteSoort(String declaration) {
    for (GbaRestAangifteSoort value : GbaRestAangifteSoort.values()) {
      if (value.getCode().equals(declaration)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Declaration " + declaration + " doesn't exist");
  }

  public static GbaRestFunctieAdres toGbaFunctieAdres(String addressFunction) {
    for (GbaRestFunctieAdres value : GbaRestFunctieAdres.values()) {
      if (value.getCode().equals(addressFunction)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Address function " + addressFunction + " doesn't exist");
  }

}
