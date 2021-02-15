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

package examples;

import static com.google.gson.JsonParser.parseString;

import com.google.gson.GsonBuilder;

import nl.procura.burgerzaken.dossiers.api.external.v1.client.ApiClientConfiguration;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.DossierApiClient;
import nl.procura.burgerzaken.dossiers.api.external.v1.client.customizations.GsonTypeAdapters;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class ExampleUtils {

  private ExampleUtils() {
  }

  public static DossierApiClient getClient() {

    String clientId = "sim";
    String clientSecret = "TokhE5rMhcpFfRDUySzB7sJPAaZAaTMH";

    String baseURL = "http://localhost:9000/dossiers/";
    //    String baseURL = "http://srv-411t:9085/";
    String tokenUrl = baseURL + "oauth/token";

    ApiClientConfiguration config = ApiClientConfiguration.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .tokenUrl(tokenUrl)
        .baseUrl(baseURL)
        .scopes(ApiClientConfiguration.SCOPE_API)
        .build();

    DossierApiClient client = DossierApiClient
        .builder()
        .config(config)
        .build();

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new PrettyLogger());
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

    client.getApiClient()
        .getOkBuilder()
        .addNetworkInterceptor(logging);

    return client;
  }

  public static class PrettyLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
      final String logName = "Message";
      if (message.startsWith("{")) {
        System.out.println(logName);
        System.out.println(toPrettyJson(message));
      } else if (message.startsWith("--> POST")) {
        System.out.println("Request");
        System.out.println("=======");
      } else if (message.startsWith("--> END POST")) {
        System.out.println("Response");
        System.out.println("=======");
      } else {
        System.out.println(message);
      }
    }
  }

  public static String toPrettyJson(String object) {
    return GsonTypeAdapters
        .registerAdapters(new GsonBuilder())
        .serializeNulls()
        .setPrettyPrinting()
        .create().toJson(parseString(object));
  }
}
