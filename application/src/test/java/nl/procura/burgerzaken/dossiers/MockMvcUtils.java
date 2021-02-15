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

package nl.procura.burgerzaken.dossiers;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.hamcrest.collection.IsEmptyCollection;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.oauth.Client;

public final class MockMvcUtils {

  private MockMvcUtils() {
  }

  public static String givenCredentials(MockMvc mockMvc, Client client, String scope) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + base64Encode(client.clientId() + ":" + client.rawSecret()));
    return givenCredentials(mockMvc, httpHeaders, scope);
  }

  public static String givenCredentials(MockMvc mockMvc, HttpHeaders authHeaders, String scope) {
    MvcResult result;
    try {
      result = mockMvc.perform(post("/oauth/token?grant_type=client_credentials&scope=" + scope).headers(authHeaders))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.access_token", notNullValue()))
          .andExpect(jsonPath("$.access_token", not(IsEmptyCollection.empty())))
          .andExpect(jsonPath("$.token_type", is("bearer")))
          .andExpect(jsonPath("$.expires_in", isA(Number.class)))
          .andReturn();
    } catch (Exception e) {
      throw new AssertionError(e);
    }
    Map<String, String> map;
    try {
      map = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
    } catch (JsonProcessingException | UnsupportedEncodingException e) {
      throw new AssertionError(e);
    }
    return map.get("access_token");
  }

  public static HttpHeaders authorization(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    return headers;
  }

  public static HttpHeaders authorization(String user, String password) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Basic " + base64Encode(user + ":" + password));
    return headers;
  }

  private static String base64Encode(String value) {
    return new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
  }

}
