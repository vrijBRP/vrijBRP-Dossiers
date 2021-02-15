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

package nl.procura.burgerzaken.dossiers.api.external.v1.resources;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.burgerzaken.dossiers.components.ApiAccess;

import lombok.SneakyThrows;

public class MockTest {

  private ResultActions             actions;
  private MockMvc                   mockMvc;
  private ApiAccess                 apiAccess;
  protected ObjectMapper            mapper;
  private final Map<String, String> queryParams = new LinkedHashMap<>();

  private MockTest() {
  }

  public static MockTest builder(MockMvc mockMvc, ApiAccess apiAccess, ObjectMapper mapper) {
    return new MockTest().init(mockMvc, apiAccess, mapper);
  }

  private MockTest init(MockMvc mockMvc, ApiAccess apiAccess, ObjectMapper mapper) {
    this.mockMvc = mockMvc;
    this.apiAccess = apiAccess;
    this.mapper = mapper;
    return this;
  }

  @SneakyThrows
  public MockTest post(String uri, Object obj) {
    actions = mockMvc.perform(MockMvcRequestBuilders.post(uri)
        .headers(apiAccess.authorization())
        .content(mapper.writeValueAsString(obj))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
    return this;
  }

  @SneakyThrows
  public MockTest post(String uri, Object obj, Object... uriVars) {
    actions = mockMvc.perform(MockMvcRequestBuilders.post(uri, uriVars)
        .headers(apiAccess.authorization())
        .content(mapper.writeValueAsString(obj))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
    return this;
  }

  @SneakyThrows
  public MockTest get(String uri, Object... uriVars) {
    MockHttpServletRequestBuilder accept = MockMvcRequestBuilders.get(uri, uriVars)
        .headers(apiAccess.authorization())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);
    queryParams.forEach(accept::queryParam);
    actions = mockMvc.perform(accept);
    return this;
  }

  @SneakyThrows
  public MockTest queryParam(String key, Object val) {
    queryParams.put(key, val.toString());
    return this;
  }

  @SneakyThrows
  public MockTest delete(String uri, Object obj) {
    actions = mockMvc.perform(MockMvcRequestBuilders.delete(uri, obj)
        .headers(apiAccess.authorization())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
    return this;
  }

  @SneakyThrows
  public MockTest documentation(String documentation) {
    if (StringUtils.isNotBlank(documentation)) {
      actions.andDo(documentPrettyPrintReqResp(documentation));
    }
    return this;
  }

  @SneakyThrows
  public MockTest status(ResultMatcher status) {
    actions.andExpect(status);
    return this;
  }

  @SneakyThrows
  public MockTest error(String errorMessage) {
    actions.andExpect(jsonPath("$.cause.message", is(errorMessage)));
    return this;
  }

  @SneakyThrows
  public <T> T toClass(TypeReference<T> valueTypeRef) {
    return mapper.readValue(actions.andReturn().getResponse().getContentAsByteArray(), valueTypeRef);
  }

  @SneakyThrows
  public <T> T toClass(Class<T> clazz) {
    return mapper.readValue(actions.andReturn().getResponse().getContentAsByteArray(), clazz);
  }

  public static RestDocumentationResultHandler documentPrettyPrintReqResp(String useCase) {
    return document(useCase,
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()));
  }
}
