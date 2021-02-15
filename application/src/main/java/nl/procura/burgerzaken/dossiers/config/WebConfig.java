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

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import nl.procura.burgerzaken.dossiers.components.GbaClient;
import nl.procura.burgerzaken.dossiers.components.GbaClientConfig;
import nl.procura.burgerzaken.dossiers.components.GbaWsPersonListClient;
import nl.procura.burgerzaken.dossiers.components.GbaWsPersonListClientConfig;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(
        "/webjars/**",
        "/public/img/**",
        "/public/css/**",
        "/public/js/**")
        .addResourceLocations(
            "classpath:/META-INF/resources/webjars/",
            "classpath:/static/public/img/",
            "classpath:/static/public/css/",
            "classpath:/static/public/js/");
  }

  @Bean
  @RequestScope
  public GbaWsPersonListClient gbaWsPersonListClient(PersonRecordSourceConfig config, HttpServletRequest request) {
    GbaWsPersonListClientConfig clientConfig = GbaWsPersonListClientConfig.builder()
        .baseUrl(config.getUrl())
        .username(config.getUsername())
        .password(config.getPassword())
        .authorization(request.getHeader(HttpHeaders.AUTHORIZATION))
        .build();
    return new GbaWsPersonListClient(clientConfig);
  }

  @Bean
  @RequestScope
  public GbaClient gbaClient(GbaSourceConfig config, HttpServletRequest request) {
    GbaClientConfig clientConfig = GbaClientConfig.builder()
        .baseUrl(config.getUrl())
        .username(config.getUsername())
        .password(config.getPassword())
        .authorization(request.getHeader(HttpHeaders.AUTHORIZATION))
        .timeoutSeconds(config.getTimeoutSeconds())
        .build();
    return new GbaClient(clientConfig);
  }
}
