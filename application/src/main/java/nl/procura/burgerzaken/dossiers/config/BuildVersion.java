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

import static java.time.ZoneId.systemDefault;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import nl.procura.burgerzaken.dossiers.model.support.Version;

@Component
public class BuildVersion implements Version {

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final Optional<BuildProperties> buildProperties;

  public BuildVersion(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<BuildProperties> buildProperties) {
    this.buildProperties = buildProperties;
  }

  @Override
  public String version() {
    return buildProperties
        .map(BuildProperties::getVersion)
        .orElse("development");
  }

  @Override
  public String time() {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm");
    return buildProperties
        .map(b -> LocalDateTime.ofInstant(b.getTime(), systemDefault()).format(format))
        .orElse("unknown");
  }
}
