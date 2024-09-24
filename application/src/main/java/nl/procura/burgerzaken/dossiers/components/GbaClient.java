/*
 * Copyright 2023 - 2024 Procura B.V.
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

import static nl.procura.gba.web.rest.v2.GbaRestDataImportResourceV2.ADD;
import static nl.procura.gba.web.rest.v2.GbaRestDataImportResourceV2.BASE_DATAIMPORT_URI;
import static nl.procura.gba.web.rest.v2.GbaRestVerhuizingResourceV2.BASE_VERHUIZING_URI;
import static nl.procura.gba.web.rest.v2.GbaRestVerhuizingResourceV2.UPDATE_INWONING_URI;
import static nl.procura.gba.web.rest.v2.GbaRestZaakResourceV2.BASE_ZAKEN_URI;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.error.ApiException;
import nl.procura.gba.web.rest.v2.GbaRestDataImportResourceV2;
import nl.procura.gba.web.rest.v2.GbaRestDocumentV2;
import nl.procura.gba.web.rest.v2.GbaRestVerhuizingResourceV2;
import nl.procura.gba.web.rest.v2.GbaRestZaakResourceV2;
import nl.procura.gba.web.rest.v2.model.base.GbaRestAntwoord;
import nl.procura.gba.web.rest.v2.model.dataimport.GbaRestDataImport;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakDocumentToevoegenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakDocumentenZoekenAntwoord;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakStatusUpdateVraag;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakToevoegenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakUpdateVraag;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakZoekenAntwoord;
import nl.procura.gba.web.rest.v2.model.zaken.GbaRestZaakZoekenVraag;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaak;
import nl.procura.gba.web.rest.v2.model.zaken.base.GbaRestZaakDocument;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Builder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

public class GbaClient {

  private static final Logger   LOGGER = LoggerFactory.getLogger(GbaClient.class);
  private final WebClient       client;
  private final GbaClientConfig config;

  private static final ParameterizedTypeReference<GbaRestAntwoord<GbaRestZaak>> ZAAK_TYPE = new ParameterizedTypeReference<>() {
  };

  private static final ParameterizedTypeReference<GbaRestAntwoord<?>> EMPTY_TYPE = new ParameterizedTypeReference<>() {
  };

  private static final ParameterizedTypeReference<GbaRestAntwoord<GbaRestZaakZoekenAntwoord>> ZAAK_ZOEKEN_TYPE = new ParameterizedTypeReference<>() {
  };

  private static final ParameterizedTypeReference<GbaRestAntwoord<GbaRestZaakDocument>> ZAAK_DOCUMENT_TYPE = new ParameterizedTypeReference<>() {
  };

  private static final ParameterizedTypeReference<GbaRestAntwoord<GbaRestZaakDocumentenZoekenAntwoord>> ZAAK_DOCUMENT_ZOEKEN_TYPE = new ParameterizedTypeReference<>() {
  };

  private static final ParameterizedTypeReference<GbaRestAntwoord<GbaRestDataImport>> DATAIMPORT_TYPE = new ParameterizedTypeReference<>() {
  };

  @Builder
  public GbaClient(GbaClientConfig config) {
    this.config = config;
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(config.getTimeoutSeconds() * 1_000);
    factory.setReadTimeout(config.getTimeoutSeconds() * 1_000);
    TcpClient tcpClient = TcpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getTimeoutSeconds() * 1_000)
        .doOnConnected(connection -> {
          connection.addHandlerLast(new ReadTimeoutHandler(config.getTimeoutSeconds()));
          connection.addHandlerLast(new WriteTimeoutHandler(config.getTimeoutSeconds()));
        });
    client = WebClient.builder()
        .codecs(codecConfigurer -> codecConfigurer.defaultCodecs().maxInMemorySize(-1))
        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
        .baseUrl(config.getBaseUrl())
        .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth(config.getUsername(), config.getPassword()))
        .build();
  }

  public GbaRestVerhuizingResourceV2 verhuizing() {

    return request -> handleError(
        post(BASE_VERHUIZING_URI + UPDATE_INWONING_URI,
            request, ZAAK_ZOEKEN_TYPE));
  }

  public GbaRestDataImportResourceV2 dataImport() {
    return request -> handleError(
        post(BASE_DATAIMPORT_URI + ADD,
            request, DATAIMPORT_TYPE));
  }

  public GbaRestZaakResourceV2 zaken() {

    return new GbaRestZaakResourceV2() {

      @Override
      public GbaRestAntwoord<GbaRestZaak> getZaakByZaakId(String zaakId) {
        return handleError(get(UriComponentsBuilder
            .fromUriString(BASE_ZAKEN_URI + GET_ZAAK_BY_ZAAK_ID_URI)
            .buildAndExpand(zaakId)
            .toUriString(),
            ZAAK_TYPE));
      }

      @Override
      public GbaRestAntwoord<GbaRestZaak> addZaak(GbaRestZaakToevoegenVraag request) {
        return handleError(post(BASE_ZAKEN_URI + ADD_ZAAK_URI,
            request,
            ZAAK_TYPE));
      }

      @Override
      public GbaRestAntwoord<GbaRestZaak> updateZaak(GbaRestZaakUpdateVraag request) {
        return handleError(post(BASE_ZAKEN_URI + UPDATE_ZAAK_URI,
            request,
            ZAAK_TYPE));
      }

      @Override
      public GbaRestAntwoord<?> deleteZaakByZaakId(String zaakId) {
        return handleError(delete(UriComponentsBuilder
            .fromUriString(BASE_ZAKEN_URI + DELETE_ZAAK_BY_ZAAK_ID_URI)
            .buildAndExpand(zaakId)
            .toUriString(),
            EMPTY_TYPE));
      }

      @Override
      public GbaRestAntwoord<?> updateZaakStatus(GbaRestZaakStatusUpdateVraag request) {
        return handleError(post(BASE_ZAKEN_URI + UPDATE_ZAAK_STATUS_URI, request, EMPTY_TYPE));
      }

      @Override
      public GbaRestAntwoord<GbaRestZaakZoekenAntwoord> findZaken(GbaRestZaakZoekenVraag request) {
        return handleError(post(BASE_ZAKEN_URI + FIND_ZAAK_URI,
            request,
            ZAAK_ZOEKEN_TYPE));
      }
    };
  }

  public GbaRestDocumentV2 documenten() {
    return new GbaRestDocumentV2() {

      @Override
      public GbaRestAntwoord<GbaRestZaakDocumentenZoekenAntwoord> getDocumentsByZaakId(String zaakId) {
        return handleError(get(UriComponentsBuilder
            .fromUriString(BASE_ZAKEN_URI + GET_DOCUMENTS_BY_ZAAK_ID_URI)
            .buildAndExpand(zaakId)
            .toUriString(), ZAAK_DOCUMENT_ZOEKEN_TYPE));
      }

      @Override
      public InputStreamResource getDocumentById(String zaakId, String documentId) {
        return get(UriComponentsBuilder
            .fromUriString(BASE_ZAKEN_URI + GET_DOCUMENT_BY_ID_URI)
            .buildAndExpand(zaakId, documentId)
            .toUriString());
      }

      @Override
      public GbaRestAntwoord<GbaRestZaakDocument> addDocument(String zaakId,
          GbaRestZaakDocumentToevoegenVraag request) {
        return handleError(post(UriComponentsBuilder
            .fromUriString(BASE_ZAKEN_URI + BASE_DOCUMENTS_URI)
            .buildAndExpand(zaakId)
            .toUriString(), request, ZAAK_DOCUMENT_TYPE));
      }
    };
  }

  private <T> T get(String uri, ParameterizedTypeReference<T> reference) {
    try {
      return client.get()
          .uri(uri)
          .retrieve()
          .bodyToMono(reference)
          .block(Duration.ofSeconds(config.getTimeoutSeconds()));
    } catch (WebClientResponseException.NotFound e) {
      LOGGER.debug(e.getMessage(), e);
      throw new ApiException(ApiErrorType.NOT_FOUND);
    }
  }

  private InputStreamResource get(String uri) {
    ClientResponse response = client.get()
        .uri(uri)
        .exchange()
        .block(Duration.ofSeconds(config.getTimeoutSeconds()));
    // somehow WebClient doesn't extract filename, do it ourselves
    HttpHeaders headers = response.headers().asHttpHeaders();
    InputStream inputStream;
    try {
      inputStream = response.bodyToMono(InputStreamResource.class).block().getInputStream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return new InputStreamResource(inputStream) {

      @Override
      public String getFilename() {
        return headers.getContentDisposition().getFilename();
      }

      @Override
      public long contentLength() throws IOException {
        return headers.getContentLength();
      }
    };
  }

  private <T> T delete(String uri, ParameterizedTypeReference<T> reference) {
    try {
      return client.delete()
          .uri(uri)
          .retrieve()
          .bodyToMono(reference)
          .block(Duration.ofSeconds(config.getTimeoutSeconds()));
    } catch (HttpClientErrorException.NotFound e) {
      LOGGER.debug(e.getMessage(), e);
      throw new ApiException(ApiErrorType.NOT_FOUND);
    }
  }

  private <S, T> T post(String uri, S requestEntity, ParameterizedTypeReference<T> reference) {
    return client.post()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestEntity)
        .retrieve()
        .bodyToMono(reference)
        .block(Duration.ofSeconds(config.getTimeoutSeconds()));
  }

  private <T> GbaRestAntwoord<T> handleError(GbaRestAntwoord<T> antwoord) {
    if (StringUtils.isNotBlank(antwoord.getFoutmelding())) {
      throw new RuntimeException(antwoord.getFoutmelding());
    }
    return antwoord;
  }
}
