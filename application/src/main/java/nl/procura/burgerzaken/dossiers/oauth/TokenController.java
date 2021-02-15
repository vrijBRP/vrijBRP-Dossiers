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

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import nl.procura.burgerzaken.dossiers.config.JwtConfig;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * See https://tools.ietf.org/html/rfc6749#section-4.4
 */
@Controller
@Hidden
@RequestMapping("/oauth")
@ConditionalOnWebApplication
public class TokenController {

  private static final int SECOND_MS = 1000;
  private static final int EXPIRE_MS = 60 * SECOND_MS;

  private final JwtConfig            jwtConfig;
  private final ClientDetailsService service;

  public TokenController(JwtConfig jwtConfig, ClientDetailsService service) {
    this.jwtConfig = jwtConfig;
    this.service = service;
  }

  private static String generateToken(String issuer, String clientId, RSAKey key, Set<Scope> scopes, Date expireTime) {
    JWSSigner signer;
    try {
      signer = new RSASSASigner(key);
    } catch (JOSEException e) {
      throw new IllegalStateException(e);
    }

    // Prepare JWT with claims set
    String scope = scopes.stream()
        .map(Scope::scope)
        .collect(joining(" "));
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(clientId)
        .issuer(issuer)
        .claim("scope", scope)
        .expirationTime(expireTime)
        .build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);

    try {
      signedJWT.sign(signer);
    } catch (JOSEException e) {
      throw new IllegalStateException(e);
    }

    return signedJWT.serialize();
  }

  private static ClientCredentials getClientCredentials(HttpServletRequest request) {
    // get client_id and client_secret from parameters
    String clientId = request.getParameter("client_id");
    String clientSecret = request.getParameter("client_secret");

    if (StringUtils.isNoneBlank(clientId, clientSecret)) {
      return ClientCredentials.of(clientId, clientSecret);
    }

    // get authentication header
    BasicAuthenticationConverter converter = new BasicAuthenticationConverter(new WebAuthenticationDetailsSource());
    UsernamePasswordAuthenticationToken authentication = converter.convert(request);
    if (authentication == null) {
      return ClientCredentials.empty();
    }
    return ClientCredentials.of(authentication.getPrincipal(), authentication.getCredentials());
  }

  private static ResponseEntity<Response> badRequest(String error) {
    return new ResponseEntity<>(new ErrorResponse(error), headers(), BAD_REQUEST);
  }

  private static ResponseEntity<Response> unauthorized(String error) {
    HttpHeaders headers = headers();
    headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
    return new ResponseEntity<>(new ErrorResponse(error), headers, HttpStatus.UNAUTHORIZED);
  }

  private static ResponseEntity<Response> bearerToken(String accessToken, Date expireTime) {
    long expiresIn = (expireTime.getTime() - System.currentTimeMillis()) / SECOND_MS;
    return new ResponseEntity<>(new TokenResponse(accessToken, "bearer", expiresIn), headers(), HttpStatus.OK);
  }

  private static HttpHeaders headers() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
    headers.set(HttpHeaders.PRAGMA, "no-cache");
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

  @PostMapping("/token")
  public ResponseEntity<Response> token(HttpServletRequest request,
      @RequestParam("grant_type") String grantType,
      @RequestParam(name = "scope", required = false) String scope) {
    Optional<Grants> optionalGrant = Grants.ofSingle(grantType);
    if (optionalGrant.isEmpty() || optionalGrant.get() != Grants.CLIENT_CREDENTIALS) {
      return badRequest("unsupported_grant_type");
    }
    ClientCredentials credentials = getClientCredentials(request);
    Optional<Client> optionalClient = service.validateCredentials(credentials);
    if (optionalClient.isEmpty()) {
      return unauthorized("invalid_client");
    }

    Set<Scope> requestedScopes = Scope.of(scope);
    Set<Scope> clientScopes = optionalClient.map(Client::scopes).get();
    if (!clientScopes.containsAll(requestedScopes)) {
      return unauthorized("invalid_scope");
    }

    if (!optionalClient.get().grants().contains(optionalGrant.get())) {
      return unauthorized("invalid_client");
    }

    String issuer = request.getScheme() + "://" + request.getServerName();
    Date expireTime = new Date(new Date().getTime() + EXPIRE_MS);
    String jwtToken = generateToken(issuer, credentials.id(), jwtConfig.rsaJwk(), requestedScopes, expireTime);

    // scope is ignored now
    return bearerToken(jwtToken, expireTime);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> exception(MissingServletRequestParameterException e) {
    return new ResponseEntity<>(new ErrorResponse("invalid_request", e.getMessage()), headers(), BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> exception(Exception e) {
    return new ResponseEntity<>(new ErrorResponse("invalid_request", e.getMessage()), headers(), BAD_REQUEST);
  }

  interface Response {
  }

  @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ErrorResponse implements Response {

    public final String error;
    public final String errorDescription;
    public final String errorUri = null;

    public ErrorResponse(String error) {
      this.error = error;
      this.errorDescription = null;
    }

    public ErrorResponse(String error, String errorDescription) {
      this.error = error;
      this.errorDescription = errorDescription;
    }
  }

  @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class TokenResponse implements Response {

    public final String accessToken;
    public final String tokenType;
    public final Long   expiresIn;

    public TokenResponse(String accessToken, String tokenType, long expiresIn) {
      this.accessToken = accessToken;
      this.tokenType = tokenType;
      this.expiresIn = expiresIn;
    }
  }
}
