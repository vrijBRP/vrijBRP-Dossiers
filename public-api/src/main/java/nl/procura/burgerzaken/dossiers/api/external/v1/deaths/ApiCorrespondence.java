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

package nl.procura.burgerzaken.dossiers.api.external.v1.deaths;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import nl.procura.burgerzaken.dossiers.model.base.ModelValidation;
import nl.procura.burgerzaken.dossiers.model.deaths.Correspondence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Correspondence")
public class ApiCorrespondence {

  @Schema(name = "communicationType")
  @NotNull(message = "communicationType is mandatory")
  private ApiCommunicationType communicationType;

  @Schema(name = "organization")
  private String organization;

  @Schema(name = "departement")
  private String departement;

  @Schema(name = "name")
  @NotEmpty(message = "name is mandatory")
  private String name;

  @Schema(name = "email")
  private String email;

  @Schema(name = "telephoneNumber")
  private String telephoneNumber;

  @Schema(name = "street")
  private String street = "";

  @Schema(name = "houseNumber")
  private Integer houseNumber;

  @Schema(name = "houseNumberLetter")
  private String houseNumberLetter;

  @Schema(name = "houseNumberAddition")
  private String houseNumberAddition;

  @Schema(name = "postalCode")
  private String postalCode;

  @Schema(name = "residence")
  private String residence;

  public Correspondence toCorrespondence() {
    Correspondence correspondence = new Correspondence();
    correspondence.setCommunicationType(communicationType.getType());
    correspondence.setOrganization(organization);
    correspondence.setDepartement(departement);
    correspondence.setName(name);
    correspondence.setEmail(email);
    correspondence.setTelephoneNumber(telephoneNumber);
    correspondence.setStreet(street);
    correspondence.setHouseNumber(houseNumber);
    correspondence.setHouseNumberLetter(houseNumberLetter);
    correspondence.setHouseNumberAddition(houseNumberAddition);
    correspondence.setPostalCode(postalCode);
    correspondence.setResidence(residence);
    return correspondence;
  }

  public static ApiCorrespondence of(Correspondence correspondence) {
    if (ModelValidation.isValid(correspondence)) {
      return ApiCorrespondence.builder()
          .communicationType(ApiCommunicationType.valueOfType(correspondence.getCommunicationType()))
          .organization(correspondence.getOrganization())
          .departement(correspondence.getDepartement())
          .name(correspondence.getName())
          .email(correspondence.getEmail())
          .telephoneNumber(correspondence.getTelephoneNumber())
          .street(correspondence.getStreet())
          .houseNumber(correspondence.getHouseNumber())
          .houseNumberLetter(correspondence.getHouseNumberLetter())
          .houseNumberAddition(correspondence.getHouseNumberAddition())
          .postalCode(correspondence.getPostalCode())
          .residence(correspondence.getResidence())
          .build();
    }
    return null;
  }
}
