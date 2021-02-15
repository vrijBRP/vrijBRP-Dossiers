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

package nl.procura.burgerzaken.dossiers.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import nl.procura.burgerzaken.dossiers.api.external.v1.error.ApiError;
import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class ApiErrorResponse extends ApiResponse {

  public ApiErrorResponse(ApiErrorType errorType) {
    setDescription(errorType.getDescription());
    String name = ApiError.class.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class).name();
    setContent(new Content()
        .addMediaType(APPLICATION_JSON_VALUE, new MediaType()
            .schema(new Schema<ApiError>()
                .$ref(name))));
  }
}
