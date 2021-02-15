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

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class InitClientTest {

  @Test
  void initClientMustCreateAnApiClient() throws IOException {
    // when
    List<String> output = exec(InitClient.class, asList("test", "api", "test-customer", "test-application"));
    // then
    assertOutputContains("id: ", output);
    assertOutputContains("secret: ", output);
    // cannot check if client exists as it's been created in a different instance of the memory database
  }

  private static List<String> exec(Class<?> type, List<String> arguments) throws IOException {
    Path javaPath = Paths.get(System.getProperty("java.home"), "bin", "java.exe");
    if (!Files.exists(javaPath)) {
      // try Linux executable
      javaPath = Paths.get(System.getProperty("java.home"), "bin", "java");
    }
    String classPath = System.getProperty("java.class.path");
    ArrayList<String> command = new ArrayList<>();
    command.add(javaPath.toString());
    command.add("-classpath");
    command.add(classPath);
    command.add(type.getName());
    command.addAll(arguments);
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), UTF_8));
    String line;
    List<String> output = new ArrayList<>();
    while ((line = stdout.readLine()) != null) {
      output.add(line);
      log.info(line);
    }
    int code;
    try {
      code = process.waitFor();
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
    if (code != 0) {
      throw new IllegalStateException(format("Process has been exited with code %d", code));
    }
    return output;
  }

  private static void assertOutputContains(String expected, List<String> actual) {
    assertTrue(actual.stream().anyMatch(s -> s.startsWith(expected)));
  }

}
