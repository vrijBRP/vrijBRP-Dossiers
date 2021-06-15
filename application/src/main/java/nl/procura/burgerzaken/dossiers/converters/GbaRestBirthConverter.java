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

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestConverter.*;
import static nl.procura.burgerzaken.dossiers.converters.GbaRestDossierConverter.toGbaRestZaakAlgemeen;
import static nl.procura.burgerzaken.dossiers.model.base.PersistableEnum.valueOfCode;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.BIRTH;
import static nl.procura.burgerzaken.dossiers.model.dossier.PersonRole.*;

import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.base.GenderType;
import nl.procura.burgerzaken.dossiers.model.base.NameSelection;
import nl.procura.burgerzaken.dossiers.model.base.TitlePredicateType;
import nl.procura.burgerzaken.dossiers.model.birth.Birth;
import nl.procura.burgerzaken.dossiers.model.birth.BirthChild;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;
import nl.procura.burgerzaken.dossiers.model.dossier.DossierType;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;
import nl.procura.gba.web.rest.v2.model.base.GbaRestGeslacht;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakType;
import nl.procura.gba.web.rest.v2.model.zaken.base.persoon.GbaRestPersoon;
import nl.procura.gba.web.rest.v2.model.zaken.geboorte.GbaRestGeboorte;
import nl.procura.gba.web.rest.v2.model.zaken.geboorte.GbaRestGeboorteVerzoek;
import nl.procura.gba.web.rest.v2.model.zaken.geboorte.GbaRestKind;
import nl.procura.gba.web.rest.v2.model.zaken.verhuizing.GbaRestContactgegevens;

@Component
public class GbaRestBirthConverter implements GbaConverter<Birth> {

    @Override
    public DossierType dossierType() {
        return BIRTH;
    }

    @Override
    public GbaRestZaakType zaakType() {
        return GbaRestZaakType.GEBOORTE;
    }

    @Override
    public Birth toDomainModel(GbaRestZaak zaak) {
        Dossier dossier = toDossier(zaak);
        Birth birth = new Birth(dossier);
        GbaRestGeboorte geb = zaak.getGeboorte();
        //People
        toPersonWithContactinfo(geb.getAangever(), DECLARANT).ifPresent(birth::setDeclarant);
        toPersonWithContactinfo(geb.getMoeder(), MOTHER).ifPresent(birth::setMother);
        geb.getKinderen().forEach(kind -> birth.addChild(toChild(kind)));

        GbaRestGeboorteVerzoek verzoek = geb.getVerzoek();
        if (verzoek != null) {
          toPersonWithContactinfo(verzoek.getVaderOfDuoMoeder(), FATHER_DUO_MOTHER)
                    .ifPresent(birth::setFatherDuoMother);
            birth.setNameSelection(new NameSelection(verzoek.getGeslachtsnaam(),
                    verzoek.getVoorvoegsel(),
                    valueOfCode(TitlePredicateType.values(), verzoek.getTitelPredikaat())));
        }
        return birth;
    }

    private static BirthChild toChild(GbaRestKind kind) {
        BirthChild child = new BirthChild();
        child.setFirstname(kind.getVoornamen());
        child.setGender(toGenderType(kind.getGeslacht()));
        child.setBirthDate(toLocalDate(kind.getGeboortedatum()));
        child.setBirthTime(toLocalTime(kind.getGeboortetijd()));
        return child;
    }

    public static GbaRestZaak toGbaRestZaak(Birth birth) {
        Dossier dossier = birth.getDossier();

        GbaRestGeboorte geboorte = new GbaRestGeboorte();
        // People
        birth.getDeclarant().ifPresent(p -> geboorte.setAangever(toGbaPersoon(p)));
        birth.getMother().ifPresent(p -> geboorte.setMoeder(toGbaPersoon(p)));
        geboorte.setKinderen(birth.getChildren().stream()
                .map(GbaRestBirthConverter::toGbaKind)
                .collect(toList()));

        // "Requested bsn father/duomother / name selection
        geboorte.setVerzoek(toGbaRestGeboorteVerzoek(birth));

        GbaRestZaak zaak = new GbaRestZaak();
        zaak.setAlgemeen(toGbaRestZaakAlgemeen(dossier, GbaRestZaakType.GEBOORTE));
        zaak.setGeboorte(geboorte);
        return zaak;
    }

    private static GbaRestGeboorteVerzoek toGbaRestGeboorteVerzoek(Birth birth) {
        GbaRestGeboorteVerzoek verzoek = new GbaRestGeboorteVerzoek();
        birth.getFatherDuoMother().ifPresent(p -> verzoek.setVaderOfDuoMoeder(toGbaPersoon(p)));
        verzoek.setGeslachtsnaam(birth.getNameSelection().getLastName());
        verzoek.setVoorvoegsel(birth.getNameSelection().getPrefix());
        verzoek.setTitelPredikaat(ofNullable(birth.getNameSelection().getTitle())
                .map(TitlePredicateType::getCode)
                .orElse(null));
        return verzoek;
    }

    private static GbaRestKind toGbaKind(BirthChild child) {
        GbaRestKind kind = new GbaRestKind();
        kind.setVoornamen(child.getFirstname());
        kind.setGeslacht(toGbaRestGeslacht(child.getGender()));
        kind.setGeboortedatum(toIntegerDate(child.getBirthDate()));
        kind.setGeboortetijd(toIntegerTime(child.getBirthTime()));
        return kind;
    }

    private static GbaRestGeslacht toGbaRestGeslacht(GenderType genderType) {
        switch (genderType) {
            case MAN:
                return GbaRestGeslacht.MAN;
            case WOMAN:
                return GbaRestGeslacht.VROUW;
            case UNKNOWN:
                return GbaRestGeslacht.ONBEKEND;
            default:
                return null;
        }
    }

    private static GenderType toGenderType(GbaRestGeslacht restGeslacht) {
        switch (restGeslacht) {
            case MAN:
                return GenderType.MAN;
            case VROUW:
                return GenderType.WOMAN;
            case ONBEKEND:
                return GenderType.UNKNOWN;
            default:
                return null;
        }
    }

    private static GbaRestPersoon toGbaPersoon(Person person) {
        GbaRestPersoon aangever = new GbaRestPersoon();
        aangever.setBsn(person.getBsn());
        GbaRestContactgegevens contactgegevens = new GbaRestContactgegevens();
        contactgegevens.setEmail(person.getEmail());
        contactgegevens.setTelefoonThuis(person.getPhoneNumber());
        aangever.setContactgegevens(contactgegevens);
        return aangever;
    }

}
