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

package nl.procura.burgerzaken.dossiers.components;

import static org.springframework.util.Base64Utils.encodeToString;
import static reactor.core.publisher.Mono.error;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRequest;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListResponse;

import lombok.Builder;

public class GbaWsPersonListClient {

  private final WebClient client;

  @Builder
  public GbaWsPersonListClient(GbaWsPersonListClientConfig config) {
    if (config.getWebClient() != null) {
      this.client = config.getWebClient();
    } else {
      WebClient.Builder builder = WebClient.builder();
      builder.exchangeStrategies(getExchangeStrategies());
      builder.baseUrl(config.getBaseUrl());
      builder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      builder.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      if (Strings.isNotEmpty(config.getUsername())) {
        String auth = "Basic " + encodeToString((config.getUsername() + ":" + config.getPassword()).getBytes());
        builder.defaultHeader(HttpHeaders.AUTHORIZATION, auth);
      } else {
        builder.defaultHeader(HttpHeaders.AUTHORIZATION, config.getAuthorization());
      }
      this.client = builder.build();
    }
  }

  /**
   * Removed memory size constraint
   */
  private ExchangeStrategies getExchangeStrategies() {
    return ExchangeStrategies.builder().codecs(stratConfig -> stratConfig
        .defaultCodecs()
        .maxInMemorySize(-1))
        .build();
  }

  public GbaWsPersonListResponse getPersonLists(GbaWsPersonListRequest request) {
    return client
        .post()
        .uri("/v2/personlists")
        .body(BodyInserters.fromValue(request))
        .retrieve()
        .onStatus(HttpStatus::isError, response -> error(new RuntimeException(response.statusCode().toString())))
        .bodyToMono(GbaWsPersonListResponse.class)
        .block();
  }
}
