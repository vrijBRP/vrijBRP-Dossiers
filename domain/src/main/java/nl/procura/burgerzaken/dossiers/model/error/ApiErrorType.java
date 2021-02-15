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

package nl.procura.burgerzaken.dossiers.model.error;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import nl.procura.burgerzaken.dossiers.util.Constants;

import lombok.Getter;

@Getter
public enum ApiErrorType {

  INTERNAL_ERROR(Constants.Errors.INTERNAL, INTERNAL_SERVER_ERROR,
      "The request failed due to an internal server error", false),
  NOT_FOUND(Constants.Errors.NOT_FOUND, HttpStatus.NOT_FOUND,
      "The requested resource could not be found", true),
  BAD_REQUEST(Constants.Errors.BAD_REQUEST, HttpStatus.BAD_REQUEST,
      "The provided request is incorrect", true);

  private final String     code;
  private final String     description;
  private final HttpStatus httpStatus;
  private boolean          addToResponse;

  ApiErrorType(String code, HttpStatus httpStatus, String description, boolean addToResponse) {
    this.code = code;
    this.description = description;
    this.httpStatus = httpStatus;
    this.addToResponse = addToResponse;
  }

  public static Optional<ApiErrorType> getByReason(String reason) {
    return Arrays.stream(values()).filter(e -> e.getCode().equals(reason)).findFirst();
  }
}
