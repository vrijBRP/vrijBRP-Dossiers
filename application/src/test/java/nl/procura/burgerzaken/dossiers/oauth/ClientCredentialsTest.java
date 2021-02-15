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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ClientCredentialsTest {

  @ParameterizedTest
  @MethodSource("provideOfArguments")
  void ofMustReturnEmptyWhenOneOfParametersIsNull(String principal, String secret, boolean expected) {
    // when
    ClientCredentials credentials = ClientCredentials.of(principal, secret);
    // then
    assertEquals(expected, credentials.id().isEmpty());
    assertEquals(expected, credentials.secret().isEmpty());
  }

  private static Stream<Arguments> provideOfArguments() {
    return Stream.of(
        Arguments.of(null, null, true),
        Arguments.of(null, "secret2", true),
        Arguments.of("client3", null, true),
        Arguments.of("client4", "secret4", false));
  }
}
