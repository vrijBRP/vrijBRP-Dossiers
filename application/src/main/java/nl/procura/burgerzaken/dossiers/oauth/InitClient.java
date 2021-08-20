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

package nl.procura.burgerzaken.dossiers.oauth;

import static java.util.Collections.singleton;

import org.springdoc.core.SpringDocConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import nl.procura.burgerzaken.dossiers.repositories.client.ClientRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Command line application to add OAuth clients with a given scope.
 * <p>
 * Usage:
 * <pre>
 * java -classpath app.jar -Dloader.main=nl.procura.burgerzaken.dossiers.oauth.InitClient org.springframework.boot.loader.PropertiesLauncher api
 * </pre>
 * <p>
 * {@link ClientRepository} should not be called from this package but this
 * script should actually be replaced by a safe webservice.
 */
@ComponentScan(basePackageClasses = InitClient.class)
@EnableJpaRepositories(basePackageClasses = { InitClient.class, ClientRepository.class })
@EntityScan(basePackageClasses = { InitClient.class, nl.procura.burgerzaken.dossiers.model.client.Client.class })
// exclude SpringDocConfiguration as it includes WebMvc
@EnableAutoConfiguration(exclude = SpringDocConfiguration.class)
@Slf4j
public class InitClient implements CommandLineRunner {

  private final ClientDetailsService service;
  private final ClientRepository     clientRepository;

  public InitClient(ClientDetailsService service, ClientRepository clientRepository) {
    this.service = service;
    this.clientRepository = clientRepository;
  }

  public static void main(String[] args) {
    System.setProperty("logging.level.root", "error");
    System.setProperty("logging.level." + InitClient.class.getName(), "info");
    new SpringApplicationBuilder(InitClient.class)
        .web(WebApplicationType.NONE)
        .run(args);
  }

  @Override
  public void run(String... args) {
    if (args.length != 5) {
      log.error("Usage: java {} <id> <scope> <customer> <application> <secret>", InitClient.class.getName());
      return;
    }
    String id = args[0];
    Scope scope = new Scope(args[1]);
    String customer = args[2];
    String application = args[3];
    String secret = args[4];
    Client oauthClient = ClientDetailsService.newClient(id, secret, singleton(scope));
    service.createOrRegeneratePassword(oauthClient);
    clientRepository.save(new nl.procura.burgerzaken.dossiers.model.client.Client(id, customer, application));
    log.info("Created client\nid: {}\nsecret: {}\nscope: {}\nCustomer: {}\nApplication: {}", oauthClient.clientId(),
        oauthClient.rawSecret(), scope.scope(), customer, application);
  }
}
