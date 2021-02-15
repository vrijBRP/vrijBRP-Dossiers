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

import java.util.Base64;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

@Component
public class JwtConfig {

  private static final int RSA_KEY_SIZE = 2048;

  private final RSAKey rsaJwk;

  public JwtConfig(OAuth2ResourceServerProperties resourceServerProperties) {
    try {
      rsaJwk = new RSAKeyGenerator(RSA_KEY_SIZE)
          .generate();
    } catch (JOSEException e) {
      throw new IllegalStateException(e);
    }
    try {
      byte[] encode = Base64.getMimeEncoder().encode(rsaJwk.toPublicKey().getEncoded());
      resourceServerProperties.getJwt().setPublicKeyLocation(new ByteArrayResource(encode));
    } catch (JOSEException e) {
      throw new IllegalStateException(e);
    }
  }

  public RSAKey rsaJwk() {
    return rsaJwk;
  }
}
