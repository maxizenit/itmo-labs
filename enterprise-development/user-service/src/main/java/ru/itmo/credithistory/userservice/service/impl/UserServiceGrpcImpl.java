package ru.itmo.credithistory.userservice.service.impl;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.grpc.*;
import ru.itmo.credithistory.userservice.mapper.CreditOrganizationMapper;
import ru.itmo.credithistory.userservice.service.CreditOrganizationService;
import ru.itmo.credithistory.userservice.service.CustomerService;
import ru.itmo.credithistory.userservice.service.UserService;

@Service
@RequiredArgsConstructor
@NullMarked
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

  private final UserService userService;
  private final CreditOrganizationService creditOrganizationService;
  private final CustomerService customerService;
  private final CreditOrganizationMapper creditOrganizationMapper;

  @Override
  public void getUserByToken(
      GetUserByTokenRequest request, StreamObserver<GetUserByTokenResponse> responseObserver) {
    try {
      User user = userService.getUserByToken(request.getToken());
      String customerInn =
          UserRole.CUSTOMER.equals(user.getRole())
              ? customerService.getCustomerByUserIdInternal(user.getId()).getInn()
              : "";

      GetUserByTokenResponse response =
          GetUserByTokenResponse.newBuilder()
              .setId(user.getId().toString())
              .setRole(user.getRole().toString())
              .setCustomerInn(customerInn)
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(Status.UNAUTHENTICATED.withCause(e).asRuntimeException());
    }
  }

  @Override
  public void getCreditOrganizationsByIds(
      GetCreditOrganizationsByIdsRequest request,
      StreamObserver<GetCreditOrganizationsByIdsResponse> responseObserver) {
    List<UUID> ids = request.getIdsList().stream().map(UUID::fromString).toList();
    List<CreditOrganization> creditOrganizations =
        creditOrganizationService.getCreditOrganizationsByIds(ids);

    GetCreditOrganizationsByIdsResponse response =
        GetCreditOrganizationsByIdsResponse.newBuilder()
            .addAllCreditOrganizations(
                creditOrganizations.stream()
                    .map(creditOrganizationMapper::fromEntityToCreditOrganizationGrpc)
                    .toList())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
