package ru.itmo.credithistory.dataservice.grpc.configuration;

import io.grpc.Channel;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import ru.itmo.credithistory.dataservice.grpc.DataServiceGrpc;

@AutoConfiguration
@ConditionalOnMissingBean(DataServiceGrpc.DataServiceImplBase.class)
@NullMarked
public class DataServiceGrpcClientAutoConfiguration {

  @Bean
  public DataServiceGrpc.DataServiceBlockingStub dataServiceBlockingStub(
      GrpcChannelFactory channelFactory) {
    Channel channel = channelFactory.createChannel("data-service");
    return DataServiceGrpc.newBlockingStub(channel);
  }
}
