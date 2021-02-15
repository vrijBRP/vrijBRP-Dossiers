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

package nl.procura.burgerzaken.dossiers.components;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import nl.procura.burgerzaken.dossiers.model.request.RequestLog;
import nl.procura.burgerzaken.dossiers.service.LoggingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLogFilter extends OncePerRequestFilter implements Ordered {

  private static final int               DEFAULT_MAX_PAYLOAD_LENGTH = 1024;
  private static final ThreadLocal<Long> requestBeginTime           = new ThreadLocal<>();

  private final int            maxPayloadLength;
  private final LoggingService loggingService;

  public RequestLogFilter(LoggingService loggingService) {
    this.loggingService = loggingService;
    maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;
  }

  @Override
  public int getOrder() {
    // Not LOWEST_PRECEDENCE, but near the end, so it has a good chance of catching all
    // enriched headers, but users can add stuff after this if they want to
    return Ordered.LOWEST_PRECEDENCE - 10;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    boolean isFirstRequest = !isAsyncDispatch(request);
    HttpServletRequest requestToUse = request;

    if (isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
      requestToUse = new ContentCachingRequestWrapper(request, maxPayloadLength);
    }

    HttpServletResponse responseToUse = response;
    if (!(response instanceof ContentCachingResponseWrapper)) {
      responseToUse = new ContentCachingResponseWrapper(response);
    }

    requestBeginTime.set(System.currentTimeMillis());

    try {
      filterChain.doFilter(requestToUse, responseToUse);
    } finally {
      if (!isAsyncStarted(requestToUse)) {
        logRequest(createMessage(requestToUse, responseToUse));
      }
    }
  }

  protected RequestLog createMessage(HttpServletRequest request, HttpServletResponse resp) {
    return new RequestLog()
        .setRequestId(loggingService.getRequestVariables().get().getRequestId())
        .setSessionId(Objects.toString(request.getSession().getId(), ""))
        .setMethod(Objects.toString(request.getMethod(), ""))
        .setUri(Objects.toString(request.getRequestURI(), ""))
        .setQueryString(Objects.toString(request.getQueryString(), ""))
        .setClient(Objects.toString(request.getRemoteAddr(), ""))
        .setUser(Objects.toString(request.getRemoteUser(), ""))
        .setHeaders(new ServletServerHttpRequest(request).getHeaders())
        .setRequest(getRequestPayload(request))
        .setResponse(getResponsePayload(resp))
        .setDuration(getDuration());
  }

  protected void logRequest(RequestLog logMessage) {
    loggingService.logRequest(logMessage);
  }

  private static long getDuration() {
    long begin = requestBeginTime.get();
    long end = System.currentTimeMillis();
    return (end - begin);
  }

  private String getRequestPayload(HttpServletRequest req) {
    ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(req,
        ContentCachingRequestWrapper.class);
    String payload = "";
    if (wrapper != null) {
      byte[] buf = wrapper.getContentAsByteArray();
      if (buf.length > 0) {
        int length = Math.min(buf.length, maxPayloadLength);
        try {
          payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException ex) {
          logger.error(ex.getMessage(), ex);
          payload = "[unknown]";
        }
      }
    }
    return payload;
  }

  private String getResponsePayload(HttpServletResponse resp) {
    ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(resp,
        ContentCachingResponseWrapper.class);
    String payload = "";
    if (responseWrapper != null) {
      byte[] buf = responseWrapper.getContentAsByteArray();
      try {
        responseWrapper.copyBodyToResponse();
      } catch (IOException e) {
        log.error("Fail to write response body back", e);
      }
      if (buf.length > 0) {
        try {
          payload = new String(buf, 0, buf.length, responseWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException ex) {
          logger.error(ex.getMessage(), ex);
          payload = "[unknown]";
        }
      }
    }
    return payload;
  }
}
