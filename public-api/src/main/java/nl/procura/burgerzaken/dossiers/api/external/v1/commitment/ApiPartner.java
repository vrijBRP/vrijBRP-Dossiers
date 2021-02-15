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

package nl.procura.burgerzaken.dossiers.api.external.v1.commitment;

import nl.procura.burgerzaken.dossiers.api.external.v1.base.ApiContactInformation;
import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiPerson;
import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.commitment.CommitmentPartner;
import nl.procura.burgerzaken.dossiers.model.dossier.Person;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "CommitmentPartner")
public class ApiPartner extends ApiPerson {

  @Schema(name = "nameAfterCommitment")
  private ApiNameAfterCommitment nameAfterCommitment;

  public CommitmentPartner toPartner() {
    CommitmentPartner partner = new CommitmentPartner();
    partner.setBsn(getBsn());
    if (getContactInformation() != null) {
      partner.setEmail(getContactInformation().getEmail());
      partner.setTelephoneNumber(getContactInformation().getTelephoneNumber());
    }
    if (nameAfterCommitment != null) {
      partner.setNameUse(nameAfterCommitment.toCommitmentNameUse());
    }
    return partner;
  }

  public static ApiPartner of(CommitmentPartner partner) {
    if (ModelValidation.isValid(partner)) {
      Person person = new Person();
      person.setPhoneNumber(partner.getTelephoneNumber());
      person.setEmail(partner.getEmail());
      return new ApiPartner(
          ApiPartner.builder()
              .bsn(String.valueOf(partner.getBsn()))
              .contactInformation(ApiContactInformation.of(person))
              .nameAfterCommitment(ApiNameAfterCommitment.of(partner.getNameUse())));
    }
    return null;
  }
}
