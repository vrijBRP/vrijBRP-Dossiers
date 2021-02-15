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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import nl.procura.burgerzaken.dossiers.api.admin.v1.resources.AdminDossierResourceV1;
import nl.procura.burgerzaken.dossiers.api.external.v1.resources.DossierResourceV1;
import nl.procura.burgerzaken.dossiers.components.RequestLogFilter;
import nl.procura.burgerzaken.dossiers.model.error.ApiErrorType;
import nl.procura.burgerzaken.dossiers.model.support.Version;
import nl.procura.burgerzaken.dossiers.service.LoggingService;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@EnableAutoConfiguration
public class RestConfig {

  private static final String  OAUTH_SECURITY_SCHEME = "oAuth2ClientCredentials";
  private final Version        version;
  private final ServletContext servletContext;

  @Value("${openapi.oauth.token.url:}")
  private String openApiAuthTokenUrl;

  @Value("${openapi.server.url:}")
  private String openApiServerUrl;

  @Value("${springdoc.pathsToMatch}")
  private String springdocPathsToMatch;

  @Value("${develop.rest.show.null:false}")
  private boolean developRestShowNull;

  public RestConfig(Version version, ServletContext servletContext) {
    this.version = version;
    this.servletContext = servletContext;
  }

  private static Map<String, ApiResponse> getResponses() {
    Map<String, ApiResponse> responseMap = new HashMap<>();
    for (ApiErrorType errorType : ApiErrorType.values()) {
      if (errorType.isAddToResponse()) {
        ApiErrorResponse response = new ApiErrorResponse(errorType);
        responseMap.put(errorType.getCode(), response);
      }
    }
    return responseMap;
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module()); // (de)serialization of Optional
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

    if (!developRestShowNull) {
      // Useful during dev to show null values. Default is false
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    JavaTimeModule module = new JavaTimeModule();
    module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
    module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
    mapper.registerModule(module);

    mapper.setVisibility(mapper.getSerializationConfig()
        .getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

    return mapper;
  }

  @Bean
  public FilterRegistrationBean<RequestLogFilter> requestLogFilter(@Autowired LoggingService loggingService) {
    FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new RequestLogFilter(loggingService));
    registrationBean.addUrlPatterns("/api/*");
    return registrationBean;
  }

  @Bean
  public GroupedOpenApi openApiPublicGroup() {
    if (new AntPathMatcher().match(springdocPathsToMatch, "/api")) {
      return GroupedOpenApi.builder()
          .setGroup("public-API")
          .packagesToScan(DossierResourceV1.class.getPackageName())
          .addOpenApiCustomiser(openApi -> {
            openApi.getInfo().setTitle("Burgerzaken Dossiers Public API");
            openApi.getInfo().setDescription("Public API for external consumers");
            openApi.addSecurityItem(new SecurityRequirement().addList(OAUTH_SECURITY_SCHEME));
            openApi.getComponents().addSecuritySchemes(OAUTH_SECURITY_SCHEME,
                getOauthSecurityScheme("api", "Actions related to the public API"));
          })
          .build();
    }
    return null;
  }

  @Bean
  public GroupedOpenApi openApiAdminGroup() {
    if (new AntPathMatcher().match(springdocPathsToMatch, "/admin/api")) {
      return GroupedOpenApi.builder()
          .setGroup("admin-API")
          .packagesToScan(AdminDossierResourceV1.class.getPackageName())
          .addOpenApiCustomiser(openApi -> {
            openApi.getInfo().setTitle("Burgerzaken Dossiers Admin API");
            openApi.getInfo().setDescription("Administrator API for internal use");
            openApi.addSecurityItem(new SecurityRequirement().addList(OAUTH_SECURITY_SCHEME));
            openApi.getComponents().addSecuritySchemes(OAUTH_SECURITY_SCHEME,
                getOauthSecurityScheme("admin", "Actions related to the admin API"));
          })
          .build();
    }
    return null;
  }

  @Bean
  public OpenAPI customOpenAPI() {
    Components components = new Components();
    components.setResponses(getResponses());

    //  Disabled for now. This no longer worked from version 1.2.31 to current (1.3.9)
    //
    //  components.schemas(getSchemas(asList(ApiError.class,
    //      ApiErrorCause.class,
    //      ApiErrorRequest.class,
    //      ApiErrorHttpStatus.class)));

    OpenAPI info = new OpenAPI()
        .openapi("3.0.0")
        .components(components)
        .info(new Info().title("Burgerzaken Dossiers API")
            .version(version.version())
            .description("Burgerzaken Dossiers API")
            .contact(new Contact().name("Procura BV")
                .email("burgerzaken@procura.nl")
                .url("https://www.procura.nl")));

    if (StringUtils.isNotBlank(openApiServerUrl)) {
      info.addServersItem(new Server().url(openApiServerUrl));
    }

    return info;
  }

  private SecurityScheme getOauthSecurityScheme(String scope, String scopeDescription) {

    String tokenUrl = openApiAuthTokenUrl;
    if (StringUtils.isBlank(openApiAuthTokenUrl)) {
      tokenUrl = servletContext.getContextPath() + "/oauth/token";
    }

    return new SecurityScheme()
        .type(SecurityScheme.Type.OAUTH2)
        .description("This API uses OAuth 2 with the Client Credentials flow. " +
            "[More info](https://www.oauth.com/oauth2-servers/access-tokens/client-credentials)")
        .flows(new OAuthFlows()
            .clientCredentials(new OAuthFlow()
                .tokenUrl(tokenUrl)
                .scopes(new Scopes()
                    .addString(scope, scopeDescription))));
  }
}
