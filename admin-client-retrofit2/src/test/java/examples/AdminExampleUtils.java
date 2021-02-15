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

import com.google.gson.GsonBuilder;

import nl.procura.burgerzaken.dossiers.api.admin.v1.client.ApiClientConfiguration;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.DossierAdminApiClient;
import nl.procura.burgerzaken.dossiers.api.admin.v1.client.customizations.GsonTypeAdapters;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class AdminExampleUtils {

  private AdminExampleUtils() {
  }

  public static DossierAdminApiClient getClient() {

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
        .scopes(ApiClientConfiguration.SCOPE_ADMIN)
        .build();

    DossierAdminApiClient client = DossierAdminApiClient
        .builder()
        .config(config)
        .build();

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

    client.getApiClient()
        .getOkBuilder()
        .addInterceptor(logging);

    return client;
  }

  public static String toPrettyJson(Object object) {
    return GsonTypeAdapters
        .registerAdapters(new GsonBuilder())
        .serializeNulls()
        .setPrettyPrinting()
        .create().toJson(object);
  }
}
