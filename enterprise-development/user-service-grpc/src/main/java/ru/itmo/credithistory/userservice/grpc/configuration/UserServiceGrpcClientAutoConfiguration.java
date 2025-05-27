package ru.itmo.credithistory.userservice.grpc.configuration;

import io.grpc.Channel;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import ru.itmo.credithistory.userservice.grpc.UserServiceGrpc;

@AutoConfiguration
@ConditionalOnMissingBean(UserServiceGrpc.UserServiceImplBase.class)
@NullMarked
public class UserServiceGrpcClientAutoConfiguration {

  @Bean
  public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(
      GrpcChannelFactory channelFactory) {
    Channel channel = channelFactory.createChannel("user-service");
    return UserServiceGrpc.newBlockingStub(channel);
  }
}
