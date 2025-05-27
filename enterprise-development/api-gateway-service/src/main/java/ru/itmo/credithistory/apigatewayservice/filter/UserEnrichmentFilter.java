package ru.itmo.credithistory.apigatewayservice.filter;

import io.grpc.StatusRuntimeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.itmo.credithistory.commons.grpc.GrpcStatusToHttpStatusMapper;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.userservice.grpc.GetUserByTokenRequest;
import ru.itmo.credithistory.userservice.grpc.UserServiceGrpc;

@Component
@RequiredArgsConstructor
@NullMarked
public class UserEnrichmentFilter implements GlobalFilter {

  private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String token = extractToken(exchange.getRequest());
    if (token == null) {
      return chain.filter(exchange);
    }

    return Mono.fromCallable(
            () -> {
              var request = GetUserByTokenRequest.newBuilder().setToken(token).build();
              var user = userServiceBlockingStub.getUserByToken(request);

              var mutatedRequest =
                  exchange
                      .getRequest()
                      .mutate()
                      .header(AuthorizationHeaders.USER_ID_HEADER, user.getId())
                      .header(AuthorizationHeaders.USER_ROLE_HEADER, user.getRole())
                      .header(
                          AuthorizationHeaders.CUSTOMER_INN_HEADER,
                          StringUtils.isNotBlank(user.getCustomerInn())
                              ? user.getCustomerInn()
                              : null)
                      .build();

              return exchange.mutate().request(mutatedRequest).build();
            })
        .flatMap(chain::filter)
        .onErrorResume(
            StatusRuntimeException.class,
            ex -> {
              HttpStatus httpStatus =
                  GrpcStatusToHttpStatusMapper.mapGrpcToHttpStatus(ex.getStatus());
              exchange.getResponse().setStatusCode(httpStatus);
              return exchange.getResponse().setComplete();
            })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private @Nullable String extractToken(ServerHttpRequest request) {
    List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");
    if (!authHeaders.isEmpty() && authHeaders.getFirst().startsWith("Bearer ")) {
      return authHeaders.getFirst().substring(7);
    }
    return null;
  }
}
